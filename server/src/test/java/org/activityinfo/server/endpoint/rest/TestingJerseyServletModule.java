package org.activityinfo.server.endpoint.rest;

import com.google.inject.Provides;
import com.sun.jersey.guice.JerseyServletModule;
import com.sun.jersey.guice.spi.container.servlet.GuiceContainer;
import org.activityinfo.legacy.shared.auth.AuthenticatedUser;
import org.activityinfo.server.authentication.AuthenticationModule;
import org.activityinfo.server.database.ServerDatabaseModule;
import org.activityinfo.server.database.TestConnectionProvider;
import org.activityinfo.server.database.hibernate.HibernateModule;
import org.activityinfo.server.endpoint.jsonrpc.JsonRpcModule;
import org.activityinfo.server.endpoint.odk.ODKModule;
import org.activityinfo.server.geo.GeometryModule;
import org.activityinfo.server.util.TemplateModule;
import org.activityinfo.server.util.config.DeploymentConfiguration;
import org.activityinfo.server.util.jaxrs.JaxRsModule;
import org.activityinfo.server.util.locale.LocaleModule;

import javax.inject.Provider;
import java.util.Properties;

public class TestingJerseyServletModule extends JerseyServletModule {

    public static int currentUserId = 0;

    @Override
    protected void configureServlets() {
        install(new HibernateModule());
        install(new ServerDatabaseModule());
        install(new TemplateModule());
        install(new GeometryModule());
        install(new JsonRpcModule());
        install(new LocaleModule());
        install(new JaxRsModule());
        install(new RestApiModule());
        install(new AuthenticationModule());

        serve("/*").with(GuiceContainer.class);
    }

    @Provides
    public DeploymentConfiguration provideConfiguration() {
        Properties properties = new Properties();
        properties.setProperty("hibernate.connection.url", TestConnectionProvider.URL);
        properties.setProperty("hibernate.connection.username", TestConnectionProvider.USERNAME);
        properties.setProperty("hibernate.connection.password", TestConnectionProvider.PASSWORD);
        properties.setProperty("hibernate.dialect", "org.hibernate.spatial.dialect.mysql.MySQLSpatialInnoDBDialect");
        properties.setProperty("hibernate.ejb.naming_strategy", "org.activityinfo.server.database.hibernate.AINamingStrategy");
        properties.setProperty("hibernate.cache.use_query_cache", "false");
        properties.setProperty("hibernate.connection.pool_size", "0");


        return new DeploymentConfiguration(properties);
    }

}
