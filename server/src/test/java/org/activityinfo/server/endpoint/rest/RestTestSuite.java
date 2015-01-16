package org.activityinfo.server.endpoint.rest;

import com.google.common.collect.Lists;
import com.google.common.io.Resources;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;

public class RestTestSuite {

    private final List<RestTestCase> cases = Lists.newArrayList();

    public List<RestTestCase> getCases() {
        return cases;
    }

    public void write() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        File file = testCaseFile();
        objectMapper.writerWithDefaultPrettyPrinter().writeValue(file, this);
    }

    private static File testCaseFile() {
        
        File sourceDir = new File("server/src/test/resources/" +
                RestApiRegressionTest.class.getPackage().getName().replace('.', '/'));

        if(!sourceDir.exists()) {
            throw new IllegalStateException("Source output dir does not exist: " + sourceDir.getAbsolutePath());
        }

        return new File(sourceDir, "regression-tests-2.8.155.json");
    }


    public static RestTestSuite loadCases() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        URL testCaseResource = Resources.getResource(RestTestSuite.class, "regression-tests-2.8.155.json");
        return mapper.readValue(testCaseResource, RestTestSuite.class);
    }
}
