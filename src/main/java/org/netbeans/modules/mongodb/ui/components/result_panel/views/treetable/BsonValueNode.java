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

import java.util.Map;
import org.bson.BsonArray;
import org.bson.BsonDocument;
import org.bson.BsonType;
import org.bson.BsonValue;
import org.jdesktop.swingx.treetable.DefaultMutableTreeTableNode;

/**
 *
 * @author Yann D'Isanto
 */
public class BsonValueNode extends DefaultMutableTreeTableNode {

    public BsonValueNode(BsonValue value) {
        super(value);
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
                add(new BsonPropertyNode("_id", _id));
            }
            for (Map.Entry<String, BsonValue> entry : document.entrySet()) {
                if ("_id".equals(entry.getKey()) == false) {
                    add(new BsonPropertyNode(entry.getKey(), entry.getValue()));
                }
            }
        } else if (value.isArray()) {
            BsonArray array = value.asArray();
            for (BsonValue item : array) {
                add(new BsonValueNode(item));
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
