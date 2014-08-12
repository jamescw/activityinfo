package org.activityinfo.core.shared.expr.functions;

import org.activityinfo.model.type.FieldValue;

public class NotEqualFunction extends ComparisonOperator {

    public static final NotEqualFunction INSTANCE = new NotEqualFunction();

    private NotEqualFunction() {
        super("!=");
    }

    @Override
    protected boolean apply(FieldValue a, FieldValue b) {
        return !a.equals(b);
    }
}
