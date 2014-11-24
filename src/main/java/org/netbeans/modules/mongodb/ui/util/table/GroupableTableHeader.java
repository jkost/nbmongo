
package org.netbeans.modules.mongodb.ui.util.table;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

/**
 * GroupableTableHeader
 * 
 * @version 1.0 20.10.1998
 * @author Nobuo Tamemasa
 */
@SuppressWarnings("serial")
public class GroupableTableHeader extends JTableHeader {

    @SuppressWarnings("unused")
    private static final String uiClassID = "GroupableTableHeaderUI";

    protected List<ColumnGroup> columnGroups = new ArrayList<>();

    public GroupableTableHeader(TableColumnModel model) {
        super(model);
        setUI(new GroupableTableHeaderUI());
        setReorderingAllowed(false);
    }

    @Override
    public void updateUI() {
        setUI(new GroupableTableHeaderUI());
    }

    @Override
    public final void setReorderingAllowed(boolean b) {
        super.setReorderingAllowed(false);
    }

    public void addColumnGroup(ColumnGroup g) {
        columnGroups.add(g);
    }

    public List<ColumnGroup> getColumnGroups(TableColumn col) {
        for (ColumnGroup group : columnGroups) {
            List<ColumnGroup> groups = group.getColumnGroups(col);
            if (!groups.isEmpty()) {
                return groups;
            }
        }
        return Collections.emptyList();
    }

    public void setColumnMargin() {
        int columnMargin = getColumnModel().getColumnMargin();
        for (ColumnGroup group : columnGroups) {
            group.setColumnMargin(columnMargin);
        }
    }

}