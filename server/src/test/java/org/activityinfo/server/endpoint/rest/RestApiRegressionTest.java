package org.activityinfo.server.endpoint.rest;


import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.sun.jersey.api.client.ClientResponse;
import org.activityinfo.testGeneration.DbUnitDataset;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import javax.annotation.Nullable;
import javax.ws.rs.core.UriBuilder;
import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Objects;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static uk.co.datumedge.hamcrest.json.SameJSONAs.sameJSONAs;

/**
 * Runs all the test cases defined in {@code regression-tests-2.8.json}
 */
@RunWith(Parameterized.class)
public class RestApiRegressionTest {

    private static JerseyGuiceTest TEST_CONTAINER;
    private static String CURRENT_DATASET = null;

    private RestTestCase testCase;

    @BeforeClass
    public static void startContainer() throws Exception {
        TEST_CONTAINER = new JerseyGuiceTest();
        TEST_CONTAINER.setUp();
    }

    @Parameterized.Parameters
    public static Iterable<Object[]> loadCases() throws IOException {
        return Iterables.transform(RestTestSuite.loadCases().getCases(), new Function<RestTestCase, Object[]>() {
            @Nullable
            @Override
            public Object[] apply(RestTestCase input) {
                return new Object[] { input };
            }
        });
    }

    public RestApiRegressionTest(RestTestCase testCase) {
        this.testCase = testCase;
    }

    @Test
    public void test() throws Exception {
        if(!Objects.equals(testCase.getDataset(), CURRENT_DATASET)) {
            DbUnitDataset dataset = DbUnitDataset.byName(testCase.getDataset());
            dataset.loadDataSet();
            CURRENT_DATASET = testCase.getDataset();
        }

        URI uri = testCase.absoluteUri(TEST_CONTAINER.resource());

        ClientResponse response = TEST_CONTAINER.client()
                .resource(uri)
                .get(ClientResponse.class);

        assertThat(response.getStatus(), equalTo(testCase.getExpectedStatusCode()));
        if(testCase.getExpectedResponse() != null) {
            assertThat(response.getEntity(String.class), 
                    sameJSONAs(testCase.getExpectedResponse().toString()).allowingAnyArrayOrdering());
        }
    }

    @AfterClass
    public static void stopContainer() throws Exception {
        TEST_CONTAINER.tearDown();
    }
}
