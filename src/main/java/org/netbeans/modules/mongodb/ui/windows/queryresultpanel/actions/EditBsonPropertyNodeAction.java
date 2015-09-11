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
import lombok.Getter;
import lombok.Setter;
import org.bson.BsonDocument;
import org.jdesktop.swingx.treetable.TreeTableNode;
import org.netbeans.modules.mongodb.ui.util.BsonPropertyEditor;
import org.netbeans.modules.mongodb.ui.util.DialogNotification;
import org.netbeans.modules.mongodb.ui.windows.QueryResultPanel;
import org.netbeans.modules.mongodb.ui.windows.collectionview.treetable.BsonPropertyNode;
import org.netbeans.modules.mongodb.ui.windows.collectionview.treetable.RootNode;
import org.netbeans.modules.mongodb.util.BsonProperty;
import org.openide.util.NbBundle.Messages;

/**
 *
 * @author Yann D'Isanto
 */
@Messages({
    "ACTION_editBsonProperty=Edit this property",
    "ACTION_editBsonProperty_tooltip=Edit this Property"
})
public final class EditBsonPropertyNodeAction extends QueryResultPanelAction {

    private static final long serialVersionUID = 1L;

    @Getter
    @Setter
    private BsonPropertyNode propertyNode;

    public EditBsonPropertyNodeAction(QueryResultPanel resultPanel, BsonPropertyNode propertyNode) {
        super(resultPanel,
            Bundle.ACTION_editBsonProperty(),
            null,
            Bundle.ACTION_editBsonProperty_tooltip());
        this.propertyNode = propertyNode;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void actionPerformed(ActionEvent e) {
        BsonProperty property = propertyNode.getBsonProperty();
        BsonProperty newProperty = BsonPropertyEditor.show(property);
        if (newProperty == null) {
            return;
        }
        propertyNode.setPropertyName(newProperty.getName());
        getResultPanel().getTreeTableModel().setUserObject(propertyNode, newProperty.getValue());
        TreeTableNode parentNode = propertyNode.getParent();
        BsonDocument document = (BsonDocument) parentNode.getUserObject();
        if (newProperty.getName().equals(property.getName()) == false) {
            document.remove(property.getName());
        }
        document.put(newProperty.getName(), newProperty.getValue());
        while ((parentNode.getParent() instanceof RootNode) == false) {
            parentNode = parentNode.getParent();
        }
        try {
            final MongoCollection<BsonDocument> collection = getResultPanel().getLookup().lookup(MongoCollection.class);
            collection.replaceOne(Filters.eq("_id", document.get("_id")), document);
            
        } catch (MongoException ex) {
            DialogNotification.error(ex);
        }
    }
}
