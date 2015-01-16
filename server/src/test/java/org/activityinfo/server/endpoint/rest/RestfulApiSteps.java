package org.activityinfo.server.endpoint.rest;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import cucumber.api.java.Before;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.activityinfo.testGeneration.DbUnitDataset;
import uk.co.datumedge.hamcrest.json.SameJSONAs;

import javax.ws.rs.core.UriBuilder;
import java.net.URI;
import java.util.Objects;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static uk.co.datumedge.hamcrest.json.SameJSONAs.sameJSONAs;


public class RestfulApiSteps {

    public static final SameJSONAs EMPTY_LIST = SameJSONAs.sameJSONAs("[]");

    public static final SameJSONAs EMPTY_FEATURE_COLLECTION = SameJSONAs.sameJSONAs(
            "{'type' : 'FeatureCollection', 'features': [] }".replace('\'', '\"'));

    private static JerseyGuiceTest TEST_CONTAINER;
    private static String CURRENT_DATASET = null;
    private ClientResponse response;


    public static UriBuilder createUri(WebResource root, String relativeUri) {
        String[] parts = relativeUri.split("\\?");

        UriBuilder absoluteUri = root.getUriBuilder().replacePath(parts[0]);
        if(parts.length > 1) {
            absoluteUri.replaceQuery(parts[1]);
        }
        return absoluteUri;
    }

    private UriBuilder createUri(String relativeUri) {
        return createUri(TEST_CONTAINER.resource(), relativeUri);
    }

    @Before
    public void startupServer() throws Exception {
        if(TEST_CONTAINER == null) {
            TEST_CONTAINER = new JerseyGuiceTest();
            TEST_CONTAINER.setUp();
        }
    }


    @Given("^\"([^\"]+)\" is loaded$")
    public void is_loaded(String name) throws Throwable {
        if(!Objects.equals(name, CURRENT_DATASET)) {
            DbUnitDataset dataset = DbUnitDataset.byName(name);
            dataset.loadDataSet();
            CURRENT_DATASET = name;
        }
    }

    @When("^\"([^\"]*)\" requests \"([^\"]*)\"$")
    public void requests(String user, String uri) throws Throwable {
        URI absoluteUri = createUri(uri).userInfo(user + ":password").build();
        this.response = TEST_CONTAINER.client().resource(absoluteUri).get(ClientResponse.class);
    }

    @When("^an anonymous user requests \"(.*?)\"$")
    public void an_anonymous_user_requests(String uri) throws Throwable {
        this.response = TEST_CONTAINER.client()
                .resource(createUri(uri).build())
                .get(ClientResponse.class);
    }

    @Then("^the server should respond with an empty list$")
    public void the_server_responds_with() throws Throwable {
        assertThat(response.getStatus(), equalTo(200));
        assertThat(response.getEntity(String.class), EMPTY_LIST);
    }

    @Then("^the server respond should respond with:$")
    public void the_server_respond_should_respond_with(String json) throws Throwable {
        assertThat(response.getStatus(), equalTo(200));
        assertThat(response.getEntity(String.class), sameJSONAs(json));
    }

    @Then("^the server should respond with status code (\\d+)$")
    public void the_server_should_respond_with_status_code(int statusCode) throws Throwable {
        assertThat(response.getStatus(), equalTo(statusCode));
    }

    @Then("^the server should respond with an empty feature collection$")
    public void the_server_should_respond_with_an_empty_feature_collection() throws Throwable {
        assertThat(response.getStatus(), equalTo(200));
        assertThat(response.getEntity(String.class), EMPTY_FEATURE_COLLECTION);
    }
}
