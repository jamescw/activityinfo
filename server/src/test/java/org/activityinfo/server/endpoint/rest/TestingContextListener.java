package org.activityinfo.server.endpoint.rest;


import com.google.common.base.Preconditions;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.servlet.GuiceServletContextListener;
import com.sun.jersey.guice.JerseyServletModule;
import org.activityinfo.fixtures.MockHibernateModule;
import org.activityinfo.server.database.ServerDatabaseModule;
import org.activityinfo.server.database.hibernate.HibernateModule;

import javax.servlet.ServletContextEvent;
import java.util.logging.Logger;


public class TestingContextListener extends GuiceServletContextListener {

    public static final ThreadLocal<Injector> INJECTOR = new ThreadLocal<>();

    private static final Logger LOGGER = Logger.getLogger(TestingContextListener.class.getName());

    @Override
    protected Injector getInjector() {
        return Guice.createInjector(new TestingJerseyServletModule());
    }

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        Preconditions.checkState(INJECTOR.get() == null,
            "Injector already exists. ServletContextListener.contextDestroyed was not invoked?");

        super.contextInitialized(servletContextEvent);

        Injector injector = (Injector) servletContextEvent
                .getServletContext()
                .getAttribute(Injector.class.getName());

        if (injector == null) {
            throw new IllegalStateException("Injector is not set.");
        }
        INJECTOR.set(injector);
    }


    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        super.contextDestroyed(servletContextEvent);
        INJECTOR.remove();
    }
}