package org.activityinfo.model.type.subform;
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

import org.activityinfo.model.form.FormClass;
import org.activityinfo.model.form.FormField;
import org.activityinfo.model.resource.ResourceIdPrefixType;
import org.activityinfo.model.type.period.PeriodType;
import org.activityinfo.model.type.period.PeriodValue;
import org.activityinfo.model.type.period.PredefinedPeriods;

/**
 * @author yuriyz on 01/27/2015.
 */
public class PeriodSubFormKind implements SubFormKind {

    private final String id;
    private final String label;
    private final PeriodValue period;

    public PeriodSubFormKind(PredefinedPeriods predefinedPeriods) {
        this(predefinedPeriods.name(), predefinedPeriods.getPeriod(), predefinedPeriods.getLabel());
    }

    public PeriodSubFormKind(String id, PeriodValue period, String label) {
        this.id = id;
        this.period = period;
        this.label = label;
    }

    @Override
    public String getId() {
        return id;
    }

    public PeriodValue getPeriod() {
        return period;
    }

    @Override
    public FormClass getDefinition() {
        FormClass formClass = new FormClass(ResourceIdPrefixType.SUBFORM.id(getId()));
        formClass.setLabel(label);

        FormField formField = formClass.addField();
        formField.setType(new PeriodType());
        return formClass;
    }
}
