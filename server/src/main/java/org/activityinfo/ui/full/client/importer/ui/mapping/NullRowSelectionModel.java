package org.activityinfo.ui.full.client.importer.ui.mapping;

import com.google.gwt.view.client.SelectionModel.AbstractSelectionModel;
import org.activityinfo.ui.full.client.importer.data.SourceRow;

/**
 * Disables row selection. For the ColumnMappingGrid, we want the user to
 * select columns.
 */
class NullRowSelectionModel extends AbstractSelectionModel<SourceRow> {

    protected NullRowSelectionModel() {
        super(null);
    }

    @Override
    public boolean isSelected(SourceRow object) {
        return false;
    }

    @Override
    public void setSelected(SourceRow object, boolean selected) {
    }

}
