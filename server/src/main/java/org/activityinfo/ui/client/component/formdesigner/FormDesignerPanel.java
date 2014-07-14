package org.activityinfo.ui.client.component.formdesigner;
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

import com.google.common.base.Function;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import org.activityinfo.core.client.ResourceLocator;
import org.activityinfo.model.form.FormClass;
import org.activityinfo.model.form.FormField;
import org.activityinfo.ui.client.component.form.field.FormFieldWidget;
import org.activityinfo.ui.client.component.formdesigner.drop.DropTargetPanel;
import org.activityinfo.ui.client.component.formdesigner.drop.NullValueUpdater;
import org.activityinfo.ui.client.component.formdesigner.header.HeaderPanel;
import org.activityinfo.ui.client.component.formdesigner.palette.FieldPalette;
import org.activityinfo.ui.client.component.formdesigner.properties.PropertiesPanel;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author yuriyz on 07/04/2014.
 */
public class FormDesignerPanel extends Composite {

    private static OurUiBinder uiBinder = GWT
            .create(OurUiBinder.class);


    interface OurUiBinder extends UiBinder<Widget, FormDesignerPanel> {
    }

    @UiField
    AbsolutePanel containerPanel;
    @UiField
    DropTargetPanel dropPanel;
    @UiField
    PropertiesPanel propertiesPanel;
    @UiField
    HeaderPanel headerPanel;
    @UiField
    FieldPalette fieldPalette;

    public FormDesignerPanel(final ResourceLocator resourceLocator, @Nonnull final FormClass formClass) {
        FormDesignerStyles.INSTANCE.ensureInjected();
        initWidget(uiBinder.createAndBindUi(this));
        propertiesPanel.setVisible(false);
        Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
            @Override
            public void execute() {
                FormDesigner formDesigner = new FormDesigner(FormDesignerPanel.this, resourceLocator, formClass);
                render(formDesigner);
            }
        });
    }

    private void render(final FormDesigner formDesigner) {
        for (final FormField formField : formDesigner.getFormClass().getFields()) {
            formDesigner.getFormFieldWidgetFactory().createWidget(formField, NullValueUpdater.INSTANCE).then(new Function<FormFieldWidget, Void>() {
                @Override
                public Void apply(@Nullable FormFieldWidget widget) {
                    Widget containerWidget = new WidgetContainer(formDesigner, widget, formField).asWidget();
                    dropPanel.add(containerWidget);
                    return null;
                }
            });
        }
    }

    public DropTargetPanel getDropPanel() {
        return dropPanel;
    }

    public AbsolutePanel getContainerPanel() {
        return containerPanel;
    }

    public PropertiesPanel getPropertiesPanel() {
        return propertiesPanel;
    }

    public HeaderPanel getHeaderPanel() {
        return headerPanel;
    }

    public FieldPalette getFieldPalette() {
        return fieldPalette;
    }

}
