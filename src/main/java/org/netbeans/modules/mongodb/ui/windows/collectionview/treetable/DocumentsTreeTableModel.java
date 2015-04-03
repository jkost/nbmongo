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
package org.netbeans.modules.mongodb.ui.windows.collectionview.treetable;

import com.mongodb.DBObject;
import org.netbeans.modules.mongodb.ui.ResultDisplayer;
import org.netbeans.modules.mongodb.ui.ResultPages;
import java.util.List;
import javax.swing.SwingUtilities;
import javax.swing.tree.TreePath;
import lombok.Getter;
import org.jdesktop.swingx.treetable.DefaultTreeTableModel;
import org.jdesktop.swingx.treetable.TreeTableNode;
import org.netbeans.lib.editor.util.swing.DocumentListenerPriority;

/**
 *
 * @author Yann D'Isanto
 */
public final class DocumentsTreeTableModel extends DefaultTreeTableModel implements ResultPages.ResultPagesListener, ResultDisplayer.View {

    @Getter
    private final ResultPages pages;

    public DocumentsTreeTableModel(ResultPages pages) {
        this.pages = pages;
        pages.addResultPagesListener(this);
        buildFromPage();
    }

    @Override
    public void pageChanged(ResultPages source, int pageIndex, List<DBObject> page) {
        buildFromPage();
    }

    @Override
    public void pageObjectUpdated(int index, DBObject oldValue, DBObject newValue) {
        documentUpdated(newValue, index);
    }

    @Override
    public void refreshIfNecessary(boolean force) {
        if (force) {
            buildFromPage();
        }
    }

    private void buildFromPage() {
        final TreeTableNode rootNode = new CollectionViewTreeTableNode<Object>(null) {
            {
                if (pages != null) {
                    for (DBObject document : pages.getPageContent()) {
                        add(new DocumentNode(document));
                    }
                }
            }
        };

        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                setRoot(rootNode);
            }
        });
    }

    public void documentUpdated(DBObject document, int index) {
        DocumentNode node = (DocumentNode) getRoot().getChildAt(index);
        setUserObject(node, document);
        TreePath path = new TreePath(getPathToRoot(node));
        modelSupport.fireTreeStructureChanged(path);
    }

    @Override
    public int getColumnCount() {
        return 1;
    }

    @Override
    public String getColumnName(int column) {
        return "Documents";
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return DBObject.class;
    }
}
