/*
 * The MIT License
 *
 * Copyright 2013 Yann D'Isanto.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.netbeans.modules.mongodb.ui.windows.collectionview.flattable;

import com.mongodb.DBObject;
import de.bfg9000.mongonb.ui.core.windows.ResultDisplayer;
import de.bfg9000.mongonb.ui.core.windows.ResultPages;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import javax.swing.SwingUtilities;
import javax.swing.table.AbstractTableModel;
import lombok.Getter;

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
            for (DBObject document : pages.getPageContent()) {
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
        return DBObject.class;
    }

    @Override
    public int getRowCount() {
        return pages.getPageContent().size();
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        final DBObject document = getRowValue(rowIndex);
        if (document == null) {
            return null;
        }
        return document.get(getFieldName(columnIndex));
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    public DBObject getRowValue(int rowIndex) {
        if (rowIndex == -1) {
            return null;
        }
        return pages.getPageContent().get(rowIndex);
    }

    @Override
    public void pageChanged(ResultPages source, int pageIndex, List<DBObject> page) {
        buildFromPage();
    }

    @Override
    public void refreshIfNecessary(boolean force) {
        if (force) {
            buildFromPage();
        }
    }
}
