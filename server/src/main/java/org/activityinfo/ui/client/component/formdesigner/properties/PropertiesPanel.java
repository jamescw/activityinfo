package org.activityinfo.ui.client.component.formdesigner.properties;
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

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import org.activityinfo.ui.client.widget.TextBox;

/**
 * @author yuriyz on 7/9/14.
 */
public class PropertiesPanel extends Composite {

    private static OurUiBinder uiBinder = GWT
            .create(OurUiBinder.class);

    interface OurUiBinder extends UiBinder<Widget, PropertiesPanel> {
    }

    @UiField
    TextBox label;
    @UiField
    VerticalPanel panel;

    public PropertiesPanel() {
        initWidget(uiBinder.createAndBindUi(this));
    }

    public TextBox getLabel() {
        return label;
    }

    public VerticalPanel getPanel() {
        return panel;
    }
}
