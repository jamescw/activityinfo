package org.activityinfo.model.type.formatter;
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

import com.google.gwt.i18n.client.DateTimeFormat;
import org.activityinfo.model.type.time.LocalDate;

/**
 * @author yuriyz on 3/10/14.
 */
public class JsDateFormatterFactory implements DateFormatterFactory {

    public static final DateTimeFormat GWT_FORMAT = DateTimeFormat.getFormat(FORMAT);

    @Override
    public DateFormatter create() {
        return new DateFormatter() {
            @Override
            public String format(LocalDate value) {
                return value.toString();
            }

            @Override
            public LocalDate parse(String valueAsString) {
                return new LocalDate(GWT_FORMAT.parse(valueAsString));
            }
        };
    }
}
