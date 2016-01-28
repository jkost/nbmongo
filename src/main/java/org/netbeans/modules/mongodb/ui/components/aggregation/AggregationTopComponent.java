/*
 * Copyright (C) 2016 Yann D'Isanto
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
package org.netbeans.modules.mongodb.ui.components.aggregation;

import com.mongodb.MongoCommandException;
import com.mongodb.client.MongoCollection;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.List;
import javax.swing.JEditorPane;
import javax.swing.JScrollPane;
import javax.swing.text.EditorKit;
import lombok.Getter;
import lombok.Setter;
import org.bson.BsonDocument;
import org.netbeans.api.editor.mimelookup.MimeLookup;
import org.netbeans.modules.mongodb.CollectionInfo;
import org.netbeans.modules.mongodb.api.MongoCursorResult;
import org.netbeans.modules.mongodb.bson.Bsons;
import org.netbeans.modules.mongodb.ui.components.CollectionResultPanel;
import org.netbeans.modules.mongodb.ui.util.DialogNotification;
import org.netbeans.modules.mongodb.util.Tasks;
import org.openide.util.Lookup;
import org.openide.util.NbBundle.Messages;
import org.openide.windows.Mode;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;

/**
 *
 * @author Yann D'Isanto
 */
@TopComponent.Description(
        preferredID = "AggregationTopComponent",
        persistenceType = TopComponent.PERSISTENCE_NEVER)
@Messages({
    "# {0} - collection name",
    "# {1} - counter",
    "TC_title_Aggregation={0} aggregation {1}",
    "TASK_performAggregation=performing aggregation"
})
public final class AggregationTopComponent extends TopComponent {

    private static final long serialVersionUID = 1L;

    @Getter
    @Setter
    private Lookup lookup;

    /**
     * Creates new form AggregationTopComponent
     */
    public AggregationTopComponent(Lookup lookup, final List<BsonDocument> pipeline) {
        setLookup(lookup);
        initComponents();
        JEditorPane pipelineDisplay = new JEditorPane();
        
        EditorKit editorKit = MimeLookup.getLookup("text/x-json").lookup(EditorKit.class);
        if (editorKit != null) {
            pipelineDisplay.setEditorKit(editorKit);
        }
        StringBuilder pipelineText = new StringBuilder();
        pipelineText.append('[');
        boolean first = true;
        for (BsonDocument stage : pipeline) {
            if(first) {
                first = false;
            } else {
                pipelineText.append(",\n");
            }
            pipelineText.append(Bsons.shellAndPretty(stage));
        }
        pipelineText.append(']');
        pipelineDisplay.setText(pipelineText.toString());
        pipelineDisplay.setCaretPosition(0);
        pipelineDisplay.setEditable(false);
        final JScrollPane scrollPane = new JScrollPane(pipelineDisplay);
        scrollPane.setPreferredSize(new Dimension(450, 300));
        aggregationPanel.add(scrollPane, BorderLayout.CENTER);

        Tasks.create(Bundle.TASK_performAggregation(), new Runnable() {
            @Override
            public void run() {
                try {
                    getResultPanel().setResult(new MongoCursorResult(
                            getCollection()
                            .aggregate(pipeline)
                            .iterator()
                    ));
                } catch (MongoCommandException ex) {
                    DialogNotification.error(ex);
                }
            }
        }).execute();
        
    }

    public CollectionResultPanel getResultPanel() {
        return (CollectionResultPanel) resultPanel;
    }

    @SuppressWarnings("unchecked")
    public MongoCollection<BsonDocument> getCollection() {
        return getLookup().lookup(MongoCollection.class);
    }
    
    public CollectionInfo getCollectionInfo() {
        return getLookup().lookup(CollectionInfo.class);
    }
    
    

    @Override
    public void componentOpened() {
        initWindowName();
    }

    private void initWindowName() {
        Mode editorMode = WindowManager.getDefault().findMode("editor");
        TopComponent[] openedTopComponents = WindowManager.getDefault().getOpenedTopComponents(editorMode);
        String name = "";
        int counter = 0;
        boolean found = true;
        while (found) {
            found = false;
            name = Bundle.TC_title_Aggregation(getCollectionInfo().getName(), ++counter);
            for (TopComponent tc : openedTopComponents) {
                if (name.equals(tc.getName())) {
                    found = true;
                    break;
                }
            }
        }
        setName(name);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        splitPane = new javax.swing.JSplitPane();
        aggregationPanel = new javax.swing.JPanel();
        resultPanel = new CollectionResultPanel(lookup, true);

        splitPane.setDividerLocation(400);
        splitPane.setDividerSize(5);
        splitPane.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        splitPane.setResizeWeight(0.5);

        aggregationPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(AggregationTopComponent.class, "AggregationTopComponent.aggregationPanel.border.title"))); // NOI18N
        aggregationPanel.setLayout(new java.awt.BorderLayout());
        splitPane.setLeftComponent(aggregationPanel);
        splitPane.setRightComponent(resultPanel);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(splitPane, javax.swing.GroupLayout.DEFAULT_SIZE, 477, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(splitPane, javax.swing.GroupLayout.DEFAULT_SIZE, 145, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel aggregationPanel;
    private javax.swing.JPanel resultPanel;
    private javax.swing.JSplitPane splitPane;
    // End of variables declaration//GEN-END:variables
}
