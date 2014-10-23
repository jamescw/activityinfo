package org.activityinfo.server.command;

/*
 * #%L
 * ActivityInfo Server
 * %%
 * Copyright (C) 2009 - 2013 UNICEF
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the 
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public 
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

import com.bedatadriven.rebar.sql.server.jdbc.JdbcScheduler;
import com.bedatadriven.rebar.time.calendar.LocalDate;
import org.activityinfo.fixtures.InjectionSupport;
import org.activityinfo.legacy.shared.command.GetSchema;
import org.activityinfo.legacy.shared.exception.CommandException;
import org.activityinfo.legacy.shared.model.*;
import org.activityinfo.server.endpoint.rest.SchemaCsvWriter;
import org.activityinfo.store.test.OnDataSet;
import org.activityinfo.store.test.TestResourceStore;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

@RunWith(InjectionSupport.class)
@OnDataSet("/dbunit/sites-simple1.db.xml")
public class GetSchemaTest extends CommandTestCase2 {

    @Rule
    public TestResourceStore store = new TestResourceStore();

    @Before
    public void cleanUpScheduler() {
        JdbcScheduler.get().forceCleanup();
    }

    @Test
    public void testDatabaseVisibilityForOwners() throws CommandException {

        // owners should be able to see their databases

        store.setUser(1); // Alex

        SchemaDTO schema = execute(new GetSchema());

        assertThat("database count", schema.getDatabases(), hasSize(3));
        assertThat("database list is sorted", schema.getDatabases().get(0).getName(), equalTo("Alpha"));

        assertTrue("ALEX(owner) in PEAR", schema.getDatabaseById(1) != null); // PEAR
        assertTrue("ALEX can design", schema.getDatabaseById(1).isDesignAllowed());
        assertTrue("Alex can edit all", schema.getDatabaseById(1).isEditAllowed());
        assertTrue("object graph is preserved",
                schema.getDatabaseById(1).getCountry() ==
                schema.getDatabaseById(2).getCountry());


        ActivityDTO nfi = schema.getDatabaseById(1).getActivities().get(0);

        assertThat("object graph is preserved (database-activity)",
                schema.getDatabaseById(1), is(nfi.getDatabase()));
        assertThat(nfi.getLocationTypeId(), equalTo(1));
        assertThat(nfi.<Integer>get("locationTypeId"), equalTo(1));

        AdminLevelDTO adminLevel = schema.getCountries().get(0).getAdminLevels().get(0);
        assertThat("CountryId is not null", adminLevel.getCountryId(), not(equalTo(0)));
        assertThat("CountryId is not null", adminLevel.getId(), not(equalTo(0)));

        assertTrue("CountryId is not null",
                schema.getCountries().get(0).getAdminLevels().get(0).getCountryId() != 0);

        assertThat("deleted attribute is not present",
                schema.getActivityById(1).getAttributeGroups().size(), equalTo(3));
    }

    @Test
    @OnDataSet("/dbunit/sites-public.db.xml")
    public void testDatabasePublished() throws CommandException {

        // Anonymouse user should fetch schema database with published
        // activities.
        store.setUser(0);

        SchemaDTO schema = execute(new GetSchema());

        assertThat(schema.getDatabases().size(), equalTo(1));
    }

    @Test
    public void testLockedProjects() {
        store.setUser(1);
        SchemaDTO schema = execute(new GetSchema());

        assertThat(schema.getProjectById(1).getLockedPeriods().size(), equalTo(1));

        LockedPeriodSet locks = new LockedPeriodSet(schema);
        assertTrue(locks.isProjectLocked(1, new LocalDate(2009, 1, 1)));
        assertTrue(locks.isProjectLocked(1, new LocalDate(2009, 1, 6)));
        assertTrue(locks.isProjectLocked(1, new LocalDate(2009, 1, 12)));
        assertFalse(locks.isProjectLocked(1, new LocalDate(2008, 1, 12)));
        assertFalse(locks.isProjectLocked(1, new LocalDate(2010, 1, 12)));
    }

    @Test
    public void testDatabaseVisibilityForView() throws CommandException {

        store.setUser(2); // Bavon

        SchemaDTO schema = execute(new GetSchema());

        assertThat(schema.getDatabases().size(), equalTo(2));
        assertThat("BAVON in PEAR", schema.getDatabaseById(1), is(not(nullValue())));
        assertThat(schema.getDatabaseById(1).getMyPartnerId(), equalTo(1));
        assertThat(schema.getDatabaseById(1).isEditAllowed(), equalTo(true));
        assertThat(schema.getDatabaseById(1).isEditAllAllowed(), equalTo(false));
    }

    @Test
    public void testDatabaseVisibilityNone() throws CommandException {
        store.setUser(3); // Stefan

        SchemaDTO schema = execute(new GetSchema());

        assertThat("STEFAN does not have access to RRM", schema.getDatabases(),
                not(hasItem(Matchers.<UserDatabaseDTO>hasProperty("id", equalTo(2)))));
    }

    @Test
    public void testIndicators() throws CommandException {

        store.setUser(1); // Alex

        SchemaDTO schema = execute(new GetSchema());

        assertTrue("no indicators case", schema.getActivityById(2).getIndicators().size() == 0);

        ActivityDTO nfi = schema.getActivityById(1);

        assertThat("indicators are present", nfi.getIndicators().size(),
                equalTo(5));

        IndicatorDTO test = nfi.getIndicatorById(2);
        assertThat(test, Matchers.hasProperty("name", equalTo("baches")));
        assertThat(test, Matchers.hasProperty("aggregation", equalTo(IndicatorDTO.AGGREGATE_SUM)));
        assertThat(test, Matchers.hasProperty("category", equalTo("outputs")));
        assertThat(test, Matchers.hasProperty("listHeader", equalTo("header")));
        assertThat(test, Matchers.hasProperty("description", equalTo("desc")));
    }

    @Test
    public void testAttributes() throws CommandException {

        store.setUser(1); // Alex

        SchemaDTO schema = execute(new GetSchema());

        assertTrue("no attributes case", schema.getActivityById(3).getAttributeGroups().size() == 0);

        ActivityDTO nfi = schema.getActivityById(1);
        AttributeDTO[] attributes = nfi.getAttributeGroupById(1)
                .getAttributes().toArray(new AttributeDTO[0]);

        assertTrue("attributes are present", attributes.length == 2);

        AttributeDTO test = nfi.getAttributeById(1);

        assertEquals("property:name", "Catastrophe Naturelle", test.getName());
    }

    @Test
    public void toCSV() {
        SchemaDTO schema = execute(new GetSchema());

        SchemaCsvWriter writer = new SchemaCsvWriter();
        writer.write(schema.getDatabaseById(1));

        System.out.println(writer.toString());
    }

}
