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
package org.netbeans.modules.mongodb.ui.windows;

import com.mongodb.client.MongoCollection;
import org.netbeans.modules.mongodb.CollectionInfo;
import javax.swing.Action;
import lombok.Getter;
import org.bson.BsonDocument;
import org.netbeans.modules.mongodb.api.connections.ConnectionInfo;
import org.netbeans.modules.mongodb.api.FindCriteria;
import org.netbeans.modules.mongodb.api.FindResult;
import org.netbeans.modules.mongodb.resources.Images;
import org.netbeans.modules.mongodb.ui.components.CollectionResultPanel;
import org.netbeans.modules.mongodb.ui.components.FindCriteriaEditor;
import org.netbeans.modules.mongodb.ui.windows.collectionview.actions.ClearQueryAction;
import org.netbeans.modules.mongodb.ui.windows.collectionview.actions.EditQueryAction;
import org.netbeans.modules.mongodb.util.SystemCollectionPredicate;
import org.openide.util.Lookup;
import org.openide.util.NbBundle.Messages;
import org.openide.windows.TopComponent;

/**
 * Top component which displays something.
 */
@TopComponent.Description(
    preferredID = "CollectionView",
    persistenceType = TopComponent.PERSISTENCE_NEVER)
@Messages({
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
public final class CollectionView extends TopComponent /*implements QueryResultWorkerFactory, QueryResultPanelContainer*/ {

    private static final long serialVersionUID = 1L;

    private final boolean isSystemCollection;

    @Getter
    private final FindCriteriaEditor criteriaEditor = new FindCriteriaEditor();

    @Getter
    private Lookup lookup;

    @Getter
    private final Action editQueryAction = new EditQueryAction(this);

    @Getter
    private final Action clearQueryAction = new ClearQueryAction(this);

    public CollectionView(CollectionInfo collectionInfo, Lookup lookup) {
        this(collectionInfo, lookup, FindCriteria.EMPTY);
    }
    
    public CollectionView(CollectionInfo collectionInfo, Lookup lookup, FindCriteria findCriteria) {
        super(lookup);
        this.lookup = lookup;
        isSystemCollection = SystemCollectionPredicate.get().eval(collectionInfo.getName());
        initComponents();
        updateTitle();
        setIcon(isSystemCollection
            ? Images.SYSTEM_COLLECTION_ICON
            : Images.COLLECTION_ICON);
        loadPreferences();
        setFindCriteria(findCriteria);
    }

    @SuppressWarnings("unchecked")
    public MongoCollection<BsonDocument> getCollection() {
        return getLookup().lookup(MongoCollection.class);
    }
    
    public void setLookup(Lookup lookup) {
        this.lookup = lookup;
    }

    public void updateTitle() {
        ConnectionInfo connectionInfo = lookup.lookup(ConnectionInfo.class);
        String collectionFullName = lookup.lookup(MongoCollection.class).getNamespace().getFullName();
        String title = Bundle.collectionViewTitle(collectionFullName);
        setName(title);
        setToolTipText(
            Bundle.collectionViewTooltip(connectionInfo.getDisplayName(), title));
    }

    @Override
    protected void componentShowing() {
    }

    @Override
    protected void componentClosed() {
        writePreferences();
    }

    public CollectionResultPanel getResultPanel() {
        return (CollectionResultPanel) resultPanel;
    }

    public void updateQueryFieldsFromEditor() {
        setFindCriteria(criteriaEditor.getFindCriteria());
    }

    public void setFindCriteria(FindCriteria findCriteria) {
        BsonDocument filter = findCriteria.getFilter();
        BsonDocument projection = findCriteria.getProjection();
        BsonDocument sort = findCriteria.getSort();
        filterField.setText(filter != null ? filter.toJson() : "");
        projectionField.setText(projection != null ? projection.toJson() : "");
        sortField.setText(sort != null ? sort.toJson() : "");
        getResultPanel().setResult(new FindResult(getCollection(), findCriteria));
    }
    
    void loadPreferences() {
        getResultPanel().loadPreferences();
    }

    void writePreferences() {
        getResultPanel().writePreferences();
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        queryPanel = new javax.swing.JPanel();
        filterLabel = new javax.swing.JLabel();
        filterField = new javax.swing.JTextField();
        projectionLabel = new javax.swing.JLabel();
        projectionField = new javax.swing.JTextField();
        sortLabel = new javax.swing.JLabel();
        sortField = new javax.swing.JTextField();
        editQueryButton = new javax.swing.JButton();
        clearQueryButton = new javax.swing.JButton();
        resultPanel = new CollectionResultPanel(lookup, isSystemCollection);

        queryPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(CollectionView.class, "CollectionView.queryPanel.border.title"))); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(filterLabel, org.openide.util.NbBundle.getMessage(CollectionView.class, "CollectionView.filterLabel.text")); // NOI18N

        filterField.setEditable(false);
        filterField.setText(org.openide.util.NbBundle.getMessage(CollectionView.class, "CollectionView.filterField.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(projectionLabel, org.openide.util.NbBundle.getMessage(CollectionView.class, "CollectionView.projectionLabel.text")); // NOI18N

        projectionField.setEditable(false);
        projectionField.setText(org.openide.util.NbBundle.getMessage(CollectionView.class, "CollectionView.projectionField.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(sortLabel, org.openide.util.NbBundle.getMessage(CollectionView.class, "CollectionView.sortLabel.text")); // NOI18N

        sortField.setEditable(false);
        sortField.setText(org.openide.util.NbBundle.getMessage(CollectionView.class, "CollectionView.sortField.text")); // NOI18N

        editQueryButton.setAction(getEditQueryAction());

        clearQueryButton.setAction(getClearQueryAction());

        javax.swing.GroupLayout queryPanelLayout = new javax.swing.GroupLayout(queryPanel);
        queryPanel.setLayout(queryPanelLayout);
        queryPanelLayout.setHorizontalGroup(
            queryPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(queryPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(queryPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(queryPanelLayout.createSequentialGroup()
                        .addComponent(editQueryButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(clearQueryButton)
                        .addContainerGap(329, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, queryPanelLayout.createSequentialGroup()
                        .addComponent(filterLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(filterField))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, queryPanelLayout.createSequentialGroup()
                        .addGroup(queryPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(projectionLabel)
                            .addComponent(sortLabel))
                        .addGap(6, 6, 6)
                        .addGroup(queryPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(projectionField)
                            .addComponent(sortField)))))
        );

        queryPanelLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {filterLabel, projectionLabel, sortLabel});

        queryPanelLayout.setVerticalGroup(
            queryPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(queryPanelLayout.createSequentialGroup()
                .addGroup(queryPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(filterField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(filterLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(queryPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(projectionField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(projectionLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(queryPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(sortField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(sortLabel))
                .addGap(12, 12, 12)
                .addGroup(queryPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(editQueryButton)
                    .addComponent(clearQueryButton)))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(queryPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(resultPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(queryPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(resultPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton clearQueryButton;
    private javax.swing.JButton editQueryButton;
    private javax.swing.JTextField filterField;
    private javax.swing.JLabel filterLabel;
    private javax.swing.JTextField projectionField;
    private javax.swing.JLabel projectionLabel;
    private javax.swing.JPanel queryPanel;
    private javax.swing.JPanel resultPanel;
    private javax.swing.JTextField sortField;
    private javax.swing.JLabel sortLabel;
    // End of variables declaration//GEN-END:variables

}
