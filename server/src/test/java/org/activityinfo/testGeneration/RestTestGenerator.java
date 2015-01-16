package org.activityinfo.testGeneration;

import com.google.common.base.Charsets;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterfaceException;
import org.activityinfo.server.endpoint.rest.*;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.ObjectMapper;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;
import java.io.*;
import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

/**
 * Generates systematic test cases to ensure that the public API remains backward
 * compatible with AI 2.8
 */
public class RestTestGenerator extends JerseyGuiceTest {

    private static final String ANONYMOUS = null;

    private DbUnitDataset currentDataset;
    private List<String> currentUsers;
    private String currentUser;

    private ObjectMapper objectMapper = new ObjectMapper();

    private RestTestSuite suite = new RestTestSuite();

    public static void main(String[] args) throws Exception {

        RestTestGenerator generate = new RestTestGenerator();
        generate.setUp();
        try {
            generate.generateTests();
        } finally {
            generate.tearDown();
        }
    }

    /**
     * Generate as many tests as possible by systematically running through
     * every db.xml file we have, and then trying every user declared in that file
     * against every GET end point, using the integers in the db.xml file as
     * a distribution from which to sample path and query parameters.
     *
     */
    public void generateTests() throws Exception {

        for(DbUnitDataset dataset : DbUnitDataset.find()) {
            load(dataset);

            as(ANONYMOUS);
            geographyApi();
            databaseApi();
            sitesApi();

            for(String user : each("email")) {
                as(user);
                databaseApi();
                sitesApi();
            }
        }
        suite.write();
    }

    private void geographyApi() throws Exception {
        get("/resources/countries");
        for(String countryId : each("countryId")) {
            get("/resources/country/%s/adminLevels", countryId);
            get("/resources/country/%s/locationTypes", countryId);
        }
        for(String countryCode : each("iso2")) {
            get("/resources/country/%s/adminLevels", countryCode);
            get("/resources/country/%s/locationTypes", countryCode);
        }
        for(String adminLevelId : each("adminLevelId")) {
            get("/resources/adminLevel/%s/entities", adminLevelId);
            get("/resources/adminLevel/%s/entities/features", adminLevelId);
        }
        for(String locationId : each("locationTypeId")) {
            get("/resources/locations?type=%s", locationId);
        }
    }

    private void databaseApi() throws Exception {
        get("/resources/databases");
        for (String databaseId : each("databaseId")) {
            get("/resources/database/%s/schema", databaseId);
            get("/responses/database/%s/schema.csv", databaseId);
        }
    }

    private void sitesApi() throws Exception {
        get("/resources/sites");

        for(String databaseId : each("databaseId")) {
            get("/resources/sites?database=%s", databaseId);
        }
        for(String activityId : each("activityId")) {
            get("/resources/sites?activity=%s", activityId);
            get("/resources/sites/points?activity=%s", activityId);

            // Filter on activity + partner
            for(String partnerId : each("partnerId")) {
                get("/resources/sites?activity=%s&partner=%s", activityId, partnerId);
            }
        }
    }

    /**
     * @return a sorted list of unique values from any column named {@code columnName} in the database.
     */
    private List<String> each(String columnName) {
        List<String> strings = Lists.newArrayList(currentDataset.columnValues(columnName));
        Collections.sort(strings);
        return strings;
    }

    /**
     * Sets the user making the request.
     *  
     * @param user the user's email. Should come from the dbunit xml file
     */
    private void as(String user) {
        this.currentUser = user;
    }

    /**
     * Loads the given DB Unit XML dataset into the database
     */
    private void load(DbUnitDataset dataset) throws Exception {
        dataset.loadDataSet();
        this.currentDataset = dataset;
        this.currentUsers = Lists.newArrayList(dataset.columnValues("email"));
        Collections.sort(currentUsers);
    }

    /**
     * Creates a test case for a GET request
     */
    private void get(String message, Object... args) {

        RestTestCase testCase = new RestTestCase();
        testCase.setDataset(currentDataset.getName());
        testCase.setUser(currentUser);
        testCase.setUri(String.format(message, args));
        
        URI uri = testCase.absoluteUri(this.resource());
        
        System.out.println(uri);

        ClientResponse clientResponse;
        try {
            clientResponse = client().resource(uri)
                    .accept(MediaType.APPLICATION_JSON_TYPE)
                    .get(ClientResponse.class);
        } catch(UniformInterfaceException e) {
            clientResponse = e.getResponse();
        }

        testCase.setExpectedStatusCode(clientResponse.getStatus());

        if(clientResponse.getStatus() == 200) {
            String json = clientResponse.getEntity(String.class);
            try {
                JsonNode node = objectMapper.readTree(json);
                testCase.setExpectedResponse(node);
            } catch (Exception e) {
                testCase.setExpectedResponseText(json);
            }
        }
        suite.getCases().add(testCase);
    }
}
