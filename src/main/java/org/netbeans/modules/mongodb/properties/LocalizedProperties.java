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
package org.netbeans.modules.mongodb.properties;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import org.bson.Document;
import org.openide.nodes.Node.Property;
import org.openide.util.NbBundle;
import org.openide.util.NbBundle.Messages;

/**
 *
 * @author Yann D'Isanto
 */
@Messages({
    "yes=Yes",
    "no=No",
    "# {0} - index name",
    "indexSizes=Size of index {0}"
})
public class LocalizedProperties {

    private final ResourceBundle bundle;

    private final String prefix;

    private final List<Property<?>> properties = new ArrayList<>();

    public LocalizedProperties(Class<?> bundle) {
        this(bundle, bundle.getSimpleName());
    }

    public LocalizedProperties(Class<?> bundle, String prefix) {
        this.bundle = NbBundle.getBundle(bundle);
        this.prefix = prefix;
    }

    public Property[] toArray() {
        return properties.toArray(new Property[properties.size()]);
    }

    public LocalizedProperties booleanProperty(String propertyName, boolean value) {
        return localizedProperty(propertyName, Boolean.class, value);
    }

    public LocalizedProperties intProperty(String propertyName, int value) {
        return localizedProperty(propertyName, Integer.class, value);
    }

    public LocalizedProperties longProperty(String propertyName, long value) {
        return localizedProperty(propertyName, Long.class, value);
    }

    public LocalizedProperties stringProperty(String propertyName, String value) {
        return localizedProperty(propertyName, String.class, value);
    }

    public <T> LocalizedProperties objectProperty(String propertyName, Class<T> propertyType, T value) {
        return localizedProperty(propertyName, propertyType, value);
    }

    public LocalizedProperties objectStringProperty(String propertyName, Object value) {
        return localizedProperty(propertyName, String.class, String.valueOf(value));
    }

    public LocalizedProperties yesNoProperty(String propertyName, boolean value) {
        return localizedProperty(propertyName, String.class, value ? Bundle.yes() : Bundle.no());
    }

    private <T> LocalizedProperties localizedProperty(String propertyName, Class<T> propertyType, T value) {
        properties.add(new LocalizedProperty<>(bundle, prefix, propertyName, propertyType, value));
        return this;
    }

    public LocalizedProperties fromDocument(Document document) {
        for (Map.Entry<String, Object> entry : document.entrySet()) {
            populate(this, entry.getKey(), entry.getValue());
        }
        return this;
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
            switch (key) {
                case "indexSizes":
                    for (Map.Entry<String, Object> entry : map.entrySet()) {
                        populate(props, Bundle.indexSizes(entry.getKey()), entry.getValue());
                    }
                    break;
                case "dataFileVersion":
                    props.stringProperty("dataFileVersion", new StringBuilder().append(map.get("major")).append('.').append(map.get("minor")).toString());
                    break;
                case "extentFreeList":
                    Number num = (Number) map.get("num");
                    if(num != null) {
                        props.longProperty("extentFreeList_num", ((Number) map.get("num")).longValue());
                    }
                    Number size = (Number) map.get("size");
                    if(size != null) {
                        props.longProperty("extentFreeList_size", ((Number) map.get("size")).longValue());
                    }
                    break;
                default:
                    for (Map.Entry<String, Object> entry : map.entrySet()) {
                        String childKey = new StringBuilder().append(key).append('.').append(entry.getKey()).toString();
                        populate(props, childKey, entry.getValue());
                    }
                    break;
            }

        }
    }
}
