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
package org.netbeans.modules.mongodb.indexes;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Value;
import org.bson.Document;
import org.openide.util.NbBundle;
import org.openide.util.NbBundle.Messages;

/**
 * POJO that contains the data of an index.
 *
 * @author thomaswerner35
 * @author Yann D'Isanto
 */
@AllArgsConstructor
public class Index {

    @Getter
    private String name;

    @Getter
    private String nameSpace;

    @Getter
    private final List<Key> keys;

    @Getter
    private boolean sparse;

    @Getter
    private boolean unique;

//    @Getter
//    private boolean dropDuplicates;
    public Index(String name, String nameSpace, List<Key> keys) {
        this(name, nameSpace, keys, true, false);
    }

    @Override
    public String toString() {
        return name;
    }

    public static Index fromJson(Document indexInfo) {
        final Document keyObj = (Document) indexInfo.get("key");
        List<Key> keys = new ArrayList<>();
        for (String field : keyObj.keySet()) {
            keys.add(new Key(
                field,
                KeySort.valueOf(keyObj.get(field))
            ));
        }
        return new Index(
            (String) indexInfo.get("name"),
            (String) indexInfo.get("ns"),
            keys,
            Boolean.TRUE.equals(indexInfo.get("sparse")),
            Boolean.TRUE.equals(indexInfo.get("unique"))
        );
    }

    @Value
    public static class Key {

        @Getter
        private String field;

        @Getter
        private KeySort sort;

    }

    @AllArgsConstructor
    @Messages({
        "ASCENDING=ascending",
        "DESCENDING=descending",
        "HASHED=hashed",
        "TEXT=text",
        "GEOSPATIAL_2D=2d",
        "GEOSPATIAL_2DSPHERE=2dsphere",
        "GEOSPATIAL_HAYSTACK=geoHaystack"
    })
    public static enum KeySort {

        ASCENDING(1),
        DESCENDING(-1),
        HASHED("hashed"),
        TEXT("text"),
        GEOSPATIAL_2D("2d"),
        GEOSPATIAL_2DSPHERE("2dsphere"),
        GEOSPATIAL_HAYSTACK("geoHaystack");

        @Getter
        private final Object value;

        public static KeySort valueOf(Object value) {
            if (value instanceof Number) {
                return valueOf(((Number) value).intValue());
            } else {
                return parse((String) value);
            }
        }

        public static KeySort valueOf(int sortValue) {
            switch (sortValue) {
                case 1:
                    return ASCENDING;
                case -1:
                    return DESCENDING;
                default:
                    throw new IllegalArgumentException("invlid index sort value: " + sortValue);
            }
        }

        public static KeySort parse(String value) {
            switch (value) {
                case "hashed":
                    return HASHED;
                case "text":
                    return TEXT;
                case "2d":
                    return GEOSPATIAL_2D;
                case "2dsphere":
                    return GEOSPATIAL_2DSPHERE;
                case "geoHaystack":
                    return GEOSPATIAL_HAYSTACK;
                default:
                    throw new IllegalArgumentException("unknown index type: " + value);
            }
        }

        private final ResourceBundle bundle = NbBundle.getBundle(KeySort.class);

        @Override
        public String toString() {
            return bundle.getString(name());
        }

    }
}
