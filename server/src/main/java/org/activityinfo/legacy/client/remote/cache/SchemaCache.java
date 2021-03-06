package org.activityinfo.legacy.client.remote.cache;

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

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.inject.Inject;
import org.activityinfo.legacy.client.CommandCache;
import org.activityinfo.legacy.client.DispatchEventSource;
import org.activityinfo.legacy.client.DispatchListener;
import org.activityinfo.legacy.shared.command.*;
import org.activityinfo.legacy.shared.command.result.CommandResult;
import org.activityinfo.legacy.shared.model.ActivityDTO;
import org.activityinfo.legacy.shared.model.SchemaDTO;

import java.util.Map;
import java.util.Set;

/**
 * Caches the user's schema in-memory for the duration of the session.
 * <p/>
 * TODO: we need to peridiodically check the server for updates. Do we do this
 * here or in a separate class?
 *
 * @author Alex Bertram
 */
public class SchemaCache implements DispatchListener {

    private SchemaDTO schema = null;
    private Set<String> schemaEntityTypes = Sets.newHashSet();
    private Map<Integer, ActivityDTO> activityMap = Maps.newHashMap();
    

    @Inject
    public SchemaCache(DispatchEventSource source) {

        source.registerProxy(GetSchema.class, new SchemaProxy());
        source.registerProxy(GetFormViewModel.class, new FormViewProxy());
        source.registerListener(GetSchema.class, this);
        source.registerListener(UpdateEntity.class, this);
        source.registerListener(CreateEntity.class, this);
        source.registerListener(AddPartner.class, this);
        source.registerListener(RemovePartner.class, this);
        source.registerListener(RequestChange.class, this);
        source.registerListener(BatchCommand.class, this);
        source.registerListener(GetFormViewModel.class, this);
        source.registerListener(BatchCommand.class, this);

        schemaEntityTypes.add("UserDatabase");
        schemaEntityTypes.add("Activity");
        schemaEntityTypes.add("Indicator");
        schemaEntityTypes.add("AttributeGroup");
        schemaEntityTypes.add("AttributeDimension");
        schemaEntityTypes.add("Attribute");
        schemaEntityTypes.add("Partner");
        schemaEntityTypes.add("Project");
        schemaEntityTypes.add("LockedPeriod");
    }


    @Override
    public void beforeDispatched(Command command) {
        if (command instanceof UpdateEntity) {
            clearCache();
        } else if (command instanceof CreateEntity) {
            clearCache();
        } else if (command instanceof AddPartner || command instanceof RemovePartner) {
            clearCache();
        } else if (command instanceof RequestChange && isSchemaEntity(((RequestChange) command).getEntityType())) {
            clearCache();
        } else if (command instanceof BatchCommand) {
            for (Command element : ((BatchCommand) command).getCommands()) {
                beforeDispatched(element);
            }
        }
    }

    private void clearCache() {
        schema = null;
        activityMap.clear();
    }

    private boolean isSchemaEntity(String entityName) {
        return schemaEntityTypes.contains(entityName);
    }

    @Override
    public void onSuccess(Command command, CommandResult result) {
        if (command instanceof GetSchema) {
            cache((SchemaDTO) result);
        } else if (command instanceof GetFormViewModel) {
            ActivityDTO activity = (ActivityDTO) result;
            activityMap.put(activity.getId(), activity);
        } else if (schema != null) {
            if (command instanceof AddPartner) {
                clearCache();
            }
        }
    }

    /**
     * Caches the schema in-memory following a successful GetSchema call.
     * Subclasses can override this to provide a more permanent cache.
     *
     * @param schema The schema to cache
     */
    protected void cache(SchemaDTO schema) {
        this.schema = schema;
    }

    @Override
    public void onFailure(Command command, Throwable caught) {

    }


    private class FormViewProxy implements CommandCache<GetFormViewModel> {
        @Override
        public CacheResult maybeExecute(GetFormViewModel command) {
            int activityId = command.getActivityId();
            if(schema != null) {
                return new CacheResult<>(schema.getActivityById(activityId));
            } else if(activityMap.containsKey(activityId)) {
                return new CacheResult<>(activityMap.get(activityId));
            } else {
                return CacheResult.couldNotExecute();
            }
        }

        @Override
        public void clear() {
            clearCache();
        }
    }

    private class SchemaProxy implements CommandCache<GetSchema> {
        @Override
        public CacheResult maybeExecute(GetSchema command) {
            if (schema == null) {
                return CacheResult.couldNotExecute();
            } else {
                return new CacheResult<>(schema);
            }
        }

        @Override
        public void clear() {

        }
    }
}
