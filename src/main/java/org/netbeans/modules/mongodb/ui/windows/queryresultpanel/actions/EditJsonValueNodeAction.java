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
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.bson.Document;
import org.jdesktop.swingx.treetable.TreeTableNode;
import org.netbeans.modules.mongodb.ui.util.DialogNotification;
import org.netbeans.modules.mongodb.ui.util.JsonPropertyEditor;
import org.netbeans.modules.mongodb.ui.windows.QueryResultPanel;
import org.netbeans.modules.mongodb.ui.windows.collectionview.treetable.DocumentNode;
import org.netbeans.modules.mongodb.ui.windows.collectionview.treetable.JsonValueNode;
import org.netbeans.modules.mongodb.util.JsonProperty;
import org.openide.util.NbBundle.Messages;

/**
 *
 * @author Yann D'Isanto
 */
@Messages({
    "editJsonValueTitle=Edit json value",
    "ACTION_editJsonValue=Edit json value",
    "ACTION_editJsonValue_tooltip=Edit Selected JSON Value"
})
public final class EditJsonValueNodeAction extends QueryResultPanelAction {

    private static final long serialVersionUID = 1L;

    @Getter
    @Setter
    private JsonValueNode valueNode;

    public EditJsonValueNodeAction(QueryResultPanel resultPanel, JsonValueNode valueNode) {
        super(resultPanel,
                Bundle.ACTION_editJsonValue(),
                null,
                Bundle.ACTION_editJsonValue_tooltip());
        this.valueNode = valueNode;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void actionPerformed(ActionEvent e) {
        Object value = valueNode.getUserObject();
        Object newValue = JsonPropertyEditor.show("value", value);
        if (newValue == null || newValue.equals(value)) {
            return;
        }
        getResultPanel().getTreeTableModel().setUserObject(valueNode, newValue);
        TreeTableNode parentNode = valueNode.getParent();
        JsonProperty parent = (JsonProperty) parentNode.getUserObject();
        List<Object> list = (List<Object>) parent.getValue();
        int index = list.indexOf(value);
        list.set(index, newValue);
        while ((parentNode instanceof DocumentNode) == false) {
            parentNode = parentNode.getParent();
        }
        try {
            MongoCollection<Document> collection = getResultPanel().getLookup().lookup(MongoCollection.class);
            Document document = (Document) parentNode.getUserObject();
            collection.replaceOne(Filters.eq("_id", document.get("_id")), document);
        } catch (MongoException ex) {
            DialogNotification.error(ex);
        }
    }
}
