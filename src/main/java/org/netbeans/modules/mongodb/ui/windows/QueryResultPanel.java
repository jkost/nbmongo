/*
 * The MIT License
 *
 * Copyright 2015 Yann D'Isanto.
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
package org.netbeans.modules.mongodb.ui.windows;

import com.mongodb.DBObject;
import com.mongodb.util.JSON;
import de.bfg9000.mongonb.ui.core.windows.QueryResultWorker;
import de.bfg9000.mongonb.ui.core.windows.ResultCache;
import de.bfg9000.mongonb.ui.core.windows.ResultDisplayer;
import de.bfg9000.mongonb.ui.core.windows.ResultPages;
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
import org.bson.types.ObjectId;
import org.jdesktop.swingx.treetable.TreeTableNode;
import org.netbeans.modules.mongodb.options.JsonCellRenderingOptions;
import org.netbeans.modules.mongodb.options.LabelCategory;
import org.netbeans.modules.mongodb.resources.Images;
import org.netbeans.modules.mongodb.ui.util.IntegerDocumentFilter;
import org.netbeans.modules.mongodb.ui.actions.CopyDocumentToClipboardAction;
import org.netbeans.modules.mongodb.ui.actions.CopyKeyToClipboardAction;
import org.netbeans.modules.mongodb.ui.actions.CopyValueToClipboardAction;
import org.netbeans.modules.mongodb.ui.util.JsonEditor;
import org.netbeans.modules.mongodb.ui.windows.queryresultpanel.actions.AddDocumentAction;
import org.netbeans.modules.mongodb.ui.windows.queryresultpanel.actions.ChangeResultViewAction;
import org.netbeans.modules.mongodb.ui.windows.queryresultpanel.actions.CollapseAllDocumentsAction;
import org.netbeans.modules.mongodb.ui.windows.queryresultpanel.actions.DeleteSelectedDocumentAction;
import org.netbeans.modules.mongodb.ui.windows.queryresultpanel.actions.EditDocumentAction;
import org.netbeans.modules.mongodb.ui.windows.queryresultpanel.actions.EditJsonPropertyNodeAction;
import org.netbeans.modules.mongodb.ui.windows.queryresultpanel.actions.EditJsonValueNodeAction;
import org.netbeans.modules.mongodb.ui.windows.queryresultpanel.actions.EditSelectedDocumentAction;
import org.netbeans.modules.mongodb.ui.windows.queryresultpanel.actions.ExpandAllDocumentsAction;
import org.netbeans.modules.mongodb.ui.windows.queryresultpanel.actions.ExportQueryResultAction;
import org.netbeans.modules.mongodb.ui.windows.queryresultpanel.actions.NavFirstAction;
import org.netbeans.modules.mongodb.ui.windows.queryresultpanel.actions.NavLastAction;
import org.netbeans.modules.mongodb.ui.windows.queryresultpanel.actions.NavLeftAction;
import org.netbeans.modules.mongodb.ui.windows.queryresultpanel.actions.NavRightAction;
import org.netbeans.modules.mongodb.ui.windows.queryresultpanel.actions.RefreshDocumentsAction;
import org.netbeans.modules.mongodb.ui.windows.collectionview.flattable.DocumentsFlatTableModel;
import org.netbeans.modules.mongodb.ui.windows.collectionview.flattable.JsonFlatTableCellRenderer;
import org.netbeans.modules.mongodb.ui.windows.collectionview.treetable.CollectionViewTreeTableNode;
import org.netbeans.modules.mongodb.ui.windows.collectionview.treetable.DocumentNode;
import org.netbeans.modules.mongodb.ui.windows.collectionview.treetable.DocumentTreeTableHighlighter;
import org.netbeans.modules.mongodb.ui.windows.collectionview.treetable.DocumentsTreeTableModel;
import org.netbeans.modules.mongodb.ui.windows.collectionview.treetable.JsonPropertyNode;
import org.netbeans.modules.mongodb.ui.windows.collectionview.treetable.JsonTreeTableCellRenderer;
import org.netbeans.modules.mongodb.ui.windows.collectionview.treetable.JsonValueNode;
import org.netbeans.modules.mongodb.util.JsonProperty;
import org.openide.awt.NotificationDisplayer;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.NbBundle.Messages;
import org.openide.util.NbPreferences;

/**
 *
 * @author Yann D'Isanto
 */
@Messages({
    "displayDocumentTitle=Display document"
})
public final class QueryResultPanel extends javax.swing.JPanel implements ResultDisplayer {

    private static final long serialVersionUID = 1L;

    private static final ResultView DEFAULT_RESULT_VIEW = ResultView.TREE_TABLE;

    @Getter
    private final Lookup lookup;

    @Getter
    private final QueryResultWorkerFactory queryResultWorkerFactory;

    private final Map<ResultView, JToggleButton> resultViewButtons = new EnumMap<>(ResultView.class);

    private ResultView resultView = DEFAULT_RESULT_VIEW;

    private final Map<ResultView, ResultDisplayer.View> resultViews = new EnumMap<>(ResultView.class);

    @Getter
    private final DocumentsTreeTableModel treeTableModel;

    @Getter
    private final DocumentsFlatTableModel flatTableModel;

    @Getter
    @Setter
    private boolean displayDocumentEditionShortcutHint = true;

    @Getter
    @Setter
    private ResultCache resultCache = ResultCache.EMPTY;

    @Getter
    private final boolean readOnly;

    /**
     * Creates new form QueryResultPanel
     */
    public QueryResultPanel(Lookup lookup, QueryResultWorkerFactory queryResultWorkerFactory, final boolean readOnly) {
        this.lookup = lookup;
        this.queryResultWorkerFactory = queryResultWorkerFactory;
        this.readOnly = readOnly;
        initComponents();

        addButton.setVisible(readOnly == false);
        deleteButton.setVisible(readOnly == false);
        editButton.setVisible(readOnly == false);

        resultViewButtons.put(ResultView.FLAT_TABLE, flatTableViewButton);
        resultViewButtons.put(ResultView.TREE_TABLE, treeTableViewButton);

        treeTableModel = new DocumentsTreeTableModel(new ResultPages(resultCache, 20));
        flatTableModel = new DocumentsFlatTableModel(new ResultPages(resultCache, 20));
        resultViews.put(ResultView.TREE_TABLE, treeTableModel);
        resultViews.put(ResultView.FLAT_TABLE, flatTableModel);

        ResultPages.ResultPagesListener pagesListener = new ResultPages.ResultPagesListener() {

            @Override
            public void pageChanged(ResultPages pages, int pageIndex, List<DBObject> page) {
                updatePagination();
                updateDocumentButtonsState();
            }
        };
        getTreeTablePages().addResultPagesListener(pagesListener);

        final ListSelectionListener tableSelectionListener = new ListSelectionListener() {

            @Override
            public void valueChanged(ListSelectionEvent evt) {
                if (!evt.getValueIsAdjusting()) {
                    updateDocumentButtonsState();
                }
            }
        };

        resultFlatTable.setModel(flatTableModel);
        resultFlatTable.setDefaultRenderer(DBObject.class, new JsonFlatTableCellRenderer());
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
        resultTreeTable.setTreeCellRenderer(new JsonTreeTableCellRenderer());
        resultTreeTable.addHighlighter(new DocumentTreeTableHighlighter());
        resultTreeTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        resultTreeTable.getSelectionModel().addListSelectionListener(tableSelectionListener);

        final PlainDocument document = (PlainDocument) pageSizeField.getDocument();
        document.setDocumentFilter(new IntegerDocumentFilter());

        resultTreeTable.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && SwingUtilities.isLeftMouseButton(e)) {
                    final TreePath path = resultTreeTable.getPathForLocation(e.getX(), e.getY());
                    final TreeTableNode node = (TreeTableNode) path.getLastPathComponent();

                    if ((e.getModifiersEx() & MouseEvent.CTRL_DOWN_MASK) == MouseEvent.CTRL_DOWN_MASK) {
                        final DocumentNode documentNode = (DocumentNode) path.getPathComponent(1);
                        if (readOnly) {
                            JsonEditor.showReadOnly(
                                Bundle.displayDocumentTitle(),
                                JSON.serialize(documentNode.getUserObject()));
                        } else {
                            editDocumentAction.setDocument(documentNode.getUserObject());
                            editDocumentAction.actionPerformed(null);
                        }
                    } else {
                        if (node.isLeaf()) {
                            if (readOnly == false) {
                                dislayDocumentEditionShortcutHintIfNecessary();
                                if (node instanceof JsonPropertyNode) {
                                    JsonPropertyNode propertyNode = (JsonPropertyNode) node;
                                    if (!(propertyNode.getUserObject().getValue() instanceof ObjectId)) {
                                        editJsonPropertyNodeAction.setPropertyNode(propertyNode);
                                        editJsonPropertyNodeAction.actionPerformed(null);
                                    }
                                } else if (node instanceof JsonValueNode) {
                                    editJsonValueNodeAction.setValueNode((JsonValueNode) node);
                                    editJsonValueNodeAction.actionPerformed(null);
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
                if (e.isPopupTrigger()) {
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
        updatePagination();
        updateDocumentButtonsState();
    }

    public void refreshResults() {
        QueryResultWorker worker = queryResultWorkerFactory.createWorker();
        worker.execute();
        worker.setResultDisplayer(this);
    }

    @Override
    public void updateData(ResultCache resultCache, boolean isReloadable) {
        this.resultCache = resultCache;
        getFlatTablePages().setCache(resultCache);
        getTreeTablePages().setCache(resultCache);
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

    public ResultPages getFlatTablePages() {
        return flatTableModel.getPages();
    }

    public ResultPages getTreeTablePages() {
        return treeTableModel.getPages();
    }

    public ResultPages getResultPages() {
        return resultViews.get(resultView).getPages();
    }

    public DBObject getResultTableSelectedDocument() {
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
                final DocumentNode documentNode = (DocumentNode) selectionPath.getPathComponent(1);
                return documentNode.getUserObject();
            default:
                throw new AssertionError();
        }
    }

    public void changeResultView(ResultView resultView) {
        this.resultView = resultView;
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
                ResultPages pages = getResultPages();
                totalDocumentsLabel.setText(
                    Bundle.totalDocuments(pages.getTotalElementsCount()));
                int page = pages.getPageIndex();
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
//        getResultPages().setPageSize(pageSize);
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

        treeTableViewButton.setAction(getTreeTableViewAction());
        resultViewButtonGroup.add(treeTableViewButton);
        treeTableViewButton.setFocusable(false);
        treeTableViewButton.setHideActionText(true);
        treeTableViewButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        treeTableViewButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        documentsToolBar.add(treeTableViewButton);

        flatTableViewButton.setAction(getFlatTableViewAction());
        resultViewButtonGroup.add(flatTableViewButton);
        flatTableViewButton.setFocusable(false);
        flatTableViewButton.setHideActionText(true);
        flatTableViewButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        flatTableViewButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        documentsToolBar.add(flatTableViewButton);
        documentsToolBar.add(jSeparator4);

        expandTreeButton.setAction(getExpandTreeAction());
        expandTreeButton.setFocusable(false);
        expandTreeButton.setHideActionText(true);
        expandTreeButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        expandTreeButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        documentsToolBar.add(expandTreeButton);

        collapseTreeButton.setAction(getCollapseTreeAction());
        collapseTreeButton.setFocusable(false);
        collapseTreeButton.setHideActionText(true);
        collapseTreeButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        collapseTreeButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        documentsToolBar.add(collapseTreeButton);
        documentsToolBar.add(jSeparator5);

        addButton.setAction(getAddDocumentAction());
        addButton.setFocusable(false);
        addButton.setHideActionText(true);
        addButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        addButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        documentsToolBar.add(addButton);

        deleteButton.setAction(getDeleteSelectedDocumentAction());
        deleteButton.setFocusable(false);
        deleteButton.setHideActionText(true);
        deleteButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        deleteButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        documentsToolBar.add(deleteButton);

        editButton.setAction(getEditSelectedDocumentAction());
        editButton.setFocusable(false);
        editButton.setHideActionText(true);
        editButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        editButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        documentsToolBar.add(editButton);

        exportButton.setAction(getExportQueryResultAction());
        exportButton.setFocusable(false);
        exportButton.setHideActionText(true);
        exportButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        exportButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        documentsToolBar.add(exportButton);
        documentsToolBar.add(jSeparator1);

        refreshDocumentsButton.setAction(getRefreshDocumentsAction());
        refreshDocumentsButton.setFocusable(false);
        refreshDocumentsButton.setHideActionText(true);
        refreshDocumentsButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        refreshDocumentsButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        documentsToolBar.add(refreshDocumentsButton);

        navFirstButton.setAction(getNavFirstAction());
        navFirstButton.setHideActionText(true);
        documentsToolBar.add(navFirstButton);

        navLeftButton.setAction(getNavLeftAction());
        navLeftButton.setHideActionText(true);
        documentsToolBar.add(navLeftButton);

        navRightButton.setAction(getNavRightAction());
        navRightButton.setFocusable(false);
        navRightButton.setHideActionText(true);
        navRightButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        navRightButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        documentsToolBar.add(navRightButton);

        navLastButton.setAction(getNavLastAction());
        navLastButton.setFocusable(false);
        navLastButton.setHideActionText(true);
        navLastButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        navLastButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        documentsToolBar.add(navLastButton);
        documentsToolBar.add(jSeparator2);

        org.openide.awt.Mnemonics.setLocalizedText(pageSizeLabel, org.openide.util.NbBundle.getMessage(QueryResultPanel.class, "QueryResultPanel.pageSizeLabel.text")); // NOI18N
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

    @Getter
    private final Action addDocumentAction = new AddDocumentAction(this);

    @Getter
    private final Action deleteSelectedDocumentAction = new DeleteSelectedDocumentAction(this);

    @Getter
    private final EditDocumentAction editDocumentAction = new EditDocumentAction(this);

    @Getter
    private final Action editSelectedDocumentAction = new EditSelectedDocumentAction(this);

    @Getter
    private final EditJsonPropertyNodeAction editJsonPropertyNodeAction = new EditJsonPropertyNodeAction(this, null);

    @Getter
    private final EditJsonValueNodeAction editJsonValueNodeAction = new EditJsonValueNodeAction(this, null);

    @Getter
    private final Action refreshDocumentsAction = new RefreshDocumentsAction(this);

    @Getter
    private final Action navFirstAction = new NavFirstAction(this);

    @Getter
    private final Action navLeftAction = new NavLeftAction(this);

    @Getter
    private final Action navRightAction = new NavRightAction(this);

    @Getter
    private final Action navLastAction = new NavLastAction(this);

    @Getter
    private final Action exportQueryResultAction = new ExportQueryResultAction(this);

    @Getter
    private final Action treeTableViewAction = ChangeResultViewAction.create(this, ResultView.TREE_TABLE);

    @Getter
    private final Action flatTableViewAction = ChangeResultViewAction.create(this, ResultView.FLAT_TABLE);

    @Getter
    private final Action collapseTreeAction = new CollapseAllDocumentsAction(this);

    @Getter
    private final Action expandTreeAction = new ExpandAllDocumentsAction(this);

    public enum ResultView {

        FLAT_TABLE, TREE_TABLE

    }

    public static interface QueryResultWorkerFactory {

        QueryResultWorker createWorker();

    }

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

    public boolean isQuickEditableJsonValue(Object value) {
        if (readOnly) {
            return false;
        }
        return !(value instanceof Map || value instanceof List || value instanceof ObjectId);
    }

    private JPopupMenu createTreeTableContextMenu(TreePath treePath) {
        final JPopupMenu menu = new JPopupMenu();
        if (treePath != null) {
            final CollectionViewTreeTableNode node = (CollectionViewTreeTableNode) treePath.getLastPathComponent();
            final DocumentNode documentNode = (DocumentNode) treePath.getPathComponent(1);
            menu.add(new JMenuItem(new CopyDocumentToClipboardAction(documentNode.getUserObject())));
            if (node != documentNode) {
                if (node instanceof JsonPropertyNode) {
                    JsonPropertyNode propertyNode = (JsonPropertyNode) node;
                    JsonProperty property = propertyNode.getUserObject();
                    menu.add(new JMenuItem(new CopyKeyToClipboardAction(property)));
                    menu.add(new JMenuItem(new CopyValueToClipboardAction(property.getValue())));
                    if (isQuickEditableJsonValue(property.getValue())) {
                        editJsonPropertyNodeAction.setPropertyNode(propertyNode);
                        menu.add(new JMenuItem(editJsonPropertyNodeAction));
                    }
                } else {
                    Object value = node.getUserObject();
                    menu.add(new JMenuItem(new CopyValueToClipboardAction(value)));
                    if (isQuickEditableJsonValue(value)) {
                        editJsonValueNodeAction.setValueNode((JsonValueNode) node);
                        menu.add(new JMenuItem(editJsonValueNodeAction));
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
        final DBObject document = getFlatTablePages().getPageContent().get(row);
        menu.add(new JMenuItem(new CopyDocumentToClipboardAction(document)));
        final DocumentsFlatTableModel model = (DocumentsFlatTableModel) resultFlatTable.getModel();
        final JsonProperty property = new JsonProperty(
            model.getColumnName(column),
            model.getValueAt(row, column));
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
        return NbPreferences.forModule(QueryResultPanel.class).node(QueryResultPanel.class.getName());
    }

    void loadPreferences() {
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

    void writePreferences() {
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

}
