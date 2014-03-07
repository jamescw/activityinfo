package org.activityinfo.ui.full.client.page.instance.views;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.HeadingElement;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;
import org.activityinfo.api2.client.InstanceQuery;
import org.activityinfo.api2.client.Promise;
import org.activityinfo.api2.client.ResourceLocator;
import org.activityinfo.api2.shared.Projection;
import org.activityinfo.api2.shared.application.ApplicationProperties;
import org.activityinfo.api2.shared.criteria.ParentCriteria;
import org.activityinfo.api2.shared.form.FormInstance;
import org.activityinfo.api2.shared.application.FolderClass;
import org.activityinfo.ui.full.client.page.instance.widgets.BreadCrumbBuilder;
import org.activityinfo.ui.full.client.page.instance.widgets.InstanceList;

import java.util.List;

/**
 * View for Folder instances
 */
public class FolderView implements InstanceView {


    private FormInstance instance;
    private ResourceLocator resourceLocator;

    interface FolderViewUiBinder extends UiBinder<HTMLPanel, FolderView> {
    }

    private static FolderViewUiBinder ourUiBinder = GWT.create(FolderViewUiBinder.class);

    private final HTMLPanel rootElement;

    @UiField
    HeadingElement folderNameElement;

    @UiField
    Element folderDescriptionElement;

    @UiField
    Element breadCrumbElement;

    @UiField
    InstanceList instanceList;

    private BreadCrumbBuilder breadCrumb;

    public FolderView(ResourceLocator resourceLocator) {
        this.resourceLocator = resourceLocator;
        rootElement = ourUiBinder.createAndBindUi(this);
        breadCrumb = new BreadCrumbBuilder(resourceLocator, breadCrumbElement);
    }

    public void show(FormInstance folderInstance) {
        this.instance = folderInstance;
        folderNameElement.setInnerText(instance.getString(FolderClass.LABEL_FIELD_ID));
        folderDescriptionElement.setInnerText(instance.getString(FolderClass.DESCRIPTION_FIELD_ID));
        instanceList.show(queryChildren());
        breadCrumb.show(folderInstance);
    }

    private Promise<List<Projection>> queryChildren() {
        return resourceLocator.query(InstanceQuery
            .select(
                    ApplicationProperties.LABEL_PROPERTY,
                    ApplicationProperties.DESCRIPTION_PROPERTY)
            .where(ParentCriteria.isChildOf(instance.getId())).build());
    }

    @Override
    public Widget asWidget() {
        return rootElement;
    }

}