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

import com.mongodb.MongoClientURI;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.JTextComponent;
import org.netbeans.modules.mongodb.ui.util.ValidablePanel;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.NbBundle.Messages;

/**
 *
 * @author Tim Boudreau
 * @author Yann D'Isanto
 */
@Messages("ConnectionNameNotSet=Specify the connection name")
public class NewConnectionPanel extends ValidablePanel implements DocumentListener, FocusListener {

    private static final long serialVersionUID = 1L;

    private MongoClientURI lastValidURI;

    /**
     * Creates new form NewConnectionPanel
     */
    public NewConnectionPanel() {
        initComponents();
        nameField.addFocusListener(this);
        nameField.getDocument().addDocumentListener(this);
        uriField.addFocusListener(this);
        uriField.getDocument().addDocumentListener(this);
        performValidation();
    }

    public String getConnectionName() {
        return nameField.getText().trim();
    }

    public MongoClientURI getMongoURI() {
        return new MongoClientURI(uriField.getText());
    }

    @Override
    public void insertUpdate(DocumentEvent e) {
        performValidation();
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
        performValidation();
    }

    @Override
    public void changedUpdate(DocumentEvent e) {
    }

    @Override
    protected String computeValidationProblem() {
        final String name = getConnectionName();
        if (name.isEmpty()) {
            return Bundle.ConnectionNameNotSet();
        }
        try {
            lastValidURI = new MongoClientURI(uriField.getText());
        } catch (IllegalArgumentException ex) {
            return ex.getLocalizedMessage();
        }
        return null;
    }

    @Override
    public void focusGained(FocusEvent e) {
        ((JTextComponent) e.getComponent()).selectAll();
    }

    @Override
    public void focusLost(FocusEvent e) {
        // do nothing
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        uriLabel = new javax.swing.JLabel();
        uriField = new javax.swing.JTextField();
        nameLabel = new javax.swing.JLabel();
        nameField = new javax.swing.JTextField();
        openURIEditorButton = new javax.swing.JButton();

        uriLabel.setLabelFor(uriField);
        org.openide.awt.Mnemonics.setLocalizedText(uriLabel, org.openide.util.NbBundle.getMessage(NewConnectionPanel.class, "NewConnectionPanel.uriLabel.text")); // NOI18N

        uriField.setText(org.openide.util.NbBundle.getMessage(NewConnectionPanel.class, "NewConnectionPanel.uriField.text")); // NOI18N
        uriField.setToolTipText(org.openide.util.NbBundle.getMessage(NewConnectionPanel.class, "NewConnectionPanel.uriField.toolTipText")); // NOI18N

        nameLabel.setLabelFor(nameField);
        org.openide.awt.Mnemonics.setLocalizedText(nameLabel, org.openide.util.NbBundle.getMessage(NewConnectionPanel.class, "NewConnectionPanel.nameLabel.text")); // NOI18N

        nameField.setText(org.openide.util.NbBundle.getMessage(NewConnectionPanel.class, "NewConnectionPanel.nameField.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(openURIEditorButton, org.openide.util.NbBundle.getMessage(NewConnectionPanel.class, "NewConnectionPanel.openURIEditorButton.text")); // NOI18N
        openURIEditorButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                openURIEditorButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(nameLabel)
                    .addComponent(uriLabel))
                .addGap(14, 14, 14)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(nameField, javax.swing.GroupLayout.DEFAULT_SIZE, 332, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(uriField)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(openURIEditorButton)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(nameLabel)
                    .addComponent(nameField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(uriLabel)
                    .addComponent(uriField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(openURIEditorButton))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void openURIEditorButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_openURIEditorButtonActionPerformed

        final MongoURIEditorPanel editor = new MongoURIEditorPanel(lastValidURI);
        final DialogDescriptor desc = new DialogDescriptor(editor, "Mongo URI Editor");
        editor.setNotificationLineSupport(desc.createNotificationLineSupport());
        editor.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                desc.setValid(editor.valid());
            }
        });
        if (NotifyDescriptor.OK_OPTION.equals(DialogDisplayer.getDefault().notify(desc))) {
            uriField.setText(editor.getMongoURI().getURI());
        }
    }//GEN-LAST:event_openURIEditorButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField nameField;
    private javax.swing.JLabel nameLabel;
    private javax.swing.JButton openURIEditorButton;
    private javax.swing.JTextField uriField;
    private javax.swing.JLabel uriLabel;
    // End of variables declaration//GEN-END:variables

}
