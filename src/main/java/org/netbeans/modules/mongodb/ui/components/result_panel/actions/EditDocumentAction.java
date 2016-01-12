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
package org.netbeans.modules.mongodb.ui.components.result_panel.actions;

import com.mongodb.MongoException;
import com.mongodb.client.MongoCollection;
import static com.mongodb.client.model.Filters.eq;
import java.awt.event.ActionEvent;
import javax.swing.ImageIcon;
import lombok.Getter;
import lombok.Setter;
import org.bson.BsonDocument;
import org.bson.BsonValue;
import org.netbeans.modules.mongodb.resources.Images;
import org.netbeans.modules.mongodb.ui.util.DialogNotification;
import org.netbeans.modules.mongodb.ui.util.BsonDocumentEditor;
import org.netbeans.modules.mongodb.ui.components.CollectionResultPanel;
import org.netbeans.modules.mongodb.util.Tasks;
import org.openide.util.NbBundle.Messages;
import org.openide.util.Task;
import org.openide.util.TaskListener;

/**
 *
 * @author Yann D'Isanto
 */
@Messages({
    "editDocumentTitle=Edit document",
    "ACTION_editDocument=Edit document",
    "ACTION_editDocument_tooltip=Edit Document",
    "TASK_updateDocument=updating document"
})
public class EditDocumentAction extends QueryResultPanelAction {

    private static final long serialVersionUID = 1L;

    @Getter
    @Setter
    private BsonDocument document;

    public EditDocumentAction(CollectionResultPanel resultPanel) {
        super(resultPanel,
            Bundle.ACTION_editDocument(),
            new ImageIcon(Images.EDIT_DOCUMENT_ICON),
            Bundle.ACTION_editDocument_tooltip());
    }

    @Override
    @SuppressWarnings("unchecked")
    public void actionPerformed(ActionEvent e) {
        if (document == null) {
            return;
        }
        final BsonValue id = document.get("_id");
        if(id == null) {
            // display error ?
            return;
        }
        final BsonDocument modifiedDocument = BsonDocumentEditor.show(
            Bundle.editDocumentTitle(),
            document);
        if (modifiedDocument != null) {
            Tasks.create(Bundle.TASK_updateDocument(), new Runnable() {

                @Override
                public void run() {
                    try {
                        getResultPanel().getLookup().lookup(MongoCollection.class).replaceOne(eq("_id", id), modifiedDocument);
                    } catch (MongoException ex) {
                        DialogNotification.error(ex);
                    }
                }
            }).execute().addTaskListener(new TaskListener() {

                @Override
                public void taskFinished(Task task) {
                    getResultPanel().editDocument(document, modifiedDocument);
                }
            });
        }
    }
}
