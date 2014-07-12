package org.activityinfo.ui.client.component.formdesigner.drop;
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

import com.allen_sauer.gwt.dnd.client.DragContext;
import com.allen_sauer.gwt.dnd.client.VetoDragException;
import com.allen_sauer.gwt.dnd.client.drop.AbsolutePositionDropController;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Widget;
import org.activityinfo.model.form.FormField;
import org.activityinfo.model.formTree.FormTree;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.ui.client.component.form.field.FormFieldWidget;
import org.activityinfo.ui.client.component.form.field.FormFieldWidgetFactory;
import org.activityinfo.ui.client.component.formdesigner.*;

/**
 * @author yuriyz on 07/07/2014.
 */
public class DropPanelDropController extends AbsolutePositionDropController {

    private final Spacer spacer = new Spacer();
    private FormDesigner formDesigner;
    private AbsolutePanel dropTarget;

    public DropPanelDropController(AbsolutePanel dropTarget, FormDesigner formDesigner) {
        super(dropTarget);
        this.formDesigner = formDesigner;
        this.dropTarget = dropTarget;
    }

    @Override
    public void onPreviewDrop(DragContext context) throws VetoDragException {

        int spacerIndex = dropTarget.getWidgetIndex(spacer);
        if (spacerIndex != -1) {
            dropTarget.remove(spacerIndex);
        }

        int dropPanelHeightBeforeDrop = dropTarget.getOffsetHeight();

        final ValueUpdater valueUpdater = new ValueUpdater() {
            @Override
            public void update(Object value) {
                // todo
            }
        };

        ControlType controlType = formDesigner.getControlType(context.draggable);
        if (controlType == null) {
            draggingExistingWidgetContainer(context);
        }

        final FormField formField = new FormField(ResourceId.generateId());
        formField.setLabel(controlType.getLabel());
        formField.setType(controlType.getFieldType());

        FormTree formTree = new FormTree();
        FormTree.Node node = formTree.addRootField(formDesigner.getFormClass(), formField);

        // todo reference support
        FormFieldWidget formFieldWidget = new FormFieldWidgetFactory(formDesigner.getResourceLocator()).createWidget(null, node, valueUpdater);


        Widget containerWidget = new WidgetContainer(formDesigner, formFieldWidget, formField).asWidget();
        Integer insertIndex = formDesigner.getInsertIndex();
        if (insertIndex != null) {
            dropTarget.insert(containerWidget, insertIndex);
        } else { // null means insert in tail
            dropTarget.add(containerWidget);
        }

        Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
            @Override
            public void execute() {
                resizeDropPanel();
            }
        });

        // forbid drop of source control widget
        throw new VetoDragException();
    }

    private void draggingExistingWidgetContainer(final DragContext context) throws VetoDragException {

        final Integer insertIndex = formDesigner.getInsertIndex();
        final Widget draggable = context.draggable;
        if (insertIndex != null) {
            dropTarget.insert(draggable, insertIndex);
        } else { // null means insert in tail
            dropTarget.add(draggable);
        }

        Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
            @Override
            public void execute() {
                // avoid widgets overlap
                draggable.getElement().getStyle().setPosition(Style.Position.RELATIVE);
                draggable.getElement().getStyle().setTop(0, Style.Unit.PX);
                formDesigner.getDragController().makeNotDraggable(draggable);
            }
        });
        throw new VetoDragException();
    }


    private void resizeDropPanel() {
        int panelHeight = dropTarget.getOffsetHeight();
        int actualHeight = 0;
        for (Widget child : SpacerDropController.getDropPanelChilds(dropTarget)) {
            actualHeight = actualHeight + child.getOffsetHeight();
        }

        //GWT.log("panelHeight=" + panelHeight + ", actualHeight=" + actualHeight);
        if ((panelHeight - Metrics.EXPECTED_MAX_CHILD_HEIGHT) < actualHeight) {
            int height = actualHeight + Metrics.PANEL_HEIGHT_INCREASE_CORRECTION;
            dropTarget.setHeight(height + "px");

            // increase also height of container panel
            formDesigner.getFormDesignerPanel().getContainerPanel().setHeight((height + Metrics.HEIGHT_DIFF_BETWEEN_DROPPANEL_AND_CONTAINER_ABSOLUTE_PANEL) + "px");
        }
    }
}