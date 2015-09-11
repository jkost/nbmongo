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
package org.netbeans.modules.mongodb.ui.windows.collectionview.flattable;

import org.netbeans.modules.mongodb.ui.ResultDisplayer;
import org.netbeans.modules.mongodb.ui.ResultPages;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import javax.swing.SwingUtilities;
import javax.swing.table.AbstractTableModel;
import lombok.Getter;
import org.bson.BsonDocument;
import org.bson.BsonValue;

/**
 *
 * @author Yann D'Isanto
 */
public final class DocumentsFlatTableModel extends AbstractTableModel implements ResultPages.ResultPagesListener, ResultDisplayer.View {
    
    private static final long serialVersionUID = 1L;

    @Getter
    private final ResultPages pages;

    private final List<String> columns = new LinkedList<>();

    public DocumentsFlatTableModel(ResultPages pages) {
        this.pages = pages;
        pages.addResultPagesListener(this);
        buildFromPage();
    }

    private void buildFromPage() {

        Set<String> updatedColumns = new LinkedHashSet<>(columns);
    
        // update columns if necessary
        boolean columnsChanged = false;
        if (pages != null) {
            for (BsonDocument document : pages.getPageContent()) {
                columnsChanged |= updatedColumns.addAll(document.keySet());
            }
        }
        columns.clear();
        columns.addAll(updatedColumns);
        int idIndex = columns.indexOf("_id");
        if (idIndex > 0) {
            Collections.swap(columns, idIndex, 0);
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
        return pages.getPageContent().size();
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
        return pages.getPageContent().get(rowIndex);
    }

    @Override
    public void pageChanged(ResultPages source, int pageIndex, List<BsonDocument> page) {
        buildFromPage();
    }

    @Override
    public void pageObjectUpdated(int index, BsonDocument oldValue, BsonDocument newValue) {
        fireTableRowsUpdated(index, index);
    }
    
    @Override
    public void refreshIfNecessary(boolean force) {
        if (force) {
            buildFromPage();
        }
    }
}
