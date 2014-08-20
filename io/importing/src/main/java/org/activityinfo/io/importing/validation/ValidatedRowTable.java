package org.activityinfo.io.importing.validation;

import org.activityinfo.io.importing.source.SourceRow;
import org.activityinfo.io.importing.strategy.FieldImporterColumn;

import java.util.List;

/**
 * Created by alex on 4/4/14.
 */
public class ValidatedRowTable {

    private List<FieldImporterColumn> columns;
    private List<ValidatedRow> rows;

    public ValidatedRowTable(List<FieldImporterColumn> columns, List<ValidatedRow> rows) {
        this.columns = columns;
        this.rows = rows;
    }

    public List<FieldImporterColumn> getColumns() {
        return columns;
    }

    public List<ValidatedRow> getRows() {
        return rows;
    }

    public ValidatedRow getRow(SourceRow sourceRow) {
        for (ValidatedRow row : rows) {
            if (row.getSourceRow().equals(sourceRow)) {
                return row;
            }
        }
        return null;

    }
}
