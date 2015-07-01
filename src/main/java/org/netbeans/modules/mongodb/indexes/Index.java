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

import com.mongodb.client.model.IndexOptions;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;
import lombok.AllArgsConstructor;
import lombok.Builder;
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
@Getter
public class Index {

    private final String name;

    private final String nameSpace;

    private final List<Key> keys;

    private final GlobalOptions globalOptions;

    private final TextOptions textOptions;

    private final Geo2DSphereOptions geo2DSphereOptions;

    private final Geo2DOptions geo2DOptions;

    private final GeoHaystackOptions geoHaystackOptions;

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
                Type.valueOf(keyObj.get(field))
            ));
        }
        return new Index(
            (String) indexInfo.get("name"),
            (String) indexInfo.get("ns"),
            keys,
            GlobalOptions.builder()
                .background(indexInfo.getBoolean("background", false))
                .unique(indexInfo.getBoolean("unique", false))
                .sparse(indexInfo.getBoolean("sparse", false))
                .expireAfterSeconds(indexInfo.getLong("expireAfterSeconds"))
                .indexVersion(indexInfo.getInteger("v"))
                .storageEngine((Document) indexInfo.get("storageEngine"))
                .build(),
            TextOptions.builder()
                .weights((Document) indexInfo.get("weights"))
                .defaultLanguage(indexInfo.getString("default_language"))
                .languageOverride(indexInfo.getString("language_override"))
                .indexVersion(indexInfo.getInteger("textIndexVersion"))
                .build(),
            Geo2DSphereOptions.builder()
                .indexVersion(indexInfo.getInteger("2dsphereIndexVersion"))
                .build(),
            Geo2DOptions.builder()
                .bits(indexInfo.getInteger("bits"))
                .min(indexInfo.getDouble("min"))
                .max(indexInfo.getDouble("max"))
                .build(),
            GeoHaystackOptions.builder()
                .bucketSize(indexInfo.getDouble("bucketSize"))
                .build()
        );
    }

    public IndexOptions getOptions() {
        return new IndexOptions()
            .name(getName())
            .background(globalOptions.isBackground())
            .unique(globalOptions.isUnique())
            .sparse(globalOptions.isSparse())
            .expireAfter(globalOptions.getExpireAfterSeconds(), TimeUnit.SECONDS)
            .version(globalOptions.getIndexVersion())
            .storageEngine(globalOptions.getStorageEngine())
            .weights(textOptions.getWeights())
            .defaultLanguage(textOptions.getDefaultLanguage())
            .languageOverride(textOptions.getLanguageOverride())
            .textVersion(textOptions.getIndexVersion())
            .sphereVersion(geo2DSphereOptions.getIndexVersion())
            .bits(geo2DOptions.getBits())
            .min(geo2DOptions.getMin())
            .max(geo2DOptions.getMax())
            .bucketSize(geoHaystackOptions.getBucketSize());
    }

    @Value
    public static class Key {

        @Getter
        private String field;

        @Getter
        private Type type;

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
    public static enum Type {

        ASCENDING(1),
        DESCENDING(-1),
        HASHED("hashed"),
        TEXT("text"),
        GEOSPATIAL_2D("2d"),
        GEOSPATIAL_2DSPHERE("2dsphere"),
        GEOSPATIAL_HAYSTACK("geoHaystack");

        @Getter
        private final Object value;

        public static Type valueOf(Object value) {
            if (value instanceof Number) {
                return valueOf(((Number) value).intValue());
            } else {
                return parse((String) value);
            }
        }

        public static Type valueOf(int sortValue) {
            switch (sortValue) {
                case 1:
                    return ASCENDING;
                case -1:
                    return DESCENDING;
                default:
                    throw new IllegalArgumentException("invlid index sort value: " + sortValue);
            }
        }

        public static Type parse(String value) {
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

        private final ResourceBundle bundle = NbBundle.getBundle(Type.class);

        @Override
        public String toString() {
            return bundle.getString(name());
        }

    }

    @Value
    @Builder
    public static class GlobalOptions {

        boolean background;

        boolean unique;

        boolean sparse;

        Long expireAfterSeconds;

        Integer indexVersion;

        Document storageEngine;

    }

    @Value
    @Builder
    public static class TextOptions {

        Document weights;

        String defaultLanguage;

        String languageOverride;

        Integer indexVersion;

    }

    @Value
    @Builder
    public static class Geo2DSphereOptions {

        Integer indexVersion;

    }

    @Value
    @Builder
    public static class Geo2DOptions {

        Integer bits;

        Double min;

        Double max;

    }

    @Value
    @Builder
    public static class GeoHaystackOptions {

        Double bucketSize;

    }

}
