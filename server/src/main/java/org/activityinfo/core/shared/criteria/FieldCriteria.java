package org.activityinfo.core.shared.criteria;

import org.activityinfo.core.shared.Cuid;
import org.activityinfo.core.shared.form.FormInstance;

import javax.annotation.Nullable;
import java.util.Objects;

/**
 * Accepts an instance if the given field value matches
 * exactly.
 */
public class FieldCriteria implements Criteria {

    private Cuid fieldId;
    private Object value;

    public FieldCriteria(Cuid fieldId, Object value) {
        this.fieldId = fieldId;
        this.value = value;
    }

    @Override
    public void accept(CriteriaVisitor visitor) {
        visitor.visitFieldCriteria(this);
    }

    @Override
    public boolean apply(@Nullable FormInstance input) {
        return Objects.equals(input.get(fieldId), value);
    }

}