/* 
 * Copyright (C) 2015 Thomas Werner
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
package de.bfg9000.mongonb.ui.core.dialogs;

import com.mongodb.DBCollection;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.text.MessageFormat;
import java.util.ResourceBundle;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import org.netbeans.modules.mongodb.ui.util.table.ColumnGroup;
import org.netbeans.modules.mongodb.ui.util.table.GroupableTableHeader;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.NbBundle;
import org.openide.windows.WindowManager;

/**
 * This dialog can be used to display / manage the indexes of a {@code Collection}.
 *
 * @author thomaswerner35
 */
public class IndexManagerDialog extends javax.swing.JDialog {

    private static final ResourceBundle bundle = NbBundle.getBundle(IndexManagerDialog.class);

    private final IndexManagerModel model;
    
    private final TableModel tableModel;

    /**
     * Creates new form IndexManagerDialog
     */
    public IndexManagerDialog(DBCollection collection) {
        super(WindowManager.getDefault().getMainWindow(), true);
        model = new IndexManagerModel(collection);
        tableModel = new IndexManagerTableModel(collection, model);
        initComponents();
        setUpTableHeaders();
    }

    public void execute() {
        final Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        final Dimension dialog = new Dimension(470, 290);
        setBounds((screen.width -dialog.width) /2, (screen.height -dialog.height) /2, dialog.width, dialog.height);
        setVisible(true);
    }

    private void close(boolean cancelled) {
        if(cancelled) {
            setVisible(false);
            return;
        }

        final IndexManager indexManager = new IndexManager(model);

        final String duplicatesMessage = indexManager.hasDuplicates();
        if(null != duplicatesMessage) {
            final String text = duplicatesMessage +"\n" +bundle.getString("IndexManagerDialog.duplicates.message");
            final NotifyDescriptor question = new NotifyDescriptor.Confirmation(text, NotifyDescriptor.YES_NO_OPTION,
                                              NotifyDescriptor.QUESTION_MESSAGE);
            final Object answer = DialogDisplayer.getDefault().notify(question);
            if(NotifyDescriptor.NO_OPTION == answer)
                return;

            final int duplicateCount = indexManager.removeDuplicates();
            String message = 1 == duplicateCount ? bundle.getString("IndexManagerDialog.duplicates.removed-single") :
                             MessageFormat.format(bundle.getString("IndexManagerDialog.duplicates.removed-multi"),
                             duplicateCount);
            DialogDisplayer.getDefault().notify(new NotifyDescriptor.Message(message,
                                                NotifyDescriptor.INFORMATION_MESSAGE));
        }

        final String emptyMessage = indexManager.hasEmptyIndexes();
        if(null != emptyMessage) {
            final String text = emptyMessage +"\n" +bundle.getString("IndexManagerDialog.empty.message");
            final NotifyDescriptor question = new NotifyDescriptor.Confirmation(text, NotifyDescriptor.YES_NO_OPTION,
                                              NotifyDescriptor.QUESTION_MESSAGE);
            final Object answer = DialogDisplayer.getDefault().notify(question);
            if(NotifyDescriptor.NO_OPTION == answer)
                return;

            final int emptyCount = indexManager.removeEmptyIndexes();
            String message = bundle.getString("IndexManagerDialog.empty.removed-single");
            DialogDisplayer.getDefault().notify(new NotifyDescriptor.Message(message,
                                                NotifyDescriptor.INFORMATION_MESSAGE));
        }

        setVisible(false);
        new Thread(new Runnable() {
            @Override
            public void run() {
                indexManager.modifyCollection();
            }
        }).start();
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlDialogActions = new javax.swing.JPanel();
        btnOK = new javax.swing.JButton();
        btnCancel = new javax.swing.JButton();
        pnlData = new javax.swing.JPanel();
        scrTableIndexes = new javax.swing.JScrollPane();
        tblIndexes = new javax.swing.JTable();
        TableColumnModel tableColumnModel = tblIndexes.getColumnModel();
        pnlDataActions = new javax.swing.JPanel();
        pnlDataActionGrid = new javax.swing.JPanel();
        btnAddIndex = new javax.swing.JButton();
        btnRemoveIndex = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle(org.openide.util.NbBundle.getMessage(IndexManagerDialog.class, "IndexManagerDialog.title")); // NOI18N

        pnlDialogActions.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));

        org.openide.awt.Mnemonics.setLocalizedText(btnOK, org.openide.util.NbBundle.getMessage(IndexManagerDialog.class, "IndexManagerDialog.btnOK.text")); // NOI18N
        btnOK.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOKActionPerformed(evt);
            }
        });
        pnlDialogActions.add(btnOK);

        org.openide.awt.Mnemonics.setLocalizedText(btnCancel, org.openide.util.NbBundle.getMessage(IndexManagerDialog.class, "IndexManagerDialog.btnCancel.text")); // NOI18N
        btnCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelActionPerformed(evt);
            }
        });
        pnlDialogActions.add(btnCancel);

        getContentPane().add(pnlDialogActions, java.awt.BorderLayout.PAGE_END);

        pnlData.setLayout(new java.awt.BorderLayout());

        tblIndexes.setModel(tableModel);
        tblIndexes.setRowHeight(18);
        tblIndexes.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tblIndexes.setTableHeader(new GroupableTableHeader(tableColumnModel));
        tblIndexes.setDefaultRenderer(Boolean.class, new IndexManagerBooleanRenderer());
        tblIndexes.setDefaultRenderer(String.class, new IndexManagerStringRenderer());
        tblIndexes.setDefaultRenderer(IndexManagerTableModel.KeySelection.class, new IndexManagerColumnSelectionRenderer());
        tblIndexes.setDefaultEditor(IndexManagerTableModel.KeySelection.class, new IndexManagerColumnSelectionEditor());
        tblIndexes.getSelectionModel().addListSelectionListener(new TableSelectionListener());
        scrTableIndexes.setViewportView(tblIndexes);

        pnlData.add(scrTableIndexes, java.awt.BorderLayout.CENTER);

        pnlDataActionGrid.setLayout(new java.awt.GridLayout(0, 1, 0, 5));

        org.openide.awt.Mnemonics.setLocalizedText(btnAddIndex, org.openide.util.NbBundle.getMessage(IndexManagerDialog.class, "IndexManagerDialog.btnAddIndex.text")); // NOI18N
        btnAddIndex.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddIndexActionPerformed(evt);
            }
        });
        pnlDataActionGrid.add(btnAddIndex);

        org.openide.awt.Mnemonics.setLocalizedText(btnRemoveIndex, org.openide.util.NbBundle.getMessage(IndexManagerDialog.class, "IndexManagerDialog.btnRemoveIndex.text")); // NOI18N
        btnRemoveIndex.setEnabled(false);
        btnRemoveIndex.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRemoveIndexActionPerformed(evt);
            }
        });
        pnlDataActionGrid.add(btnRemoveIndex);

        pnlDataActions.add(pnlDataActionGrid);

        pnlData.add(pnlDataActions, java.awt.BorderLayout.LINE_END);

        getContentPane().add(pnlData, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnOKActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOKActionPerformed
        close(false);
    }//GEN-LAST:event_btnOKActionPerformed

    private void btnCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelActionPerformed
        close(true);
    }//GEN-LAST:event_btnCancelActionPerformed

    private void btnAddIndexActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddIndexActionPerformed
        model.addIndex();
        tblIndexes.getSelectionModel().setSelectionInterval(0, model.getIndexCount() -1);
    }//GEN-LAST:event_btnAddIndexActionPerformed

    private void btnRemoveIndexActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRemoveIndexActionPerformed
        final int selectedRow = tblIndexes.getSelectedRow();
        model.removeIndex(selectedRow);
        if(selectedRow >= model.getIndexCount())
            tblIndexes.getSelectionModel().setSelectionInterval(0, selectedRow -1);
    }//GEN-LAST:event_btnRemoveIndexActionPerformed

    private void setUpTableHeaders() {
        final TableColumnModel cm = tblIndexes.getColumnModel();
        GroupableTableHeader header = (GroupableTableHeader) tblIndexes.getTableHeader();
        ColumnGroup attributes = new ColumnGroup(bundle.getString("IndexManagerDialog.columnHeader.attributes"));
        for(int col=1; col<cm.getColumnCount() -3; col++) {
            attributes.add(cm.getColumn(col));
        }
        final ColumnGroup options = new ColumnGroup(bundle.getString("IndexManagerDialog.columnHeader.options"));
        for(int col=cm.getColumnCount() -3; col<cm.getColumnCount() ; col++) {
            options.add(cm.getColumn(col));
        }
        header.addColumnGroup(attributes);
        header.addColumnGroup(options);
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAddIndex;
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnOK;
    private javax.swing.JButton btnRemoveIndex;
    private javax.swing.JPanel pnlData;
    private javax.swing.JPanel pnlDataActionGrid;
    private javax.swing.JPanel pnlDataActions;
    private javax.swing.JPanel pnlDialogActions;
    private javax.swing.JScrollPane scrTableIndexes;
    private javax.swing.JTable tblIndexes;
    // End of variables declaration//GEN-END:variables

    private final class TableSelectionListener implements ListSelectionListener {

        @Override
        public void valueChanged(ListSelectionEvent e) {
            if(e.getValueIsAdjusting())
                return;

            final int selectedRow = tblIndexes.getSelectedRow();
            btnRemoveIndex.setEnabled((1 <= selectedRow) && (selectedRow < model.getIndexCount()));
        }

    }

}
