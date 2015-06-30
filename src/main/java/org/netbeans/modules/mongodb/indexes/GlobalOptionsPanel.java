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
package org.netbeans.modules.mongodb.indexes;

import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import org.bson.Document;
import org.netbeans.modules.mongodb.indexes.Index.GlobalOptions;
import org.netbeans.modules.mongodb.ui.util.JsonEditor;
import org.openide.util.NbBundle.Messages;

/**
 *
 * @author Yann D'Isanto
 */
@Messages({
    "storageEngineEditorTitle=Storage engine document"
})
public class GlobalOptionsPanel extends javax.swing.JPanel {

    /**
     * Creates new form GlobalOptionsPanel
     */
    public GlobalOptionsPanel() {
        initComponents();
    }

    public GlobalOptions getGlobalOptions() {
        boolean background = backgroundCheckBox.isSelected();
        boolean unique = uniqueCheckBox.isSelected();
        boolean sparse = sparseCheckBox.isSelected();
        Long expireAfterSeconds = expireAfterCheckBox.isSelected() ? ((Number) expireAfterSpinner.getValue()).longValue() : null;
        Integer indexVersion = indexVersionCheckBox.isSelected() ? (Integer) indexVersionSpinner.getValue() : null;
        String seJson = storageEngineField.getText().trim();
        Document storageEngine = seJson.isEmpty()? null : Document.parse(seJson);
        return new GlobalOptions(background, unique, sparse, expireAfterSeconds, indexVersion, storageEngine);
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        sparseCheckBox = new javax.swing.JCheckBox();
        uniqueCheckBox = new javax.swing.JCheckBox();
        backgroundCheckBox = new javax.swing.JCheckBox();
        expireAfterCheckBox = new javax.swing.JCheckBox();
        expireAfterSpinner = new JSpinner(new SpinnerNumberModel(0, 0, Integer.MAX_VALUE, 1));
        secondsLabel = new javax.swing.JLabel();
        indexVersionCheckBox = new javax.swing.JCheckBox();
        indexVersionSpinner = new javax.swing.JSpinner();
        storageEngineLabel = new javax.swing.JLabel();
        storageEngineField = new javax.swing.JTextField();
        editStorageEngineButton = new javax.swing.JButton();
        clearStorageEngineButton = new javax.swing.JButton();

        org.openide.awt.Mnemonics.setLocalizedText(sparseCheckBox, org.openide.util.NbBundle.getMessage(GlobalOptionsPanel.class, "GlobalOptionsPanel.sparseCheckBox.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(uniqueCheckBox, org.openide.util.NbBundle.getMessage(GlobalOptionsPanel.class, "GlobalOptionsPanel.uniqueCheckBox.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(backgroundCheckBox, org.openide.util.NbBundle.getMessage(GlobalOptionsPanel.class, "GlobalOptionsPanel.backgroundCheckBox.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(expireAfterCheckBox, org.openide.util.NbBundle.getMessage(GlobalOptionsPanel.class, "GlobalOptionsPanel.expireAfterCheckBox.text")); // NOI18N
        expireAfterCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                expireAfterCheckBoxActionPerformed(evt);
            }
        });

        expireAfterSpinner.setEnabled(false);

        org.openide.awt.Mnemonics.setLocalizedText(secondsLabel, org.openide.util.NbBundle.getMessage(GlobalOptionsPanel.class, "GlobalOptionsPanel.secondsLabel.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(indexVersionCheckBox, org.openide.util.NbBundle.getMessage(GlobalOptionsPanel.class, "GlobalOptionsPanel.indexVersionCheckBox.text")); // NOI18N
        indexVersionCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                indexVersionCheckBoxActionPerformed(evt);
            }
        });

        indexVersionSpinner.setModel(new javax.swing.SpinnerNumberModel(2, 1, 2, 1));
        indexVersionSpinner.setEnabled(false);

        org.openide.awt.Mnemonics.setLocalizedText(storageEngineLabel, org.openide.util.NbBundle.getMessage(GlobalOptionsPanel.class, "GlobalOptionsPanel.storageEngineLabel.text")); // NOI18N

        storageEngineField.setEditable(false);

        org.openide.awt.Mnemonics.setLocalizedText(editStorageEngineButton, org.openide.util.NbBundle.getMessage(GlobalOptionsPanel.class, "GlobalOptionsPanel.editStorageEngineButton.text")); // NOI18N
        editStorageEngineButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editStorageEngineButtonActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(clearStorageEngineButton, org.openide.util.NbBundle.getMessage(GlobalOptionsPanel.class, "GlobalOptionsPanel.clearStorageEngineButton.text")); // NOI18N
        clearStorageEngineButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clearStorageEngineButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(indexVersionCheckBox)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(indexVersionSpinner))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(uniqueCheckBox, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(expireAfterCheckBox))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(expireAfterSpinner)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(secondsLabel))
                    .addComponent(backgroundCheckBox)
                    .addComponent(sparseCheckBox)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(storageEngineLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(storageEngineField, javax.swing.GroupLayout.PREFERRED_SIZE, 171, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(editStorageEngineButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(clearStorageEngineButton)))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {backgroundCheckBox, expireAfterCheckBox, indexVersionCheckBox, sparseCheckBox, storageEngineLabel, uniqueCheckBox});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(backgroundCheckBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(uniqueCheckBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(sparseCheckBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(expireAfterCheckBox)
                    .addComponent(expireAfterSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(secondsLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(indexVersionSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(indexVersionCheckBox))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(storageEngineLabel)
                    .addComponent(editStorageEngineButton)
                    .addComponent(storageEngineField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(clearStorageEngineButton))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void expireAfterCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_expireAfterCheckBoxActionPerformed
        expireAfterSpinner.setEnabled(expireAfterCheckBox.isSelected());
    }//GEN-LAST:event_expireAfterCheckBoxActionPerformed

    private void indexVersionCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_indexVersionCheckBoxActionPerformed
        indexVersionSpinner.setEnabled(indexVersionCheckBox.isSelected());
    }//GEN-LAST:event_indexVersionCheckBoxActionPerformed

    private void editStorageEngineButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editStorageEngineButtonActionPerformed
        String json = storageEngineField.getText().trim();
        Document document = JsonEditor.show(Bundle.storageEngineEditorTitle(), json.isEmpty() ? null : Document.parse(json));
        if(document != null) {
            storageEngineField.setText(document.toJson());
        }
    }//GEN-LAST:event_editStorageEngineButtonActionPerformed

    private void clearStorageEngineButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clearStorageEngineButtonActionPerformed
        storageEngineField.setText("");
    }//GEN-LAST:event_clearStorageEngineButtonActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox backgroundCheckBox;
    private javax.swing.JButton clearStorageEngineButton;
    private javax.swing.JButton editStorageEngineButton;
    private javax.swing.JCheckBox expireAfterCheckBox;
    private javax.swing.JSpinner expireAfterSpinner;
    private javax.swing.JCheckBox indexVersionCheckBox;
    private javax.swing.JSpinner indexVersionSpinner;
    private javax.swing.JLabel secondsLabel;
    private javax.swing.JCheckBox sparseCheckBox;
    private javax.swing.JTextField storageEngineField;
    private javax.swing.JLabel storageEngineLabel;
    private javax.swing.JCheckBox uniqueCheckBox;
    // End of variables declaration//GEN-END:variables
}
