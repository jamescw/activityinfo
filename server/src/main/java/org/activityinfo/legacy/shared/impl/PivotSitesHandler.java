package org.activityinfo.legacy.shared.impl;

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

import com.bedatadriven.rebar.sql.client.query.SqlDialect;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import org.activityinfo.legacy.shared.command.DimensionType;
import org.activityinfo.legacy.shared.command.PivotSites;
import org.activityinfo.legacy.shared.command.PivotSites.PivotResult;
import org.activityinfo.legacy.shared.command.PivotSites.ValueType;
import org.activityinfo.legacy.shared.command.result.Bucket;
import org.activityinfo.legacy.shared.impl.pivot.*;
import org.activityinfo.legacy.shared.Log;

import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

public class PivotSitesHandler implements
        CommandHandlerAsync<PivotSites, PivotSites.PivotResult> {

    private final SqlDialect dialect;

    private static final Logger LOGGER = Logger
            .getLogger(PivotSitesHandler.class.getName());

    private List<BaseTable> baseTables = Lists.newArrayList();

    @Inject
    public PivotSitesHandler(SqlDialect dialect) {
        this.dialect = dialect;
        baseTables.add(new SumAvgIndicatorValues());
        baseTables.add(new CountIndicatorValues());
        baseTables.add(new LinkedSumAvgIndicatorValues());
        baseTables.add(new Targets());
        baseTables.add(new SiteCounts());
        baseTables.add(new LinkedSiteCounts());
        baseTables.add(new DimensionValues());
        baseTables.add(new LinkedDimensionValues());
    }

    @Override
    public void execute(PivotSites command, ExecutionContext context,
                        final AsyncCallback<PivotResult> callback) {

        LOGGER.fine("Pivoting: " + command);

        if (command.getFilter() == null
                ||
                command.getFilter().getRestrictions(DimensionType.Indicator)
                        .isEmpty()) {
            Log.error("No indicator filter provided to pivot query");
            PivotResult emptyResult = new PivotResult();
            emptyResult.setBuckets(Lists.<Bucket>newArrayList());
            callback.onSuccess(emptyResult);
            return;
        }

        final PivotQueryContext queryContext = new PivotQueryContext(command,
                context, dialect);
        final List<PivotQuery> queries = Lists.newArrayList();

        for (BaseTable baseTable : baseTables) {
            if (baseTable.accept(command)) {
                queries.add(new PivotQuery(queryContext, baseTable));
            }
        }

        final List<Bucket> buckets = Lists.newArrayList();
        if (queries.isEmpty()) {
            callback.onSuccess(new PivotResult(buckets));
        }

        final Set<PivotQuery> remaining = Sets.newHashSet(queries);
        final List<Throwable> errors = Lists.newArrayList();

        for (final PivotQuery query : queries) {
            query.execute(new AsyncCallback<Void>() {

                @Override
                public void onSuccess(Void voidResult) {
                    if (errors.isEmpty()) {
                        remaining.remove(query);
                        if (remaining.isEmpty()) {
                            callback.onSuccess(new PivotResult(queryContext
                                    .getBuckets()));
                        }
                    }
                }

                @Override
                public void onFailure(Throwable caught) {
                    if (errors.isEmpty()) {
                        callback.onFailure(caught);
                    }
                    errors.add(caught);
                }
            });
        }
    }

}
