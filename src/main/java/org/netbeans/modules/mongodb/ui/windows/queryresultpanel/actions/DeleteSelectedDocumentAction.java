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
package org.netbeans.modules.mongodb.ui.windows.queryresultpanel.actions;

import com.mongodb.MongoException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import java.awt.event.ActionEvent;
import javax.swing.ImageIcon;
import org.bson.Document;
import org.netbeans.modules.mongodb.resources.Images;
import org.netbeans.modules.mongodb.ui.windows.QueryResultPanel;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.NbBundle.Messages;

/**
 *
 * @author Yann D'Isanto
 */
@Messages({
    "confirmDocumentDeletionText=Delete document?",
    "ACTION_deleteSelectedDocument=Delete document",
    "ACTION_deleteSelectedDocument_tooltip=Delete Selected Document"
})
public final class DeleteSelectedDocumentAction extends QueryResultPanelAction {
    
    private static final long serialVersionUID = 1L;

    public DeleteSelectedDocumentAction(QueryResultPanel resultPanel) {
        super(resultPanel,
            Bundle.ACTION_deleteSelectedDocument(),
            new ImageIcon(Images.DELETE_DOCUMENT_ICON),
            Bundle.ACTION_deleteSelectedDocument_tooltip());
    }

    @Override
    @SuppressWarnings("unchecked")
    public void actionPerformed(ActionEvent e) {
        final Object dlgResult = DialogDisplayer.getDefault().notify(
            new NotifyDescriptor.Confirmation(Bundle.confirmDocumentDeletionText(), NotifyDescriptor.YES_NO_OPTION));
        if (dlgResult.equals(NotifyDescriptor.OK_OPTION)) {
            try {
                MongoCollection<Document> collection = getResultPanel().getLookup().lookup(MongoCollection.class);
                Document document = (Document) getResultPanel().getResultTableSelectedDocument();
                collection.deleteOne(Filters.eq("_id", document.get("_id")));
                getResultPanel().refreshResults();
            } catch (MongoException ex) {
                DialogDisplayer.getDefault().notify(
                    new NotifyDescriptor.Message(ex.getLocalizedMessage(), NotifyDescriptor.ERROR_MESSAGE));
            }
        }
    }
}
