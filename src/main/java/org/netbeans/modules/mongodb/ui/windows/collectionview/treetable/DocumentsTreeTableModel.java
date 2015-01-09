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
package org.netbeans.modules.mongodb.ui.windows.collectionview.treetable;

import com.mongodb.DBObject;
import de.bfg9000.mongonb.ui.core.windows.ResultDisplayer;
import de.bfg9000.mongonb.ui.core.windows.ResultPages;
import java.util.List;
import javax.swing.SwingUtilities;
import javax.swing.tree.TreePath;
import lombok.Getter;
import org.jdesktop.swingx.treetable.DefaultTreeTableModel;
import org.jdesktop.swingx.treetable.TreeTableNode;

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
