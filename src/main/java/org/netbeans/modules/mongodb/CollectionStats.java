/* 
 * Copyright (C) 2015 Thomas Werner
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
package org.netbeans.modules.mongodb;

import java.text.NumberFormat;
import java.util.Map;
import lombok.ToString;
import org.bson.Document;
import org.netbeans.modules.mongodb.properties.LocalizedProperties;
import org.openide.util.NbBundle.Messages;

/**
 * Wrapps the output of the collStats mongodb server command.
 *
 * @author thomaswerner35
 */
@ToString
@Messages({
    "# {0} - index name",
    "indexSizes=Size of index {0}"
})
public class CollectionStats {

    private final Document stats;

    public CollectionStats(Document stats) {
        this.stats = stats;
    }

    public LocalizedProperties populate(LocalizedProperties props) {
        for (Map.Entry<String, Object> entry : stats.entrySet()) {
            populate(props, entry.getKey(), entry.getValue());
        }
        return props;
    }

    @SuppressWarnings("unchecked")
    private static void populate(LocalizedProperties props, String key, Object value) {
        if (value instanceof Boolean) {
            props.booleanProperty(key, (Boolean) value);
        } else if (value instanceof String) {
            props.stringProperty(key, (String) value);
        } else if (value instanceof Integer) {
            props.intProperty(key, (Integer) value);
        } else if (value instanceof Long) {
            props.longProperty(key, (Long) value);
        } else if (value instanceof Number) {
            props.stringProperty(key, NumberFormat.getNumberInstance().format(value));
        } else if (value instanceof Map) {
            Map<String, Object> map = (Map<String, Object>) value;
            StringBuilder prefix = new StringBuilder();
            if ("indexSizes".equals(key)) {
                for (Map.Entry<String, Object> entry : map.entrySet()) {
                    populate(props, Bundle.indexSizes(entry.getKey()), entry.getValue());
                }
            } else {
                for (Map.Entry<String, Object> entry : map.entrySet()) {
                    String childKey = prefix.append(key).append('.').append(entry.getKey()).toString();
                    populate(props, childKey, entry.getValue());
                }
            }
        }
    }
}
