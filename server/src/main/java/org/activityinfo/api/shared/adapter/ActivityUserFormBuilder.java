package org.activityinfo.api.shared.adapter;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import org.activityinfo.api.shared.model.*;
import org.activityinfo.api2.shared.Cuid;
import org.activityinfo.api2.shared.Iri;
import org.activityinfo.api2.shared.LocalizedString;
import org.activityinfo.api2.shared.Namespace;
import org.activityinfo.api2.shared.form.*;
import org.activityinfo.ui.full.client.i18n.I18N;

import java.util.List;

/**
 * Adapts a Legacy "Activity" model to a FormClass
 */
public class ActivityUserFormBuilder {


    private final ActivityDTO activity;
    private List<FormElement> siteElements = Lists.newArrayList();

    private FormClass siteForm;

    public ActivityUserFormBuilder(ActivityDTO activity) {
        this.activity = activity;
    }

    public FormClass build() {
        Cuid classId = CuidAdapter.activityFormClass(activity.getId());

        siteForm = new FormClass(classId);
        FormField partnerField = new FormField(CuidAdapter.field(classId, CuidAdapter.PARTNER_FIELD));
        partnerField.addSuperProperty(Namespace.REPORTED_BY);
        partnerField.setLabel(new LocalizedString(I18N.CONSTANTS.partner()));
        partnerField.setRange(CuidAdapter.partnerFormClass(activity.getDatabase().getId()).asIri());
        partnerField.setType(FormFieldType.REFERENCE);
        siteForm.addElement(partnerField);

        FormField projectField = new FormField(CuidAdapter.field(classId, CuidAdapter.PROJECT_FIELD));
        projectField.setLabel(new LocalizedString(I18N.CONSTANTS.project()));
        projectField.setRange(CuidAdapter.projectFormClass(activity.getDatabase().getId()).asIri());
        projectField.setType(FormFieldType.REFERENCE);
        siteForm.addElement(projectField);

        FormField dateField = new FormField(CuidAdapter.field(classId, CuidAdapter.DATE_FIELD));
        dateField.setLabel(new LocalizedString(I18N.CONSTANTS.endDate()));
        dateField.setType(FormFieldType.LOCAL_DATE);
        siteForm.addElement(dateField);

        FormField locationField = new FormField(CuidAdapter.locationField(activity.getId()));
        locationField.setLabel(new LocalizedString(activity.getLocationType().getName()));
        locationField.setRange(locationClass(activity.getLocationType()).asIri());
        locationField.setType(FormFieldType.REFERENCE);
        siteForm.addElement(locationField);

        for (AttributeGroupDTO group : activity.getAttributeGroups()) {
            FormField attributeField = new FormField(CuidAdapter.attributeGroupField(activity, group));
            attributeField.setLabel(new LocalizedString(group.getName()));
            attributeField.setRange(CuidAdapter.attributeGroupFormClass(group).asIri());
            attributeField.setType(FormFieldType.REFERENCE);
            attributeField.setRequired(group.isMandatory());
            if (group.isMultipleAllowed()) {
                attributeField.setCardinality(FormFieldCardinality.MULTIPLE);
            } else {
                attributeField.setCardinality(FormFieldCardinality.SINGLE);
            }
            siteForm.addElement(attributeField);
        }

        for (IndicatorGroup group : activity.groupIndicators()) {
            if (Strings.isNullOrEmpty(group.getName())) {
                addIndicators(siteForm, group);
            } else {
                FormSection section = new FormSection(CuidAdapter.activityFormSection(activity.getId(), group.getName()));
                section.setLabel(new LocalizedString(group.getName()));

                addIndicators(section, group);

                siteForm.addElement(section);
            }
        }

        FormField commentsField = new FormField(CuidAdapter.commentsField(activity.getId()));
        commentsField.setType(FormFieldType.NARRATIVE);
        commentsField.setLabel(new LocalizedString(I18N.CONSTANTS.comments()));
        siteForm.addElement(commentsField);

        return siteForm;
    }

    private static Cuid locationClass(LocationTypeDTO locationType) {
        if(locationType.isAdminLevel()) {
            return CuidAdapter.adminLevelFormClass(locationType.getBoundAdminLevelId());
        } else {
            return CuidAdapter.locationFormClass(locationType.getId());
        }
    }

    private static void addIndicators(FormElementContainer container, IndicatorGroup group) {
        for (IndicatorDTO indicator : group.getIndicators()) {
            FormField field = new FormField(CuidAdapter.indicatorField(indicator.getId()));
            field.setLabel(new LocalizedString(indicator.getName()));
            field.setDescription(new LocalizedString(indicator.getDescription()));
            field.setType(FormFieldType.QUANTITY);
            container.addElement(field);
        }
    }
}