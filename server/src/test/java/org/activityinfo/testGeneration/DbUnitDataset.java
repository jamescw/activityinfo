package org.activityinfo.testGeneration;


import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.io.Resources;
import org.activityinfo.legacy.shared.auth.AuthenticatedUser;
import org.activityinfo.server.database.DatabaseCleaner;
import org.activityinfo.server.database.TestConnectionProvider;
import org.apache.xerces.parsers.DOMParser;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.LowerCaseDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.ext.mssql.InsertIdentityOperation;
import org.dbunit.ext.mysql.MySqlConnection;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;
import java.util.regex.Pattern;

/**
 * Describes a DbUnit (*.db.xml) data set
 */
public class DbUnitDataset {

    private static final Logger LOGGER = Logger.getLogger(DbUnitDataset.class.getName());

    private File file;

    private Set<AuthenticatedUser> users = Sets.newHashSet();

    private static final Pattern PRIMARY_KEY_PATTERN = Pattern.compile("\\d{1,10}");
    private Document document;

    public DbUnitDataset(File file) throws IOException, SAXException {
        this.file = file;
    }

    public Set<String> columnValues(String columnName)  {
        try {
            if(document == null) {
                DOMParser parser = new DOMParser();
                parser.parse(file.getAbsolutePath());

                document = parser.getDocument();
            }

            // a db.xml file is completely flat, with each row an XML tag
            NodeList nodeList = document.getDocumentElement().getChildNodes();

            Set<String> matching = new HashSet<>();

            for (int i = 0; i != nodeList.getLength(); ++i) {
                Node node = nodeList.item(i);
                if (node instanceof Element) {
                    Element element = (Element) node;
                    NamedNodeMap attrs = element.getAttributes();
                    for(int j=0;j!=attrs.getLength();++j) {
                        if(attrs.item(j).getNodeName().equalsIgnoreCase(columnName)) {
                            matching.add(attrs.item(j).getNodeValue());
                        }
                    }
                }
            }
            return matching;
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Loads the db.xml into the testing database.
     */
    public void loadDataSet() throws Exception {

        LOGGER.info("Loadig " + file);

        // Remove all rows from the test database
        DatabaseCleaner cleaner = new DatabaseCleaner(new TestConnectionProvider());
        cleaner.clean();

        try (Connection connection = new TestConnectionProvider().get()) {
            IDatabaseConnection dbUnitConnection = new MySqlConnection(connection, null);
            LowerCaseDataSet dataSet = new LowerCaseDataSet(new FlatXmlDataSetBuilder()
                    .setDtdMetadata(true)
                    .setColumnSensing(true)
                    .build(file));

            InsertIdentityOperation.INSERT.execute(dbUnitConnection, dataSet);
            dbUnitConnection.close();
        }
    }

    public static DbUnitDataset byName(String name) throws IOException, SAXException {
        URL dataset = Resources.getResource("dbunit/" + name + ".db.xml");
        return new DbUnitDataset(new File(dataset.getFile()));
    }

    /**
     * Find all the db.xml files in the workspace.
     */
    public static List<DbUnitDataset> find() throws IOException, SAXException {

        List<DbUnitDataset> datasets = Lists.newArrayList();

        URL dataset = Resources.getResource("dbunit/sites-simple1.db.xml");

        File testDir = new File(dataset.getFile());

        File[] files = testDir.getParentFile().listFiles();
        if (files != null) {
            for (File testFile : files) {
                if(testFile.getName().endsWith(".db.xml")) {
                    datasets.add(new DbUnitDataset(testFile));
                }
            }
        }
        return datasets;
    }

    public String getFileName() {
        return file.getName();
    }

    public String getName() {
        if(file.getName().endsWith(".db.xml")) {
            return file.getName().substring(0, file.getName().length()-".db.xml".length());
        } else {
            return file.getName();
        }
    }

    public File getInputFile(String name) {
        return new File("src/test/resources/org/activityinfo/server/endpoint/rest/" + name + ".json");
    }

    public Set<AuthenticatedUser> getDefinedUsers() {
        return users;
    }
}
