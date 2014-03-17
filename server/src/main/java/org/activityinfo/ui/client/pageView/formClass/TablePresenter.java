package org.activityinfo.ui.client.pageView.formClass;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gwt.user.client.ui.Widget;
import org.activityinfo.core.client.ResourceLocator;
import org.activityinfo.core.shared.Cuid;
import org.activityinfo.core.shared.criteria.ClassCriteria;
import org.activityinfo.core.shared.form.tree.FormTree;
import org.activityinfo.fp.client.Promise;
import org.activityinfo.ui.client.component.table.FieldColumn;
import org.activityinfo.ui.client.widget.DisplayWidget;

import java.util.List;
import java.util.Map;

/**
 * Presents the instances of this form class as table
 */
public class TablePresenter implements DisplayWidget<FormTree> {

    private TableView tableView;

    private FormTree formTree;
    private Map<Cuid, FieldColumn> columnMap;

    private List<FieldColumn> columns;
    private List<FieldColumn> selectedColumns;

    public TablePresenter(ResourceLocator resourceLocator) {
        this.tableView = new TableView(resourceLocator);
    }

    @Override
    public Promise<Void> show(FormTree formTree) {
        this.formTree = formTree;
        enumerateColumns();

        // select all columns by default
        selectedColumns = Lists.newArrayList(columns);

        tableView.setCriteria(new ClassCriteria(formTree.getRootFormClass().getId()));
        tableView.setColumns(selectedColumns);

        return Promise.nothing();
    }

    @Override
    public Widget asWidget() {
        return tableView.asWidget();
    }

    /**
     *
     * @return a list of possible FieldColumns to display
     */
    private void enumerateColumns() {

        columnMap = Maps.newHashMap();
        columns = Lists.newArrayList();

        enumerateColumns(formTree.getRoot());
    }

    private void enumerateColumns(FormTree.Node parent) {
        for(FormTree.Node child : parent.getChildren()) {
            if(child.isReference()) {
                enumerateColumns(child);
            } else {
                if(columnMap.containsKey(child.getFieldId())) {
                    columnMap.get(child.getFieldId()).addFieldPath(child.getPath());
                } else {
                    FieldColumn col = new FieldColumn(child.getPath(), composeHeader(child));
                    columnMap.put(child.getFieldId(), col);
                    columns.add(col);
                }
            }
        }
    }

    private String composeHeader(FormTree.Node child) {
        if(child.getPath().isNested()) {
            return child.getFormClass().getLabel().getValue() + " " + child.getField().getLabel().getValue();
        } else {
            return child.getField().getLabel().getValue();
        }
    }
}
