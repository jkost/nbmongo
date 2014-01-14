/*
 * The MIT License
 *
 * Copyright 2014 PVVQ7166.
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
package org.netbeans.modules.nbmongo.ui.wizards;

import com.mongodb.DB;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.event.ChangeListener;
import org.netbeans.modules.nbmongo.ui.QueryEditor;
import org.openide.util.ChangeSupport;
import org.openide.util.NbBundle.Messages;

@Messages({"ExportQueryStep=Query options"})
public final class ExportVisualPanel1 extends JPanel {

    private final ChangeSupport changeSupport = new ChangeSupport(this);

    private final QueryEditor queryEditor = new QueryEditor();

    public ExportVisualPanel1(DB db) {
        initComponents();
        for (String collection : db.getCollectionNames()) {
            collectionComboBox.addItem(collection);
        }
        collectionComboBox.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                changeSupport.fireChange();
            }
        });
    }

    @Override
    public String getName() {
        return Bundle.ExportQueryStep();
    }

    JComboBox<String> getCollectionComboBox() {
        return collectionComboBox;
    }

    QueryEditor getQueryEditor() {
        return queryEditor;
    }

    void updateQueryFieldsFromEditor() {
        final DBObject criteria = queryEditor.getCriteria();
        final DBObject projection = queryEditor.getProjection();
        final DBObject sort = queryEditor.getSort();
        criteriaField.setText(criteria != null ? JSON.serialize(criteria) : "");
        projectionField.setText(projection != null ? JSON.serialize(projection) : "");
        sortField.setText(sort != null ? JSON.serialize(sort) : "");
    }

    public final void addChangeListener(ChangeListener l) {
        changeSupport.addChangeListener(l);
    }

    public final void removeChangeListener(ChangeListener l) {
        changeSupport.removeChangeListener(l);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        collectionLabel = new javax.swing.JLabel();
        collectionComboBox = new javax.swing.JComboBox<String>();
        queryPanel = new javax.swing.JPanel();
        editQueryButton = new javax.swing.JButton();
        clearQueryButton = new javax.swing.JButton();
        sortField = new javax.swing.JTextField();
        sortLabel = new javax.swing.JLabel();
        projectionLabel = new javax.swing.JLabel();
        criteriaLabel = new javax.swing.JLabel();
        criteriaField = new javax.swing.JTextField();
        projectionField = new javax.swing.JTextField();

        org.openide.awt.Mnemonics.setLocalizedText(collectionLabel, org.openide.util.NbBundle.getMessage(ExportVisualPanel1.class, "ExportVisualPanel1.collectionLabel.text")); // NOI18N

        queryPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(ExportVisualPanel1.class, "ExportVisualPanel1.queryPanel.border.title"))); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(editQueryButton, org.openide.util.NbBundle.getMessage(ExportVisualPanel1.class, "ExportVisualPanel1.editQueryButton.text")); // NOI18N
        editQueryButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editQueryButtonActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(clearQueryButton, org.openide.util.NbBundle.getMessage(ExportVisualPanel1.class, "ExportVisualPanel1.clearQueryButton.text")); // NOI18N
        clearQueryButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clearQueryButtonActionPerformed(evt);
            }
        });

        sortField.setEditable(false);

        org.openide.awt.Mnemonics.setLocalizedText(sortLabel, org.openide.util.NbBundle.getMessage(ExportVisualPanel1.class, "ExportVisualPanel1.sortLabel.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(projectionLabel, org.openide.util.NbBundle.getMessage(ExportVisualPanel1.class, "ExportVisualPanel1.projectionLabel.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(criteriaLabel, org.openide.util.NbBundle.getMessage(ExportVisualPanel1.class, "ExportVisualPanel1.criteriaLabel.text")); // NOI18N

        criteriaField.setEditable(false);

        projectionField.setEditable(false);

        javax.swing.GroupLayout queryPanelLayout = new javax.swing.GroupLayout(queryPanel);
        queryPanel.setLayout(queryPanelLayout);
        queryPanelLayout.setHorizontalGroup(
            queryPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(queryPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(queryPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(queryPanelLayout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addComponent(editQueryButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(clearQueryButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 355, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(queryPanelLayout.createSequentialGroup()
                        .addComponent(criteriaLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(criteriaField))
                    .addGroup(queryPanelLayout.createSequentialGroup()
                        .addComponent(projectionLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(projectionField))
                    .addGroup(queryPanelLayout.createSequentialGroup()
                        .addComponent(sortLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(sortField)))
                .addContainerGap())
        );

        queryPanelLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {criteriaLabel, projectionLabel, sortLabel});

        queryPanelLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {clearQueryButton, editQueryButton});

        queryPanelLayout.setVerticalGroup(
            queryPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(queryPanelLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(queryPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(criteriaLabel)
                    .addComponent(criteriaField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(queryPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(projectionLabel)
                    .addComponent(projectionField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(queryPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(sortLabel)
                    .addComponent(sortField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(queryPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(editQueryButton)
                    .addComponent(clearQueryButton)))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(collectionLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(collectionComboBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(queryPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(collectionLabel)
                    .addComponent(collectionComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(queryPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void editQueryButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editQueryButtonActionPerformed
        if (queryEditor.showDialog()) {
            updateQueryFieldsFromEditor();
        }
    }//GEN-LAST:event_editQueryButtonActionPerformed

    private void clearQueryButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clearQueryButtonActionPerformed
        queryEditor.setCriteria(null);
        queryEditor.setProjection(null);
        queryEditor.setSort(null);
        updateQueryFieldsFromEditor();
    }//GEN-LAST:event_clearQueryButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton clearQueryButton;
    private javax.swing.JComboBox<String> collectionComboBox;
    private javax.swing.JLabel collectionLabel;
    private javax.swing.JTextField criteriaField;
    private javax.swing.JLabel criteriaLabel;
    private javax.swing.JButton editQueryButton;
    private javax.swing.JTextField projectionField;
    private javax.swing.JLabel projectionLabel;
    private javax.swing.JPanel queryPanel;
    private javax.swing.JTextField sortField;
    private javax.swing.JLabel sortLabel;
    // End of variables declaration//GEN-END:variables
}
