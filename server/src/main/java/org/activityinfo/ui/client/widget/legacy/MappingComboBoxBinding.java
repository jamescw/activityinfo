package org.activityinfo.ui.client.widget.legacy;

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

import com.extjs.gxt.ui.client.binding.Converter;
import com.extjs.gxt.ui.client.binding.FieldBinding;
import com.extjs.gxt.ui.client.data.ModelData;

/**
 * Extension of FieldBinding to bind
 *
 * @author Alex Bertram
 */
public class MappingComboBoxBinding extends FieldBinding {

    public MappingComboBoxBinding(final MappingComboBox field, String property) {
        super(field, property);
        setConverter(new Converter() {
            @Override
            public Object convertModelValue(Object value) {
                return field.wrap(value);
            }

            @Override
            public Object convertFieldValue(Object value) {
                return ((ModelData) value).get("value");
            }
        });
    }
}
