package org.activityinfo.server.endpoint.rest;

import com.google.inject.servlet.GuiceFilter;
import com.sun.jersey.test.framework.JerseyTest;
import com.sun.jersey.test.framework.WebAppDescriptor;


public class JerseyGuiceTest extends JerseyTest {


    public JerseyGuiceTest() {
        super(new WebAppDescriptor.Builder()
                .filterClass(GuiceFilter.class)
                .contextListenerClass(TestingContextListener.class)
                .build());
    }


}
