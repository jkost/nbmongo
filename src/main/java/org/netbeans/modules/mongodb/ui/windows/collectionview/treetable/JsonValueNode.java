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

import java.util.List;
import java.util.Map;
import org.netbeans.modules.mongodb.util.JsonProperty;

/**
 *
 * @author Yann D'Isanto
 */
public final class JsonValueNode extends CollectionViewTreeTableNode<Object> {

    @SuppressWarnings("unchecked")
    public JsonValueNode(Object value) {
        super(value);
        if (value instanceof Map) {
            final Map<String, Object> map = (Map<String, Object>) value;
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                add(new JsonPropertyNode(new JsonProperty(entry.getKey(), entry.getValue())));
            }
        } else if (value instanceof List) {
            final List<Object> objects = (List<Object>) value;
            for (Object object : objects) {
                add(new JsonValueNode(object));
            }
        } else {
            setAllowsChildren(false);
        }
    }
}
