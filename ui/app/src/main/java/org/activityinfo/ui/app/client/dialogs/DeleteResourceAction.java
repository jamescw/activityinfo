package org.activityinfo.ui.app.client.dialogs;
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

import org.activityinfo.i18n.shared.I18N;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.resource.UserResource;
import org.activityinfo.promise.Promise;
import org.activityinfo.service.store.UpdateResult;
import org.activityinfo.ui.app.client.Application;
import org.activityinfo.ui.app.client.page.Place;
import org.activityinfo.ui.app.client.page.folder.FolderPlace;
import org.activityinfo.ui.app.client.page.home.HomePlace;
import org.activityinfo.ui.app.client.request.RemoveRequest;
import org.activityinfo.ui.flux.store.Status;
import org.activityinfo.ui.style.ButtonStyle;

/**
 * @author yuriyz on 9/23/14.
 */
public class DeleteResourceAction implements ConfirmDialog.Action<UpdateResult> {

    private Application application;
    private ResourceId resourceId;
    private String resourceLabel;
    private Place placeAfterDeletion;

    public DeleteResourceAction(Application application, ResourceId resourceId, String resourceLabel) {
        this(application, resourceId, resourceLabel, true);
    }

    public DeleteResourceAction(Application application, ResourceId resourceId, String resourceLabel, boolean changePlaceAfterDeletion) {
        this.application = application;
        this.resourceId = resourceId;
        this.resourceLabel = resourceLabel;
        this.placeAfterDeletion = changePlaceAfterDeletion ? placeAfterDeletion(application, resourceId) : null;
    }

    private Place placeAfterDeletion(Application application, ResourceId resourceId) {
        Status<UserResource> resourceStatus = application.getResourceStore().get(resourceId);
        if (resourceStatus.isAvailable() &&
                application.getFolderStore().get(resourceStatus.get().getResource().getOwnerId()).isAvailable()) {
            return new FolderPlace(resourceStatus.get().getResource().getOwnerId());
        }
        return HomePlace.INSTANCE;
    }

    @Override
    public ConfirmDialog.Messages getConfirmationMessages() {
        return new org.activityinfo.ui.app.client.dialogs.ConfirmDialog.Messages(
                I18N.CONSTANTS.confirmDeletion(),
                I18N.MESSAGES.removeResourceConfirmation(resourceLabel),
                I18N.CONSTANTS.delete());
    }

    @Override
    public ConfirmDialog.Messages getProgressMessages() {
        return new org.activityinfo.ui.app.client.dialogs.ConfirmDialog.Messages(
                I18N.CONSTANTS.deletionInProgress(),
                I18N.MESSAGES.deletingResource(resourceLabel),
                I18N.CONSTANTS.deleting());
    }

    @Override
    public ConfirmDialog.Messages getFailureMessages() {
        return new org.activityinfo.ui.app.client.dialogs.ConfirmDialog.Messages(
                I18N.CONSTANTS.deletionFailed(),
                I18N.MESSAGES.retryResourceDeletion(resourceLabel),
                I18N.CONSTANTS.retry());
    }

    @Override
    public ButtonStyle getPrimaryButtonStyle() {
        return ButtonStyle.DANGER;
    }

    @Override
    public Promise<UpdateResult> execute() {
        return application.getRequestDispatcher().execute(new RemoveRequest(resourceId));
    }

    @Override
    public void onComplete() {
        if (placeAfterDeletion != null) {
            application.getRouter().navigate(placeAfterDeletion);
        }
    }
}