/* 
 * Copyright (C) 2015 Yann D'Isanto
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package org.netbeans.modules.mongodb.ui.components.result_panel.views.flattable;

import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import javax.swing.SwingUtilities;
import javax.swing.table.AbstractTableModel;
import lombok.Getter;
import lombok.Setter;
import org.bson.BsonDocument;
import org.bson.BsonValue;
import org.netbeans.modules.mongodb.api.CollectionResultPages;
import org.netbeans.modules.mongodb.ui.components.CollectionResultPanel;

/**
 *
 * @author Yann D'Isanto
 */
public final class DocumentsFlatTableModel extends AbstractTableModel implements CollectionResultPanel.View, CollectionResultPages.Listener {
    
    private static final long serialVersionUID = 1L;

    @Getter
    private final CollectionResultPages pages;

    private final List<String> columns = new LinkedList<>();

    @Getter
    @Setter
    private boolean sortDocumentsFields;

    public DocumentsFlatTableModel(CollectionResultPages pages) {
        this.pages = pages;
        pages.addListener(this);
        buildModelFromCurrentPage();
    }

    private void buildModelFromCurrentPage() {

        Set<String> updatedColumns = sortDocumentsFields ? new TreeSet<>(columns) : new LinkedHashSet<>(columns);
        
        // update columns if necessary
        boolean columnsChanged = false;
        if (pages != null) {
            for (BsonDocument document : pages.getCurrentPageItems()) {
                columnsChanged |= updatedColumns.addAll(document.keySet());
            }
        }
        columns.clear();
        columns.addAll(updatedColumns);
        int idIndex = columns.indexOf("_id");
        if (idIndex > 0) {
            columns.remove(idIndex);
            columns.add(0, "_id");
        }

        final boolean tableStructureChanged = columnsChanged;
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                if (tableStructureChanged) {
                    fireTableStructureChanged();
                } else {
                    fireTableDataChanged();
                }
            }
        });

    }

    String getFieldName(int column) {
        return columns.get(column);
    }
    
    @Override
    public int getColumnCount() {
        return columns != null ? columns.size() : 0;
    }

    @Override
    public String getColumnName(int column) {
        return getFieldName(column);
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return BsonDocument.class;
    }

    @Override
    public int getRowCount() {
        return pages.getCurrentPageItems().size();
    }

    @Override
    public BsonValue getValueAt(int rowIndex, int columnIndex) {
        final BsonDocument document = getRowValue(rowIndex);
        if (document == null) {
            return null;
        }
        return document.get(getFieldName(columnIndex));
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    public BsonDocument getRowValue(int rowIndex) {
        if (rowIndex == -1) {
            return null;
        }
        return pages.getCurrentPageItems().get(rowIndex);
    }

    @Override
    public void pageChanged(CollectionResultPages source, int pageIndex, List<BsonDocument> page) {
        buildModelFromCurrentPage();
    }

    @Override
    public void pageObjectUpdated(int index, BsonDocument oldValue, BsonDocument newValue) {
        fireTableRowsUpdated(index, index);
    }
    
}
