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
package org.netbeans.modules.mongodb.ui.options;

import java.awt.Font;
import java.beans.PropertyEditor;
import java.beans.PropertyEditorManager;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bson.BsonType;
import org.netbeans.modules.mongodb.options.MongoNativeToolsOptions;
import org.netbeans.modules.mongodb.options.RenderingOptions;
import org.netbeans.modules.mongodb.options.RenderingOptions.PrefsRenderingOptions;
import org.netbeans.modules.mongodb.options.RenderingOptions.RenderingOptionsItem;
import org.netbeans.modules.mongodb.options.RenderingOptions.RenderingOptionsItem.RenderingOptionsItemBuilder;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.awt.ColorComboBox;
import org.openide.filesystems.FileChooserBuilder;
import org.openide.util.NbBundle;
import org.openide.util.NbBundle.Messages;

/**
 * @author Yann D'Isanto
 */
@Messages({
    "RenderingOptionItemKey_DEFAULT_RENDERING=Default",
    "RenderingOptionItemKey_COMMENT=Comment",
    "RenderingOptionItemKey_KEY=Key",
    "RenderingOptionItemKey_DOCUMENT_ROOT=Document root",
    "RenderingOptionItemKey_DOCUMENT_ID=Document _id",
    "RenderingOptionItemKey_OBJECT_ID=ObjectId",
    "RenderingOptionItemKey_STRING=String",
    "RenderingOptionItemKey_INT32=Integer",
    "RenderingOptionItemKey_INT64=64 bits integer",
    "RenderingOptionItemKey_DOUBLE=Double",
    "RenderingOptionItemKey_BOOLEAN=Boolean",
    "RenderingOptionItemKey_ARRAY=Array",
    "RenderingOptionItemKey_NULL=Null",
    "RenderingOptionItemKey_UNDEFINED=Undefined"
})
final class MongoOptionsPanel extends javax.swing.JPanel {

    private final MongoNativeToolsOptions mongoToolsOptions = MongoNativeToolsOptions.INSTANCE;

    private final MongoOptionsPanelController controller;

    private final PropertyEditor fontEditor = PropertyEditorManager.findEditor(Font.class);

    private final PrefsRenderingOptions prefsRenderingOptions = PrefsRenderingOptions.INSTANCE;
    private final Map<RenderingOptionItemKey, RenderingOptionsItemBuilder> renderingOptionsBuilders = new HashMap<>();

    private boolean internalUpdate = false;

    MongoOptionsPanel(MongoOptionsPanelController controller) {
        this.controller = controller;
        initComponents();
        internalUpdate = true;
        categoriesList.setListData(RenderingOptionItemKey.values());
        categoriesList.setSelectedIndex(0);
        loadRenderingOptions(prefsRenderingOptions);
        internalUpdate = false;

        categoriesList.addListSelectionListener(new ListSelectionListener() {

            @Override
            public void valueChanged(ListSelectionEvent e) {
                updateSelectedLabelFontConfUI();
            }
        });
        mongoToolsFolderPathField.getDocument().addDocumentListener(new DocumentListener() {

            @Override
            public void insertUpdate(DocumentEvent e) {
                fireChangeEvent();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                fireChangeEvent();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                fireChangeEvent();
            }
        });
    }

    private void updateSelectedLabelFontConfUI() {
        loadRenderingOptionsInUI(getSelectedRenderingOptionsBuilder().build());
    }

    private RenderingOptionsItemBuilder getSelectedRenderingOptionsBuilder() {
        final RenderingOptionItemKey category = categoriesList.getSelectedValue();
        return renderingOptionsBuilders.get(category);
    }

    private void loadRenderingOptionsInUI(RenderingOptionsItem renderingOptions) {
        final Font font = renderingOptions.getFont();
        fontEditor.setValue(font);
        fontField.setFont(font);
        fontField.setText(fontEditor.getAsText());
        ((ColorComboBox) foregroundComboBox).setSelectedColor(renderingOptions.getForeground());
        ((ColorComboBox) backgroundComboBox).setSelectedColor(renderingOptions.getBackground());
    }

    private void fireChangeEvent() {
        if (internalUpdate == false) {
            controller.changed();
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jsonTreeRenderingOptionsPanel = new javax.swing.JPanel();
        renderingOptionsPanel = new javax.swing.JPanel();
        fontLabel = new javax.swing.JLabel();
        backgroundComboBox = new ColorComboBox();
        foregroundLabel = new javax.swing.JLabel();
        foregroundComboBox = new ColorComboBox();
        backgroundLabel = new javax.swing.JLabel();
        fontField = new javax.swing.JTextField();
        browseFontButton = new javax.swing.JButton();
        categoriesScrollPane = new javax.swing.JScrollPane();
        categoriesList = new javax.swing.JList<RenderingOptionItemKey>();
        categoriesLabel = new javax.swing.JLabel();
        restoreDefaultRenderingLabel = new javax.swing.JLabel();
        restoreDefaultRenderingButton = new javax.swing.JButton();
        shellOptionsPanel = new javax.swing.JPanel();
        mongoToolsFolderPathLabel = new javax.swing.JLabel();
        mongoToolsFolderPathField = new javax.swing.JTextField();
        browseMongoToolsFolderPathButton = new javax.swing.JButton();

        jsonTreeRenderingOptionsPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(MongoOptionsPanel.class, "MongoOptionsPanel.jsonTreeRenderingOptionsPanel.border.title"))); // NOI18N

        renderingOptionsPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        org.openide.awt.Mnemonics.setLocalizedText(fontLabel, org.openide.util.NbBundle.getMessage(MongoOptionsPanel.class, "MongoOptionsPanel.fontLabel.text")); // NOI18N

        backgroundComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                backgroundComboBoxActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(foregroundLabel, org.openide.util.NbBundle.getMessage(MongoOptionsPanel.class, "MongoOptionsPanel.foregroundLabel.text")); // NOI18N

        foregroundComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                foregroundComboBoxActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(backgroundLabel, org.openide.util.NbBundle.getMessage(MongoOptionsPanel.class, "MongoOptionsPanel.backgroundLabel.text")); // NOI18N

        fontField.setEditable(false);
        fontField.setText(org.openide.util.NbBundle.getMessage(MongoOptionsPanel.class, "MongoOptionsPanel.fontField.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(browseFontButton, org.openide.util.NbBundle.getMessage(MongoOptionsPanel.class, "MongoOptionsPanel.browseFontButton.text")); // NOI18N
        browseFontButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                browseFontButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout renderingOptionsPanelLayout = new javax.swing.GroupLayout(renderingOptionsPanel);
        renderingOptionsPanel.setLayout(renderingOptionsPanelLayout);
        renderingOptionsPanelLayout.setHorizontalGroup(
            renderingOptionsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(renderingOptionsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(renderingOptionsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(renderingOptionsPanelLayout.createSequentialGroup()
                        .addComponent(fontLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(fontField)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(browseFontButton))
                    .addGroup(renderingOptionsPanelLayout.createSequentialGroup()
                        .addComponent(backgroundLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(backgroundComboBox, 0, 214, Short.MAX_VALUE))
                    .addGroup(renderingOptionsPanelLayout.createSequentialGroup()
                        .addComponent(foregroundLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(foregroundComboBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );

        renderingOptionsPanelLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {backgroundLabel, foregroundLabel});

        renderingOptionsPanelLayout.setVerticalGroup(
            renderingOptionsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(renderingOptionsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(renderingOptionsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(fontLabel)
                    .addComponent(fontField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(browseFontButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(renderingOptionsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(foregroundLabel)
                    .addComponent(foregroundComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(renderingOptionsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(backgroundLabel)
                    .addComponent(backgroundComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        categoriesScrollPane.setViewportView(categoriesList);

        org.openide.awt.Mnemonics.setLocalizedText(categoriesLabel, org.openide.util.NbBundle.getMessage(MongoOptionsPanel.class, "MongoOptionsPanel.categoriesLabel.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(restoreDefaultRenderingLabel, org.openide.util.NbBundle.getMessage(MongoOptionsPanel.class, "MongoOptionsPanel.restoreDefaultRenderingLabel.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(restoreDefaultRenderingButton, org.openide.util.NbBundle.getMessage(MongoOptionsPanel.class, "MongoOptionsPanel.restoreDefaultRenderingButton.text")); // NOI18N
        restoreDefaultRenderingButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                restoreDefaultRenderingButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jsonTreeRenderingOptionsPanelLayout = new javax.swing.GroupLayout(jsonTreeRenderingOptionsPanel);
        jsonTreeRenderingOptionsPanel.setLayout(jsonTreeRenderingOptionsPanelLayout);
        jsonTreeRenderingOptionsPanelLayout.setHorizontalGroup(
            jsonTreeRenderingOptionsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jsonTreeRenderingOptionsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jsonTreeRenderingOptionsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jsonTreeRenderingOptionsPanelLayout.createSequentialGroup()
                        .addComponent(categoriesScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(renderingOptionsPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jsonTreeRenderingOptionsPanelLayout.createSequentialGroup()
                        .addComponent(categoriesLabel)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jsonTreeRenderingOptionsPanelLayout.createSequentialGroup()
                        .addComponent(restoreDefaultRenderingLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(restoreDefaultRenderingButton)))
                .addContainerGap())
        );
        jsonTreeRenderingOptionsPanelLayout.setVerticalGroup(
            jsonTreeRenderingOptionsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jsonTreeRenderingOptionsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jsonTreeRenderingOptionsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(restoreDefaultRenderingLabel)
                    .addComponent(restoreDefaultRenderingButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(categoriesLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jsonTreeRenderingOptionsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(renderingOptionsPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(categoriesScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addContainerGap())
        );

        shellOptionsPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(MongoOptionsPanel.class, "MongoOptionsPanel.shellOptionsPanel.border.title"))); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(mongoToolsFolderPathLabel, org.openide.util.NbBundle.getMessage(MongoOptionsPanel.class, "MongoOptionsPanel.mongoToolsFolderPathLabel.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(browseMongoToolsFolderPathButton, org.openide.util.NbBundle.getMessage(MongoOptionsPanel.class, "MongoOptionsPanel.browseMongoToolsFolderPathButton.text")); // NOI18N
        browseMongoToolsFolderPathButton.setActionCommand(org.openide.util.NbBundle.getMessage(MongoOptionsPanel.class, "MongoOptionsPanel.browseMongoToolsFolderPathButton.actionCommand")); // NOI18N
        browseMongoToolsFolderPathButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                browseMongoToolsFolderPathButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout shellOptionsPanelLayout = new javax.swing.GroupLayout(shellOptionsPanel);
        shellOptionsPanel.setLayout(shellOptionsPanelLayout);
        shellOptionsPanelLayout.setHorizontalGroup(
            shellOptionsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(shellOptionsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(shellOptionsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(shellOptionsPanelLayout.createSequentialGroup()
                        .addComponent(mongoToolsFolderPathLabel)
                        .addGap(0, 241, Short.MAX_VALUE))
                    .addGroup(shellOptionsPanelLayout.createSequentialGroup()
                        .addComponent(mongoToolsFolderPathField)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(browseMongoToolsFolderPathButton)))
                .addContainerGap())
        );
        shellOptionsPanelLayout.setVerticalGroup(
            shellOptionsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(shellOptionsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(mongoToolsFolderPathLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(shellOptionsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(mongoToolsFolderPathField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(browseMongoToolsFolderPathButton))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jsonTreeRenderingOptionsPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(shellOptionsPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jsonTreeRenderingOptionsPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(shellOptionsPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void browseFontButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_browseFontButtonActionPerformed
        fontEditor.setValue(fontField.getFont());
        final DialogDescriptor dd = new DialogDescriptor(
                fontEditor.getCustomEditor(),
                "Select Font" // NOI18N
        );

        DialogDisplayer.getDefault().createDialog(dd).setVisible(true);
        if (dd.getValue() == DialogDescriptor.OK_OPTION) {
            final Font font = (Font) fontEditor.getValue();
            fontField.setFont(font);
            fontField.setText(fontEditor.getAsText());
            fireChangeEvent();
            getSelectedRenderingOptionsBuilder().font(font);
        }

    }//GEN-LAST:event_browseFontButtonActionPerformed

    private void foregroundComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_foregroundComboBoxActionPerformed
        getSelectedRenderingOptionsBuilder().foreground(((ColorComboBox) foregroundComboBox).getSelectedColor());
        fireChangeEvent();
    }//GEN-LAST:event_foregroundComboBoxActionPerformed

    private void backgroundComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_backgroundComboBoxActionPerformed
        getSelectedRenderingOptionsBuilder().background(((ColorComboBox) backgroundComboBox).getSelectedColor());
        fireChangeEvent();
    }//GEN-LAST:event_backgroundComboBoxActionPerformed

    private void browseMongoToolsFolderPathButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_browseMongoToolsFolderPathButtonActionPerformed
        final String mongoHomePath = System.getenv("MONGO_HOME");
        final FileChooserBuilder fcb = new FileChooserBuilder(MongoNativeToolsOptions.class);
        fcb.setDirectoriesOnly(true);
        fcb.setSelectionApprover(new MongoToolsFolderSelectionApprover());
        final String mongoToolsFolderPath = mongoToolsFolderPathField.getText().trim();
        if (mongoToolsFolderPath.isEmpty() == false) {
            fcb.setDefaultWorkingDirectory(new File(mongoToolsFolderPath));
        } else if (mongoHomePath != null) {
            final File mongoHome = new File(mongoHomePath);
            if (mongoHome.isDirectory()) {
                fcb.setDefaultWorkingDirectory(mongoHome);
            }
        }
        final File file = fcb.showOpenDialog();
        if (file != null) {
            mongoToolsFolderPathField.setText(file.getAbsolutePath());
        }
    }//GEN-LAST:event_browseMongoToolsFolderPathButtonActionPerformed

    private void restoreDefaultRenderingButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_restoreDefaultRenderingButtonActionPerformed
        loadRenderingOptions(RenderingOptions.DEFAULT);
    }//GEN-LAST:event_restoreDefaultRenderingButtonActionPerformed

    void load() {
        internalUpdate = true;
        loadRenderingOptions(prefsRenderingOptions);

        final String mongoToolsFolderPath = mongoToolsOptions.getToolsFolder();
        if (mongoToolsFolderPath != null) {
            mongoToolsFolderPathField.setText(mongoToolsFolderPath);
        }
        internalUpdate = false;
    }

    private void loadRenderingOptions(RenderingOptions renderingOptions) {
        renderingOptionsBuilders.put(RenderingOptionItemKey.DEFAULT_RENDERING, renderingOptions.fallback().asBuilder());
        renderingOptionsBuilders.put(RenderingOptionItemKey.COMMENT, renderingOptions.comment().asBuilder());
        renderingOptionsBuilders.put(RenderingOptionItemKey.KEY, renderingOptions.key().asBuilder());
        renderingOptionsBuilders.put(RenderingOptionItemKey.DOCUMENT_ROOT, renderingOptions.documentRoot().asBuilder());
        renderingOptionsBuilders.put(RenderingOptionItemKey.DOCUMENT_ID, renderingOptions.documentId().asBuilder());
        for (RenderingOptionItemKey key : RenderingOptionItemKey.values()) {
            if(key.isBsonType()) {
                renderingOptionsBuilders.put(key, renderingOptions.get(BsonType.valueOf(key.name())).asBuilder());
            }
        }
        updateSelectedLabelFontConfUI();
    }
    
    void store() {
        RenderingOptionsItem options = renderingOptionsBuilders.get(RenderingOptionItemKey.DEFAULT_RENDERING).build();
        prefsRenderingOptions.setKey(options);
        options = renderingOptionsBuilders.get(RenderingOptionItemKey.COMMENT).build();
        prefsRenderingOptions.setComment(options);
        options = renderingOptionsBuilders.get(RenderingOptionItemKey.KEY).build();
        prefsRenderingOptions.setKey(options);
        options = renderingOptionsBuilders.get(RenderingOptionItemKey.DOCUMENT_ROOT).build();
        prefsRenderingOptions.setDocumentRoot(options);
        options = renderingOptionsBuilders.get(RenderingOptionItemKey.DOCUMENT_ID).build();
        prefsRenderingOptions.setDocumentId(options);
        for (RenderingOptionItemKey key : RenderingOptionItemKey.values()) {
            if(key.isBsonType()) {
                options = renderingOptionsBuilders.get(key).build();
                prefsRenderingOptions.set(BsonType.valueOf(key.name()), options);
            }
        }
        
        prefsRenderingOptions.store();

        final String mongoToolsFolderPath = mongoToolsFolderPathField.getText().trim();
        mongoToolsOptions.setToolsFolder(mongoToolsFolderPath.isEmpty() ? null : mongoToolsFolderPath);
        mongoToolsOptions.store();
    }

    boolean valid() {
        final String mongoExecPath = mongoToolsFolderPathField.getText().trim();
        if (mongoExecPath.isEmpty() == false) {
            return new File(mongoExecPath).isDirectory();
        }
        return true;
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox backgroundComboBox;
    private javax.swing.JLabel backgroundLabel;
    private javax.swing.JButton browseFontButton;
    private javax.swing.JButton browseMongoToolsFolderPathButton;
    private javax.swing.JLabel categoriesLabel;
    private javax.swing.JList<RenderingOptionItemKey> categoriesList;
    private javax.swing.JScrollPane categoriesScrollPane;
    private javax.swing.JTextField fontField;
    private javax.swing.JLabel fontLabel;
    private javax.swing.JComboBox foregroundComboBox;
    private javax.swing.JLabel foregroundLabel;
    private javax.swing.JPanel jsonTreeRenderingOptionsPanel;
    private javax.swing.JTextField mongoToolsFolderPathField;
    private javax.swing.JLabel mongoToolsFolderPathLabel;
    private javax.swing.JPanel renderingOptionsPanel;
    private javax.swing.JButton restoreDefaultRenderingButton;
    private javax.swing.JLabel restoreDefaultRenderingLabel;
    private javax.swing.JPanel shellOptionsPanel;
    // End of variables declaration//GEN-END:variables

    @AllArgsConstructor
    private static enum RenderingOptionItemKey {

        DEFAULT_RENDERING(false),
        COMMENT(false),
        KEY(false),
        DOCUMENT_ROOT(false),
        DOCUMENT_ID(false),
        OBJECT_ID(true),
        STRING(true),
        INT32(true),
        INT64(true),
        DOUBLE(true),
        BOOLEAN(true),
        ARRAY(true),
        NULL(true),
        UNDEFINED(true);

        @Getter
        private final boolean bsonType;
        
        @Override
        public String toString() {
            return NbBundle.getMessage(Bundle.class,
                    new StringBuilder()
                    .append(getClass().getSimpleName())
                    .append('_')
                    .append(name())
                    .toString());
        }

    }
}
