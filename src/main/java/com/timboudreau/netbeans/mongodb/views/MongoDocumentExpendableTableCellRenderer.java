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
package com.timboudreau.netbeans.mongodb.views;

import com.mongodb.DBObject;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.table.TableCellRenderer;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;

/**
 *
 * @author Yann D'Isanto
 */
public final class MongoDocumentExpendableTableCellRenderer extends JPanel implements TableCellRenderer {

    private final JTree tree = new JTree();
    
    public MongoDocumentExpendableTableCellRenderer() {
        tree.setRootVisible(false);
        tree.setShowsRootHandles(true);
        DefaultTreeCellRenderer treeRenderer = new DefaultTreeCellRenderer();
        treeRenderer.setLeafIcon(null);
        treeRenderer.setClosedIcon(null);
        treeRenderer.setOpenIcon(null);
        tree.setCellRenderer(treeRenderer);
        
        setLayout(new BorderLayout());
        add(new JScrollPane(tree), BorderLayout.CENTER);
        setPreferredSize(new Dimension(0, 100));
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        tree.setModel(new DefaultTreeModel(buildDocumentTree((DBObject) value)));
        setBorder(hasFocus 
            ? BorderFactory.createLineBorder(Color.BLUE) 
            : BorderFactory.createEmptyBorder());
        return this;
    }
    
    private TreeNode buildDocumentTree(DBObject document) {
        final Map<String, Object> map = document.toMap();
        final DefaultMutableTreeNode root = new DefaultMutableTreeNode(document);
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            root.add(buildTreeNode(entry));
        }
        return root;
    }

    private MutableTreeNode buildTreeNode(Map.Entry<String, Object> entry) {
        if (entry.getValue() instanceof Map) {
            final Map<String, Object> map = (Map<String, Object>) entry.getValue();
            return buildTreeNode(entry.getKey(), map);
        }
        return new DefaultMutableTreeNode(entry.getKey() + ": " + entry.getValue());
    }

    private MutableTreeNode buildTreeNode(String name, Map<String, Object> value) {
        final DefaultMutableTreeNode node = new DefaultMutableTreeNode(name);
        for (Map.Entry<String, Object> entry : value.entrySet()) {
            node.add(buildTreeNode(entry));
        }
        return node;
    }
}
