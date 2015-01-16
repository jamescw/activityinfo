package org.activityinfo.server.endpoint.rest;

import com.sun.jersey.api.client.WebResource;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import javax.ws.rs.core.UriBuilder;
import java.net.URI;


public class RestTestCase {

    private String dataset;

    @JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
    private String user;

    private String uri;

    private int expectedStatusCode;


    @JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
    private JsonNode expectedResponse;

    @JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
    private String expectedResponseText;

    public String getDataset() {
        return dataset;
    }

    public void setDataset(String dataset) {
        this.dataset = dataset;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public int getExpectedStatusCode() {
        return expectedStatusCode;
    }

    public void setExpectedStatusCode(int expectedStatusCode) {
        this.expectedStatusCode = expectedStatusCode;
    }

    public JsonNode getExpectedResponse() {
        return expectedResponse;
    }

    public void setExpectedResponse(JsonNode expectedResponse) {
        this.expectedResponse = expectedResponse;
    }

    public String getExpectedResponseText() {
        return expectedResponseText;
    }

    public void setExpectedResponseText(String expectedResponseText) {
        this.expectedResponseText = expectedResponseText;
    }

    public URI absoluteUri(WebResource root) {
        String[] parts = this.uri.split("\\?");

        UriBuilder absoluteUri = root.getUriBuilder().replacePath(parts[0]);
        if(parts.length > 1) {
            absoluteUri.replaceQuery(parts[1]);
        }
        if(user != null) {
            // Server does not valid passwords in development mode
            absoluteUri.userInfo(user + ":password");
        }
        return absoluteUri.build();
    }
}
