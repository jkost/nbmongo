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
import lombok.Getter;
import lombok.ToString;
import org.bson.Document;

/**
 * Wrapps the output of the collStats mongodb server command.
 *
 * @author thomaswerner35
 */
 @ToString
public class CollectionStats {

    @Getter private final String serverUsed;
    @Getter private final String ns;
    @Getter private final String capped;
    @Getter private final String count;
    @Getter private final String size;
    @Getter private final String storageSize;
    @Getter private final String numExtents;
    @Getter private final String nindexes;
    @Getter private final String lastExtentSize;
    @Getter private final String paddingFactor;
    @Getter private final String systemFlags;
    @Getter private final String userFlags;
    @Getter private final String totalIndexSize;
    @Getter private final String ok;

    public CollectionStats(Document stats) {
        if(null != stats) {
            serverUsed = stats.get("serverUsed").toString();
            ns = stats.get("ns").toString();
            capped = stats.getBoolean("capped") ? Bundle.yes() : Bundle.no();
            count = getIntegerValue(stats, "count");
            size = getIntegerValue(stats, "size");
            storageSize = getIntegerValue(stats, "storageSize");
            numExtents = getIntegerValue(stats, "numExtents");
            nindexes = getIntegerValue(stats, "nindexes");
            lastExtentSize = getIntegerValue(stats, "lastExtentSize");
            paddingFactor = getNumberValue(stats, "paddingFactor");
            systemFlags = getIntegerValue(stats, "systemFlags");
            userFlags = getIntegerValue(stats, "userFlags");
            totalIndexSize = getIntegerValue(stats, "totalIndexSize");
            ok = Double.valueOf(1.0).equals(stats.get("ok")) ?  Bundle.yes() : Bundle.no();
        } else {
            serverUsed = "";
            ns = "";
            capped = "";
            count = "";
            size = "";
            storageSize = "";
            numExtents = "";
            nindexes = "";
            lastExtentSize = "";
            paddingFactor = "";
            systemFlags = "";
            userFlags = "";
            totalIndexSize = "";
            ok = "";
        }
    }

    private String getIntegerValue(Document stats, String key) {
        return stats.get(key) instanceof Number ?
               NumberFormat.getIntegerInstance().format(((Number) stats.get(key)).doubleValue()) : "";
    }

    private String getNumberValue(Document stats, String key) {
        return stats.get(key) instanceof Number ?
               NumberFormat.getNumberInstance().format(((Number) stats.get(key)).doubleValue()) : "";
    }

}
