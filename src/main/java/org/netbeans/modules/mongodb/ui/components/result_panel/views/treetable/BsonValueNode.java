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
package org.netbeans.modules.mongodb.ui.components.result_panel.views.treetable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.bson.BsonArray;
import org.bson.BsonDocument;
import org.bson.BsonType;
import org.bson.BsonValue;
import org.jdesktop.swingx.treetable.DefaultMutableTreeTableNode;
import org.netbeans.modules.mongodb.ui.components.CollectionResultPanel;

/**
 *
 * @author Yann D'Isanto
 */
public class BsonValueNode extends DefaultMutableTreeTableNode {

    private final boolean sortDocumentsFields;
    
    public BsonValueNode(BsonValue value, boolean sortDocumentsFields) {
        super(value);
        this.sortDocumentsFields = sortDocumentsFields;
        refreshChildren();
    }

    private void refreshChildren() {
        children.clear();
        BsonValue value = getValue();
        if (value.isDocument()) {
            BsonDocument document = value.asDocument();
            // let's put _id as first child if it exists
            BsonValue _id = document.get("_id");
            if (_id != null) {
                add(new BsonPropertyNode("_id", _id, false));
            }
            Collection<Map.Entry<String, BsonValue>> fields = document.entrySet();
            if (sortDocumentsFields) {
                List<Map.Entry<String, BsonValue>> entries = new ArrayList<>(document.entrySet());
                Collections.sort(entries, CollectionResultPanel.DOCUMENT_FIELD_ENTRY_KEY_COMPARATOR);
                fields = entries;
            }
            for (Map.Entry<String, BsonValue> entry : fields) {
                if ("_id".equals(entry.getKey()) == false) {
                    add(new BsonPropertyNode(entry.getKey(), entry.getValue(), sortDocumentsFields));
                }
            }
        } else if (value.isArray()) {
            BsonArray array = value.asArray();
            for (BsonValue item : array) {
                add(new BsonValueNode(item, sortDocumentsFields));
            }
        }
    }

    @Override
    public void setUserObject(Object object) {
        if (object instanceof BsonValue) {
            super.setUserObject(object);
            refreshChildren();
        } else {
            throw new IllegalArgumentException("not a BsonValue object");
        }
    }

    @Override
    public BsonValue getUserObject() {
        return (BsonValue) super.getUserObject();
    }

    public BsonValue getValue() {
        return getUserObject();
    }

    public BsonType getValueType() {
        return getValue().getBsonType();
    }

    @Override
    public boolean isEditable(int column) {
        return false;
    }

}
