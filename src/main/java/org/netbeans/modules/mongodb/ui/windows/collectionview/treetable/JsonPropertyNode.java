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
package org.netbeans.modules.mongodb.ui.windows.collectionview.treetable;

import com.mongodb.DBObject;
import java.util.List;
import java.util.Map;
import org.netbeans.modules.mongodb.util.JsonProperty;

/**
 *
 * @author Yann D'Isanto
 */
public final class JsonPropertyNode extends CollectionViewTreeTableNode<JsonProperty> {

    private final boolean arrayValue;

    private final boolean objectValue;
    
    @SuppressWarnings("unchecked")
    public JsonPropertyNode(JsonProperty property) {
        super(property);
        arrayValue = getValue() instanceof List;
        objectValue = getValue() instanceof Map;
        if (objectValue) {
            for (Map.Entry<String, Object> entry : getObjectValue().entrySet()) {
                add(new JsonPropertyNode(new JsonProperty(entry.getKey(), entry.getValue())));
            }
        } else if (arrayValue) {
            for (Object object : getArrayValue()) {
                if (object instanceof DBObject) {
                    add(new DBObjectNode((DBObject) object));
                } else {
                    add(new JsonValueNode(object));
                }
            }
        } else {
            setAllowsChildren(false);
        }
    }
    
    public String getName() {
        return getUserObject().getName();
    }
    
    @Override
    public Object getValue() {
        return getUserObject().getValue();
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public List<Object> getArrayValue() {
        return (List<Object>) getValue();
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public Map<String, Object> getObjectValue() {
        return (Map<String, Object>) getValue();
    }

    @Override
    public boolean isArrayValue() {
        return arrayValue;
    }

    @Override
    public boolean isNullValue() {
        return getValue() == null;
    }

    @Override
    public boolean isObjectValue() {
        return objectValue;
    }
    
    @Override
    public boolean isNotNullValue() {
        return isNullValue()== false;
    }
    
    @Override
    public boolean isSimpleValue() {
        return isNotNullValue() && isArrayValue() == false && isObjectValue() == false;
    }
}
