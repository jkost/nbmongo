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

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.swing.DefaultListModel;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.netbeans.modules.mongodb.ui.util.ValidablePanel;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.NbBundle;
import org.openide.util.NbBundle.Messages;

/**
 *
 * @author Yann D'Isanto
 */
@Messages({
    "CreateIndexPanel.globalOptionsPanel.TabConstraints.tabTitle=Global",
    "CreateIndexPanel.textOptionsPanel.TabConstraints.tabTitle=Text",
    "CreateIndexPanel.geo2DSphereOptionsPanel.TabConstraints.tabTitle=2D Sphere",
    "CreateIndexPanel.geo2DOptionsPanel.TabConstraints.tabTitle=2D",
    "CreateIndexPanel.geoHaystackOptionsPanel.TabConstraints.tabTitle=Haystack",
    "createIndexText=Create Index",
    "ACTION_Create_Index=Create Index",
    "VALIDATION_emptyName=specify the index name",
    "VALIDATION_noKey=specify at least one key"
})
public class CreateIndexPanel extends ValidablePanel {

    private static final long serialVersionUID = 1L;

    private final DefaultListModel<Index.Key> keyFieldsListModel = new DefaultListModel<>();

    private final Map<Index.Type, javax.swing.JPanel> dynamicOptionsPanels = new EnumMap<>(Index.Type.class);

    /**
     * Creates new form CreateIndexPanel
     */
    public CreateIndexPanel() {
        initComponents();
        dynamicOptionsPanels.put(Index.Type.TEXT, textOptionsPanel);
        dynamicOptionsPanels.put(Index.Type.GEOSPATIAL_2D, geo2DOptionsPanel);
        dynamicOptionsPanels.put(Index.Type.GEOSPATIAL_2DSPHERE, geo2DSphereOptionsPanel);
        dynamicOptionsPanels.put(Index.Type.GEOSPATIAL_HAYSTACK, geoHaystackOptionsPanel);
        nameField.getDocument().addDocumentListener(new DocumentListener() {

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
        });
        keyFieldsList.setCellRenderer(new IndexKeyListCellRenderer());
        keyFieldsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        keyFieldsList.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

            @Override
            public void valueChanged(ListSelectionEvent e) {
                removeFieldButton.setEnabled(e.getFirstIndex() > -1);
            }
        });
        keyFieldsListModel.addListDataListener(new ListDataListener() {

            @Override
            public void intervalAdded(ListDataEvent e) {
                performValidation();
                updateOptionsTabs();
            }

            @Override
            public void intervalRemoved(ListDataEvent e) {
                performValidation();
                updateOptionsTabs();
            }

            @Override
            public void contentsChanged(ListDataEvent e) {
            }
        });
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                performValidation();
                updateOptionsTabs();
            }
        });
    }

    @Override
    protected String computeValidationProblem() {
        int keysCount = keyFieldsListModel.getSize();
        addFieldButton.setEnabled(keysCount < 31);
        if (keysCount == 0) {
            return Bundle.VALIDATION_noKey();
        }
        return null;
    }

    private void updateOptionsTabs() {
        Set<Index.Type> types = new HashSet<>();
        for (Index.Key key : Collections.list(keyFieldsListModel.elements())) {
            types.add(key.getType());
        }
        for (Index.Type type : dynamicOptionsPanels.keySet()) {
            javax.swing.JPanel panel = dynamicOptionsPanels.get(type);
            if (types.contains(type) == false) {
                optionsTabbedPane.remove(panel);
            } else if (panel.getParent() == null) {
                optionsTabbedPane.add(getOptionsPanelTabTitle(panel), panel);
            }
        }
    }

    private String getOptionsPanelTabTitle(javax.swing.JPanel panel) {
        return NbBundle.getMessage(
            CreateIndexPanel.class,
            new StringBuilder()
            .append("CreateIndexPanel.")
            .append(panel.getName())
            .append(".TabConstraints.tabTitle")
            .toString()
        );
    }

    Index getIndex() {
        String name = nameField.getText();
        return new Index(
            name.isEmpty() ? null : name,
            null,
            Collections.list(keyFieldsListModel.elements()),
            globalOptionsPanel.getGlobalOptions(),
            textOptionsPanel.getTextOptions(),
            geo2DSphereOptionsPanel.getGeo2DSphereOptions(),
            geo2DOptionsPanel.getGeo2DOptions(),
            geoHaystackOptionsPanel.getGeoHaystackOptions()
        );
    }

    void setIndex(Index index) {
        String name = index.getName();
        if (name != null) {
            nameField.setText(name);
        }
        keyFieldsListModel.clear();
        for (Index.Key key : index.getKeys()) {
            keyFieldsListModel.addElement(key);
        }
        globalOptionsPanel.setOptions(index.getGlobalOptions());
        textOptionsPanel.setOptions(index.getTextOptions());
        geo2DSphereOptionsPanel.setOptions(index.getGeo2DSphereOptions());
        geo2DOptionsPanel.setOptions(index.getGeo2DOptions());
        geoHaystackOptionsPanel.setOptions(index.getGeoHaystackOptions());
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                performValidation();
                updateOptionsTabs();
            }
        });
    }
    
    private void clearIndex() {
        nameField.setText("");
        keyFieldsListModel.clear();
        globalOptionsPanel.clearOptions();
        textOptionsPanel.clearOptions();
        geo2DSphereOptionsPanel.clearOptions();
        geo2DOptionsPanel.clearOptions();
        geoHaystackOptionsPanel.clearOptions();
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                performValidation();
                updateOptionsTabs();
            }
        });
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        nameLabel = new javax.swing.JLabel();
        nameField = new javax.swing.JTextField();
        addFieldButton = new javax.swing.JButton();
        fieldsLabel = new javax.swing.JLabel();
        removeFieldButton = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        keyFieldsList = new javax.swing.JList<Index.Key>();
        optionsPanel = new javax.swing.JPanel();
        optionsTabbedPane = new javax.swing.JTabbedPane();
        globalOptionsPanel = new org.netbeans.modules.mongodb.indexes.GlobalOptionsPanel();
        textOptionsPanel = new org.netbeans.modules.mongodb.indexes.TextOptionsPanel();
        geo2DSphereOptionsPanel = new org.netbeans.modules.mongodb.indexes.Geo2DSphereOptionsPanel();
        geo2DOptionsPanel = new org.netbeans.modules.mongodb.indexes.Geo2DOptionsPanel();
        geoHaystackOptionsPanel = new org.netbeans.modules.mongodb.indexes.GeoHaystackOptionsPanel();

        org.openide.awt.Mnemonics.setLocalizedText(nameLabel, org.openide.util.NbBundle.getMessage(CreateIndexPanel.class, "CreateIndexPanel.nameLabel.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(addFieldButton, org.openide.util.NbBundle.getMessage(CreateIndexPanel.class, "CreateIndexPanel.addFieldButton.text")); // NOI18N
        addFieldButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addFieldButtonActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(fieldsLabel, org.openide.util.NbBundle.getMessage(CreateIndexPanel.class, "CreateIndexPanel.fieldsLabel.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(removeFieldButton, org.openide.util.NbBundle.getMessage(CreateIndexPanel.class, "CreateIndexPanel.removeFieldButton.text")); // NOI18N
        removeFieldButton.setEnabled(false);
        removeFieldButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeFieldButtonActionPerformed(evt);
            }
        });

        keyFieldsList.setModel(keyFieldsListModel);
        jScrollPane2.setViewportView(keyFieldsList);

        optionsPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(CreateIndexPanel.class, "optionsPanel.title"))); // NOI18N

        globalOptionsPanel.setName("globalOptionsPanel"); // NOI18N
        optionsTabbedPane.addTab(getOptionsPanelTabTitle(globalOptionsPanel), globalOptionsPanel);

        textOptionsPanel.setName("textOptionsPanel"); // NOI18N
        optionsTabbedPane.addTab(getOptionsPanelTabTitle(textOptionsPanel), textOptionsPanel);

        geo2DSphereOptionsPanel.setName("geo2DSphereOptionsPanel"); // NOI18N
        optionsTabbedPane.addTab(getOptionsPanelTabTitle(geo2DSphereOptionsPanel), geo2DSphereOptionsPanel);

        geo2DOptionsPanel.setName("geo2DOptionsPanel"); // NOI18N
        optionsTabbedPane.addTab(getOptionsPanelTabTitle(geo2DOptionsPanel), geo2DOptionsPanel);

        geoHaystackOptionsPanel.setName("geoHaystackOptionsPanel"); // NOI18N
        optionsTabbedPane.addTab(getOptionsPanelTabTitle(geoHaystackOptionsPanel), geoHaystackOptionsPanel);

        javax.swing.GroupLayout optionsPanelLayout = new javax.swing.GroupLayout(optionsPanel);
        optionsPanel.setLayout(optionsPanelLayout);
        optionsPanelLayout.setHorizontalGroup(
            optionsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(optionsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(optionsTabbedPane)
                .addContainerGap())
        );
        optionsPanelLayout.setVerticalGroup(
            optionsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(optionsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(optionsTabbedPane, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(optionsPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addContainerGap())
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(nameLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(nameField)
                        .addGap(10, 10, 10))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane2)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(fieldsLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(addFieldButton)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(removeFieldButton)))
                        .addContainerGap())))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(nameLabel)
                    .addComponent(nameField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(optionsPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(fieldsLabel)
                    .addComponent(removeFieldButton)
                    .addComponent(addFieldButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void addFieldButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addFieldButtonActionPerformed
        Enumeration<Index.Key> keys = keyFieldsListModel.elements();
        List<String> usedKeys = new ArrayList<>();
        while (keys.hasMoreElements()) {
            usedKeys.add(keys.nextElement().getField());
        }
        Index.Key key = IndexKeyPanel.showCreateDialog(usedKeys);
        if (key != null) {
            keyFieldsListModel.addElement(key);
        }
    }//GEN-LAST:event_addFieldButtonActionPerformed

    private void removeFieldButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeFieldButtonActionPerformed
        int index = keyFieldsList.getSelectedIndex();
        if (index > -1) {
            keyFieldsListModel.removeElementAt(index);
        }
    }//GEN-LAST:event_removeFieldButtonActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addFieldButton;
    private javax.swing.JLabel fieldsLabel;
    private org.netbeans.modules.mongodb.indexes.Geo2DOptionsPanel geo2DOptionsPanel;
    private org.netbeans.modules.mongodb.indexes.Geo2DSphereOptionsPanel geo2DSphereOptionsPanel;
    private org.netbeans.modules.mongodb.indexes.GeoHaystackOptionsPanel geoHaystackOptionsPanel;
    private org.netbeans.modules.mongodb.indexes.GlobalOptionsPanel globalOptionsPanel;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JList<Index.Key> keyFieldsList;
    private javax.swing.JTextField nameField;
    private javax.swing.JLabel nameLabel;
    private javax.swing.JPanel optionsPanel;
    private javax.swing.JTabbedPane optionsTabbedPane;
    private javax.swing.JButton removeFieldButton;
    private org.netbeans.modules.mongodb.indexes.TextOptionsPanel textOptionsPanel;
    // End of variables declaration//GEN-END:variables

    public static Index showDialog() {
        return showDialog(null);
    }
    
    public static Index showDialog(Index index) {
        final CreateIndexPanel panel = new CreateIndexPanel();
        if(index != null) {
            panel.setIndex(index);
        }
        final DialogDescriptor desc = new DialogDescriptor(panel, Bundle.IndexKeyPanel_title());
        panel.setNotificationLineSupport(desc.createNotificationLineSupport());
        panel.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                desc.setValid(panel.isValidationSuccess());
            }
        });
        if (NotifyDescriptor.OK_OPTION.equals(DialogDisplayer.getDefault().notify(desc))) {
            return panel.getIndex();
        }
        return null;
    }
}
