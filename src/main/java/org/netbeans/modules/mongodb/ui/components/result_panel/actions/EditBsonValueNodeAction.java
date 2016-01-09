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
import lombok.Getter;
import lombok.Setter;
import org.bson.BsonArray;
import org.bson.BsonDocument;
import org.bson.BsonValue;
import org.jdesktop.swingx.treetable.TreeTableNode;
import org.netbeans.modules.mongodb.ui.util.BsonPropertyEditor;
import org.netbeans.modules.mongodb.ui.util.DialogNotification;
import org.netbeans.modules.mongodb.ui.components.CollectionResultPanel;
import org.netbeans.modules.mongodb.ui.components.result_panel.views.treetable.BsonValueNode;
import org.netbeans.modules.mongodb.ui.components.result_panel.views.treetable.RootNode;
import org.openide.util.NbBundle.Messages;

/**
 *
 * @author Yann D'Isanto
 */
@Messages({
    "ACTION_editBsonValue=Edit this value",
    "ACTION_editBsonValue_tooltip=Edit this Value"
})
public final class EditBsonValueNodeAction extends QueryResultPanelAction {

    private static final long serialVersionUID = 1L;

    @Getter
    @Setter
    private BsonValueNode valueNode;

    public EditBsonValueNodeAction(CollectionResultPanel resultPanel, BsonValueNode valueNode) {
        super(resultPanel,
            Bundle.ACTION_editBsonValue(),
            null,
            Bundle.ACTION_editBsonValue_tooltip());
        this.valueNode = valueNode;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void actionPerformed(ActionEvent e) {
        BsonValue value = valueNode.getValue();
        BsonValue newValue = BsonPropertyEditor.show("value", value);
        if (newValue == null || newValue.equals(value)) {
            return;
        }
        getResultPanel().getTreeTableModel().setUserObject(valueNode, newValue);
        TreeTableNode parentNode = valueNode.getParent();
        BsonArray array = ((BsonValueNode) parentNode).getValue().asArray();
        int index = array.indexOf(value);
        array.set(index, newValue);
        while ((parentNode.getParent() instanceof RootNode) == false) {
            parentNode = parentNode.getParent();
        }
        try {
            MongoCollection<BsonDocument> collection = getResultPanel().getLookup().lookup(MongoCollection.class);
            BsonDocument document = (BsonDocument) parentNode.getUserObject();
            collection.replaceOne(eq("_id", document.get("_id")), document);
        } catch (MongoException ex) {
            DialogNotification.error(ex);
        }
    }
}
