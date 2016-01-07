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
package org.netbeans.modules.mongodb.ui.components;

import java.awt.CardLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.JToggleButton;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableColumnModelListener;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.text.PlainDocument;
import javax.swing.tree.TreePath;
import lombok.Getter;
import lombok.Setter;
import org.bson.BsonDocument;
import org.bson.BsonValue;
import org.netbeans.modules.mongodb.CollectionInfo;
import org.netbeans.modules.mongodb.api.CollectionResult;
import org.netbeans.modules.mongodb.api.CollectionResultPages;
import org.netbeans.modules.mongodb.options.JsonCellRenderingOptions;
import org.netbeans.modules.mongodb.options.LabelCategory;
import org.netbeans.modules.mongodb.resources.Images;
import org.netbeans.modules.mongodb.ui.util.IntegerDocumentFilter;
import org.netbeans.modules.mongodb.ui.actions.*;
import org.netbeans.modules.mongodb.ui.components.result_panel.views.flattable.DocumentsFlatTableModel;
import org.netbeans.modules.mongodb.ui.components.result_panel.views.treetable.DocumentsTreeTableModel;
import org.netbeans.modules.mongodb.ui.util.BsonPropertyEditor;
import static org.netbeans.modules.mongodb.ui.util.BsonPropertyEditor.isQuickEditableBsonValue;
import org.netbeans.modules.mongodb.ui.util.BsonDocumentEditor;
import org.netbeans.modules.mongodb.ui.components.result_panel.actions.*;
import org.netbeans.modules.mongodb.ui.windows.collectionview.flattable.JsonFlatTableCellRenderer;
import org.netbeans.modules.mongodb.ui.windows.collectionview.treetable.BsonNodeRenderer;
import org.netbeans.modules.mongodb.ui.windows.collectionview.treetable.BsonPropertyNode;
import org.netbeans.modules.mongodb.ui.windows.collectionview.treetable.BsonValueNode;
import org.netbeans.modules.mongodb.ui.windows.collectionview.treetable.DocumentRootTreeTableHighlighter;
import org.netbeans.modules.mongodb.util.BsonProperty;
import org.openide.awt.NotificationDisplayer;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.NbBundle.Messages;
import org.openide.util.NbPreferences;
import org.openide.util.RequestProcessor;

/**
 *
 * @author Yann D'Isanto
 */
@Messages({
    "displayDocumentTitle=Display document",
    "invalidJson=invalid json",
    "# {0} - total documents count",
    "totalDocuments=Total Documents: {0}      ",
    "# {0} - current page",
    "# {1} - total page count",
    "pageCountLabel=Page {0} of {1}",
    "# {0} - collection namespace",
    "collectionViewTitle={0}",
    "# {0} - connection name",
    "# {1} - view title",
    "collectionViewTooltip={0}: {1}",
    "documentEditionShortcutHintTitle=Use CTRL + doubleclick to edit full document",
    "documentEditionShortcutHintDetails=Click here or use shortcut so this message won't show again."
})
public final class CollectionResultPanel extends javax.swing.JPanel {

    private static final long serialVersionUID = 1L;

    private static final ResultView DEFAULT_RESULT_VIEW = ResultView.TREE_TABLE;

    @Getter
    private final Lookup lookup;

    private final Map<ResultView, JToggleButton> resultViewButtons = new EnumMap<>(ResultView.class);

    private ResultView resultView = DEFAULT_RESULT_VIEW;

    private final Map<ResultView, View> resultViews = new EnumMap<>(ResultView.class);

    @Getter
    private final DocumentsTreeTableModel treeTableModel;

    @Getter
    private final DocumentsFlatTableModel flatTableModel;

    @Getter
    @Setter
    private boolean displayDocumentEditionShortcutHint = true;
    
    
    @Getter
    private final boolean readOnly;

    private CollectionResult currentResult;
    
    private final Runnable resultRefresh = new Runnable() {

        @Override
        public void run() {
            getResultPages().refresh();
        }
    };
    
    private final Runnable resultUpdate = new Runnable() {

        @Override
        public void run() {
            getResultPages().setQueryResult(currentResult);
        }
    };
    private final CollectionResultPages.Listener pagesListener = new CollectionResultPages.Listener() {

        @Override
        public void pageChanged(CollectionResultPages pages, int pageIndex, List<BsonDocument> page) {
            updatePagination();
            updateDocumentButtonsState();
        }

        @Override
        public void pageObjectUpdated(int index, BsonDocument oldValue, BsonDocument newValue) {
        }

    };
    
    /**
     * Creates new form QueryResultPanel
     */
    public CollectionResultPanel(Lookup lookup, final boolean readOnly) {
        this.lookup = lookup;
//        this.queryResultWorkerFactory = queryResultWorkerFactory;
        this.readOnly = readOnly;
        initComponents();

        addButton.setVisible(readOnly == false);
        deleteButton.setVisible(readOnly == false);
        editButton.setVisible(readOnly == false);

        resultViewButtons.put(ResultView.FLAT_TABLE, flatTableViewButton);
        resultViewButtons.put(ResultView.TREE_TABLE, treeTableViewButton);

        int pageSize = 20; // TODO: store/load from pref
        currentResult = CollectionResult.EMPTY;
            
        treeTableModel = new DocumentsTreeTableModel(new CollectionResultPages(currentResult, pageSize));
        flatTableModel = new DocumentsFlatTableModel(new CollectionResultPages(currentResult, pageSize));
        resultViews.put(ResultView.TREE_TABLE, treeTableModel);
        resultViews.put(ResultView.FLAT_TABLE, flatTableModel);

        final ListSelectionListener tableSelectionListener = new ListSelectionListener() {

            @Override
            public void valueChanged(ListSelectionEvent evt) {
                if (!evt.getValueIsAdjusting()) {
                    updateDocumentButtonsState();
                }
            }
        };

        resultFlatTable.setModel(flatTableModel);
        resultFlatTable.setDefaultRenderer(BsonDocument.class, new JsonFlatTableCellRenderer());
        resultFlatTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        resultFlatTable.getSelectionModel().addListSelectionListener(tableSelectionListener);
        resultFlatTable.getColumnModel().addColumnModelListener(new TableColumnModelListener() {

            @Override
            public void columnAdded(TableColumnModelEvent e) {
                final TableColumnModel model = (TableColumnModel) e.getSource();
                final TableColumn column = model.getColumn(e.getToIndex());
                if ("_id".equals(column.getHeaderValue())) {
                    final Font font = JsonCellRenderingOptions.INSTANCE
                        .getLabelFontConf(LabelCategory.DOCUMENT).getFont();
                    final int preferredWidth = getFontMetrics(font)
                        .stringWidth("000000000000000000000000");
                    column.setPreferredWidth(preferredWidth);
                }
            }

            @Override
            public void columnRemoved(TableColumnModelEvent e) {
            }

            @Override
            public void columnMoved(TableColumnModelEvent e) {
            }

            @Override
            public void columnMarginChanged(ChangeEvent e) {
            }

            @Override
            public void columnSelectionChanged(ListSelectionEvent e) {
            }
        });

        resultTreeTable.setTreeTableModel(treeTableModel);
        resultTreeTable.setTreeCellRenderer(new BsonNodeRenderer());
        resultTreeTable.addHighlighter(new DocumentRootTreeTableHighlighter());
        resultTreeTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        resultTreeTable.getSelectionModel().addListSelectionListener(tableSelectionListener);

        final PlainDocument document = (PlainDocument) pageSizeField.getDocument();
        document.setDocumentFilter(new IntegerDocumentFilter());

        resultTreeTable.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && SwingUtilities.isLeftMouseButton(e)) {
                    final TreePath path = resultTreeTable.getPathForLocation(e.getX(), e.getY());
                    final BsonValueNode node = (BsonValueNode) path.getLastPathComponent();

                    if ((e.getModifiersEx() & MouseEvent.CTRL_DOWN_MASK) == MouseEvent.CTRL_DOWN_MASK) {
                        final BsonValueNode documentRootNode = (BsonValueNode) path.getPathComponent(1);
                        if (readOnly) {
                            BsonDocumentEditor.showReadOnly(
                                Bundle.displayDocumentTitle(),
                                documentRootNode.getValue().asDocument());
                        } else {
                            editDocumentAction.setDocument(documentRootNode.getValue().asDocument());
                            editDocumentAction.actionPerformed(null);
                        }
                    } else {
                        if (node.isLeaf()) {
                            if (readOnly == false) {
                                dislayDocumentEditionShortcutHintIfNecessary();
                                if (node instanceof BsonPropertyNode) {
                                    BsonPropertyNode propertyNode = (BsonPropertyNode) node;
                                    if (BsonPropertyEditor.isQuickEditableBsonValue(propertyNode.getValue())) {
                                        editBsonPropertyNodeAction.setPropertyNode(propertyNode);
                                        editBsonPropertyNodeAction.actionPerformed(null);
                                    }
                                } else if (BsonPropertyEditor.isQuickEditableBsonValue(node.getValue())) {
                                    editBsonValueNodeAction.setValueNode(node);
                                    editBsonValueNodeAction.actionPerformed(null);
                                }
                            }
                        } else {
                            if (resultTreeTable.isCollapsed(path)) {
                                resultTreeTable.expandPath(path);
                            } else {
                                resultTreeTable.collapsePath(path);
                            }
                        }
                    }
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if(e.isPopupTrigger() || SwingUtilities.isRightMouseButton(e)) {
                    final TreePath path = resultTreeTable.getPathForLocation(e.getX(), e.getY());
                    if (path != null) {
                        final int row = resultTreeTable.getRowForPath(path);
                        resultTreeTable.setRowSelectionInterval(row, row);
                    }
                    final JPopupMenu menu = createTreeTableContextMenu(path);
                    menu.show(e.getComponent(), e.getX(), e.getY());
                }
            }

        });
        resultFlatTable.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && SwingUtilities.isLeftMouseButton(e) && readOnly == false) {
                    editSelectedDocumentAction.actionPerformed(null);
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    final int row = resultFlatTable.rowAtPoint(e.getPoint());
                    if (row > -1) {
                        final int column = resultFlatTable.columnAtPoint(e.getPoint());
                        resultFlatTable.setRowSelectionInterval(row, row);
                        final JPopupMenu menu = createFlatTableContextMenu(row, column);
                        menu.show(e.getComponent(), e.getX(), e.getY());
                    }
                }
            }

        });
        getResultPages().addListener(pagesListener);
//        updatePagination();
//        updateDocumentButtonsState();
    }

    
    public void setResult(CollectionResult result) {
        currentResult = result;
//        updateResultDisplayWorker.execute();
        RequestProcessor.getDefault().post(resultUpdate); 
    }
    
    public void refreshResults() {
//        refreshResultDisplayWorker.execute();
        RequestProcessor.getDefault().post(resultRefresh); 
    }
    
    public void editDocument(BsonDocument document, BsonDocument modifiedDocument) {
        getTreeTablePages().updateDocument(document, modifiedDocument);
        getFlatTablePages().updateDocument(document, modifiedDocument);
    }

    private JTable getResultTable() {
        switch (resultView) {
            case FLAT_TABLE:
                return resultFlatTable;
            case TREE_TABLE:
                return resultTreeTable;
            default:
                throw new AssertionError();
        }
    }

    public CollectionResultPages getFlatTablePages() {
        return flatTableModel.getPages();
    }

    public CollectionResultPages getTreeTablePages() {
        return treeTableModel.getPages();
    }

    public CollectionResultPages getResultPages() {
        return resultViews.get(resultView).getPages();
    }

    public BsonDocument getResultTableSelectedDocument() {
        final JTable table = getResultTable();
        int row = table.getSelectedRow();
        if (row == -1) {
            return null;
        }
        switch (resultView) {
            case FLAT_TABLE:
                return ((DocumentsFlatTableModel) resultFlatTable.getModel()).getRowValue(row);
            case TREE_TABLE:
                final TreePath selectionPath = resultTreeTable.getPathForRow(row);
                final BsonValueNode documentRootNode = (BsonValueNode) selectionPath.getPathComponent(1);
                return documentRootNode.getValue().asDocument();
            default:
                throw new AssertionError();
        }
    }

    public void changeResultView(ResultView resultView) {
        getResultPages().removeListener(pagesListener);
        this.resultView = resultView;
        getResultPages().addListener(pagesListener);
        if(getResultPages().getQueryResult().equals(currentResult) == false) {
            setResult(currentResult);
        }
        updateResultPanel();
    }

    private void updateResultPanel() {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                final CardLayout layout = (CardLayout) resultPanel.getLayout();
                layout.show(resultPanel, resultView.name());
                final boolean treeViewDisplayed = resultView == ResultView.TREE_TABLE;
                collapseTreeAction.setEnabled(treeViewDisplayed);
                expandTreeAction.setEnabled(treeViewDisplayed);
            }
        });
    }

    public void updatePagination() {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                CollectionResultPages pages = getResultPages();
                long documentCount = pages.getTotalElementsCount();
                totalDocumentsLabel.setText(
                    Bundle.totalDocuments(documentCount));
                int page = documentCount == 0 ? 0 : pages.getPageIndex();
                int pageCount = pages.getPageCount();
                pageCountLabel.setText(Bundle.pageCountLabel(page, pageCount));
                navFirstAction.setEnabled(pages.canMoveBackward());
                navLeftAction.setEnabled(pages.canMoveBackward());
                navRightAction.setEnabled(pages.canMoveForward());
                navLastAction.setEnabled(pages.canMoveForward());
            }
        });
    }

    private void updateDocumentButtonsState() {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                boolean itemSelected = getResultTable().getSelectedRow() > -1;
                addButton.setEnabled(readOnly == false);
                deleteButton.setEnabled(itemSelected && readOnly == false);
                editButton.setEnabled(itemSelected && readOnly == false);
            }
        });
    }

    private void changePageSize(int pageSize) {
        getTreeTablePages().setPageSize(pageSize);
        getFlatTablePages().setPageSize(pageSize);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        resultViewButtonGroup = new javax.swing.ButtonGroup();
        documentsToolBar = new javax.swing.JToolBar();
        treeTableViewButton = new javax.swing.JToggleButton();
        flatTableViewButton = new javax.swing.JToggleButton();
        jSeparator4 = new javax.swing.JToolBar.Separator();
        expandTreeButton = new javax.swing.JButton();
        collapseTreeButton = new javax.swing.JButton();
        jSeparator5 = new javax.swing.JToolBar.Separator();
        addButton = new javax.swing.JButton();
        deleteButton = new javax.swing.JButton();
        editButton = new javax.swing.JButton();
        exportButton = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JToolBar.Separator();
        refreshDocumentsButton = new javax.swing.JButton();
        navFirstButton = new javax.swing.JButton();
        navLeftButton = new javax.swing.JButton();
        navRightButton = new javax.swing.JButton();
        navLastButton = new javax.swing.JButton();
        jSeparator2 = new javax.swing.JToolBar.Separator();
        pageSizeLabel = new javax.swing.JLabel();
        pageSizeField = new javax.swing.JTextField();
        jSeparator3 = new javax.swing.JToolBar.Separator();
        totalDocumentsLabel = new javax.swing.JLabel();
        pageCountLabel = new javax.swing.JLabel();
        resultPanel = new javax.swing.JPanel();
        treeTableScrollPane = new javax.swing.JScrollPane();
        resultTreeTable = new org.jdesktop.swingx.JXTreeTable();
        flatTableScrollPane = new javax.swing.JScrollPane();
        resultFlatTable = new javax.swing.JTable();

        setLayout(new java.awt.BorderLayout(0, 5));

        documentsToolBar.setFloatable(false);
        documentsToolBar.setRollover(true);

        treeTableViewButton.setAction(treeTableViewAction);
        resultViewButtonGroup.add(treeTableViewButton);
        treeTableViewButton.setFocusable(false);
        treeTableViewButton.setHideActionText(true);
        treeTableViewButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        treeTableViewButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        documentsToolBar.add(treeTableViewButton);

        flatTableViewButton.setAction(flatTableViewAction);
        resultViewButtonGroup.add(flatTableViewButton);
        flatTableViewButton.setFocusable(false);
        flatTableViewButton.setHideActionText(true);
        flatTableViewButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        flatTableViewButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        documentsToolBar.add(flatTableViewButton);
        documentsToolBar.add(jSeparator4);

        expandTreeButton.setAction(expandTreeAction);
        expandTreeButton.setFocusable(false);
        expandTreeButton.setHideActionText(true);
        expandTreeButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        expandTreeButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        documentsToolBar.add(expandTreeButton);

        collapseTreeButton.setAction(collapseTreeAction);
        collapseTreeButton.setFocusable(false);
        collapseTreeButton.setHideActionText(true);
        collapseTreeButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        collapseTreeButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        documentsToolBar.add(collapseTreeButton);
        documentsToolBar.add(jSeparator5);

        addButton.setAction(addDocumentAction);
        addButton.setFocusable(false);
        addButton.setHideActionText(true);
        addButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        addButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        documentsToolBar.add(addButton);

        deleteButton.setAction(deleteSelectedDocumentAction);
        deleteButton.setEnabled(false);
        deleteButton.setFocusable(false);
        deleteButton.setHideActionText(true);
        deleteButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        deleteButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        documentsToolBar.add(deleteButton);

        editButton.setAction(editSelectedDocumentAction);
        editButton.setEnabled(false);
        editButton.setFocusable(false);
        editButton.setHideActionText(true);
        editButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        editButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        documentsToolBar.add(editButton);

        exportButton.setAction(exportQueryResultAction);
        exportButton.setFocusable(false);
        exportButton.setHideActionText(true);
        exportButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        exportButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        documentsToolBar.add(exportButton);
        documentsToolBar.add(jSeparator1);

        refreshDocumentsButton.setAction(refreshDocumentsAction);
        refreshDocumentsButton.setFocusable(false);
        refreshDocumentsButton.setHideActionText(true);
        refreshDocumentsButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        refreshDocumentsButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        documentsToolBar.add(refreshDocumentsButton);

        navFirstButton.setAction(navFirstAction);
        navFirstButton.setEnabled(false);
        navFirstButton.setHideActionText(true);
        documentsToolBar.add(navFirstButton);

        navLeftButton.setAction(navLeftAction);
        navLeftButton.setEnabled(false);
        navLeftButton.setHideActionText(true);
        documentsToolBar.add(navLeftButton);

        navRightButton.setAction(navRightAction);
        navRightButton.setEnabled(false);
        navRightButton.setFocusable(false);
        navRightButton.setHideActionText(true);
        navRightButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        navRightButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        documentsToolBar.add(navRightButton);

        navLastButton.setAction(navLastAction);
        navLastButton.setEnabled(false);
        navLastButton.setFocusable(false);
        navLastButton.setHideActionText(true);
        navLastButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        navLastButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        documentsToolBar.add(navLastButton);
        documentsToolBar.add(jSeparator2);

        org.openide.awt.Mnemonics.setLocalizedText(pageSizeLabel, org.openide.util.NbBundle.getMessage(CollectionResultPanel.class, "CollectionResultPanel.pageSizeLabel.text")); // NOI18N
        documentsToolBar.add(pageSizeLabel);

        pageSizeField.setMaximumSize(new java.awt.Dimension(40, 2147483647));
        pageSizeField.setMinimumSize(new java.awt.Dimension(40, 20));
        pageSizeField.setPreferredSize(new java.awt.Dimension(40, 20));
        pageSizeField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pageSizeFieldActionPerformed(evt);
            }
        });
        documentsToolBar.add(pageSizeField);
        documentsToolBar.add(jSeparator3);
        documentsToolBar.add(totalDocumentsLabel);
        documentsToolBar.add(pageCountLabel);

        add(documentsToolBar, java.awt.BorderLayout.NORTH);

        resultPanel.setLayout(new java.awt.CardLayout());

        treeTableScrollPane.setViewportView(resultTreeTable);

        resultPanel.add(treeTableScrollPane, "TREE_TABLE");

        flatTableScrollPane.setViewportView(resultFlatTable);

        resultPanel.add(flatTableScrollPane, "FLAT_TABLE");

        add(resultPanel, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void pageSizeFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pageSizeFieldActionPerformed
        final int pageSize = Integer.parseInt(pageSizeField.getText());
        changePageSize(pageSize);
    }//GEN-LAST:event_pageSizeFieldActionPerformed

    private final Action addDocumentAction = new AddDocumentAction(this);

    private final Action deleteSelectedDocumentAction = new DeleteSelectedDocumentAction(this);

    private final EditDocumentAction editDocumentAction = new EditDocumentAction(this);

    private final Action editSelectedDocumentAction = new EditSelectedDocumentAction(this);

    private final EditBsonPropertyNodeAction editBsonPropertyNodeAction = new EditBsonPropertyNodeAction(this, null);

    private final EditBsonValueNodeAction editBsonValueNodeAction = new EditBsonValueNodeAction(this, null);
    
    private final FindWithBsonPropertyNodeAction findWithBsonPropertyNodeAction = new FindWithBsonPropertyNodeAction(this, null);

    private final Action refreshDocumentsAction = new RefreshDocumentsAction(this);

    private final Action navFirstAction = new NavFirstAction(this);

    private final Action navLeftAction = new NavLeftAction(this);

    private final Action navRightAction = new NavRightAction(this);

    private final Action navLastAction = new NavLastAction(this);

    private final Action exportQueryResultAction = new ExportQueryResultAction(this);

    private final Action treeTableViewAction = ChangeResultViewAction.create(this, ResultView.TREE_TABLE);

    private final Action flatTableViewAction = ChangeResultViewAction.create(this, ResultView.FLAT_TABLE);

    private final Action collapseTreeAction = new CollapseAllDocumentsAction(this);

    private final Action expandTreeAction = new ExpandAllDocumentsAction(this);

    public enum ResultView {

        FLAT_TABLE, TREE_TABLE

    }

//    public static interface QueryResultWorkerFactory {
//
//        QueryResultWorker createWorker();
//
//    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addButton;
    private javax.swing.JButton collapseTreeButton;
    private javax.swing.JButton deleteButton;
    private javax.swing.JToolBar documentsToolBar;
    private javax.swing.JButton editButton;
    private javax.swing.JButton expandTreeButton;
    private javax.swing.JButton exportButton;
    private javax.swing.JScrollPane flatTableScrollPane;
    private javax.swing.JToggleButton flatTableViewButton;
    private javax.swing.JToolBar.Separator jSeparator1;
    private javax.swing.JToolBar.Separator jSeparator2;
    private javax.swing.JToolBar.Separator jSeparator3;
    private javax.swing.JToolBar.Separator jSeparator4;
    private javax.swing.JToolBar.Separator jSeparator5;
    private javax.swing.JButton navFirstButton;
    private javax.swing.JButton navLastButton;
    private javax.swing.JButton navLeftButton;
    private javax.swing.JButton navRightButton;
    private javax.swing.JLabel pageCountLabel;
    private javax.swing.JTextField pageSizeField;
    private javax.swing.JLabel pageSizeLabel;
    private javax.swing.JButton refreshDocumentsButton;
    @Getter
    private javax.swing.JTable resultFlatTable;
    private javax.swing.JPanel resultPanel;
    @Getter
    private org.jdesktop.swingx.JXTreeTable resultTreeTable;
    private javax.swing.ButtonGroup resultViewButtonGroup;
    private javax.swing.JLabel totalDocumentsLabel;
    private javax.swing.JScrollPane treeTableScrollPane;
    private javax.swing.JToggleButton treeTableViewButton;
    // End of variables declaration//GEN-END:variables

    private JPopupMenu createTreeTableContextMenu(TreePath treePath) {
        final JPopupMenu menu = new JPopupMenu();
        if (treePath != null) {
            BsonValueNode selectedNode = (BsonValueNode) treePath.getLastPathComponent();
            final BsonValueNode documentRootNode = (BsonValueNode) treePath.getPathComponent(1);
            menu.add(new JMenuItem(new CopyFullDocumentToClipboardAction(documentRootNode.getValue().asDocument())));
            if (selectedNode != documentRootNode) {
                if (selectedNode instanceof BsonPropertyNode) {
                    BsonPropertyNode propertyNode = (BsonPropertyNode) selectedNode;
                    BsonProperty property = propertyNode.getBsonProperty();
                    
                    if(lookup.lookup(CollectionInfo.class) != null) {
                        findWithBsonPropertyNodeAction.setPropertyNode(propertyNode);
                        menu.add(new JMenuItem(findWithBsonPropertyNodeAction));
                    }
                    menu.add(new JMenuItem(new CopyKeyValuePairToClipboardAction(property)));
                    menu.add(new JMenuItem(new CopyKeyToClipboardAction(property)));
                    menu.add(new JMenuItem(new CopyValueToClipboardAction(property.getValue())));
                    if (isQuickEditableBsonValue(property.getValue())) {
                        editBsonPropertyNodeAction.setPropertyNode(propertyNode);
                        menu.add(new JMenuItem(editBsonPropertyNodeAction));
                    }
                } else {
                    BsonValue value = selectedNode.getValue();
                    menu.add(new JMenuItem(new CopyValueToClipboardAction(value)));
                    if (isQuickEditableBsonValue(value)) {
                        editBsonValueNodeAction.setValueNode((BsonValueNode) selectedNode);
                        menu.add(new JMenuItem(editBsonValueNodeAction));
                    }
                }
            }
            if (readOnly == false) {
                menu.addSeparator();
                menu.add(new JMenuItem(new EditSelectedDocumentAction(this)));
                menu.add(new JMenuItem(new DeleteSelectedDocumentAction(this)));
            }
            menu.addSeparator();
        }
        menu.add(new JMenuItem(collapseTreeAction));
        menu.add(new JMenuItem(expandTreeAction));
        return menu;
    }

    private JPopupMenu createFlatTableContextMenu(int row, int column) {
        final JPopupMenu menu = new JPopupMenu();
        final BsonDocument document = getFlatTablePages().getCurrentPageItems().get(row);
        menu.add(new JMenuItem(new CopyFullDocumentToClipboardAction(document)));
        final DocumentsFlatTableModel model = (DocumentsFlatTableModel) resultFlatTable.getModel();
        final BsonProperty property = new BsonProperty(
            model.getColumnName(column),
            model.getValueAt(row, column));
        menu.add(new JMenuItem(new CopyKeyValuePairToClipboardAction(property)));
        menu.add(new JMenuItem(new CopyKeyToClipboardAction(property)));
        menu.add(new JMenuItem(new CopyValueToClipboardAction(property.getValue())));
        if (readOnly == false) {
            menu.addSeparator();
            menu.add(new JMenuItem(new EditSelectedDocumentAction(this)));
            menu.add(new JMenuItem(new DeleteSelectedDocumentAction(this)));
        }
        return menu;
    }

    private void dislayDocumentEditionShortcutHintIfNecessary() {
        if (displayDocumentEditionShortcutHint && readOnly == false) {
            NotificationDisplayer.getDefault().notify(
                Bundle.documentEditionShortcutHintTitle(),
                new ImageIcon(Images.EDIT_DOCUMENT_ICON),
                Bundle.documentEditionShortcutHintDetails(),
                new ActionListener() {

                    @Override
                    public void actionPerformed(ActionEvent e) {
                        displayDocumentEditionShortcutHint = false;
                    }
                }
            );
        }
    }

    public Preferences prefs() {
        return NbPreferences.forModule(CollectionResultPanel.class).node(CollectionResultPanel.class.getName());
    }

    public void loadPreferences() {
        final Preferences prefs = prefs();
        final String version = prefs.get("version", "1.0");
        displayDocumentEditionShortcutHint = prefs.getBoolean("display-document-edition-shortcut-hint", true);
        final String resultViewPref = prefs.get("result-view", ResultView.TREE_TABLE.name());
        final ResultView rView = ResultView.valueOf(resultViewPref);
        resultViewButtons.get(rView).setSelected(true);
        changeResultView(rView);
        final int pageSize = prefs.getInt("result-view-table-page-size", getTreeTablePages().getPageSize());
        getTreeTablePages().setPageSize(pageSize);
        getFlatTablePages().setPageSize(pageSize);
        pageSizeField.setText(String.valueOf(pageSize));
    }

    public void writePreferences() {
        final Preferences prefs = prefs();
        prefs.put("version", "1.0");
        prefs.putInt("result-view-table-page-size", getTreeTablePages().getPageSize());
        prefs.put("result-view", resultView.name());
        prefs.putBoolean("display-document-edition-shortcut-hint", displayDocumentEditionShortcutHint);
        try {
            prefs.flush();
        } catch (BackingStoreException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    public static interface View {
        
        CollectionResultPages getPages();
    } 
}
