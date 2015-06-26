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

import com.mongodb.CommandResult;
import com.mongodb.DBObject;
import java.text.NumberFormat;
import lombok.Getter;
import lombok.ToString;
import org.bson.Document;

/**
 * Wrapps the output of the dbStats mongodb server command.
 *
 * @author thomaswerner35
 */
@ToString
public class DatabaseStats {

    @Getter private final String serverUsed;
    @Getter private final String db;
    @Getter private final String collections;
    @Getter private final String objects;
    @Getter private final String avgObjSize;
    @Getter private final String dataSize;
    @Getter private final String storageSize;
    @Getter private final String numExtents;
    @Getter private final String indexes;
    @Getter private final String indexSize;
    @Getter private final String fileSize;
    @Getter private final String nsSizeMB;
    @Getter private final String dataFileVersion;
    @Getter private final String ok;

    public DatabaseStats(Document stats) {
//    public DatabaseStats(CommandResult stats) {
        if(null == stats) {
            serverUsed = "";
            db = "";
            collections = "";
            objects = "";
            avgObjSize = "";
            dataSize = "";
            storageSize = "";
            numExtents = "";
            indexes = "";
            indexSize = "";
            fileSize = "";
            nsSizeMB = "";
            dataFileVersion = "";
            ok = "";
        } else {
            serverUsed = (String) stats.get("serverUsed");
            db = (String) stats.get("db");
            collections = getIntegerValue(stats, "collections");
            objects = getIntegerValue(stats, "objects");
            avgObjSize = getNumberValue(stats, "avgObjSize");
            dataSize = getIntegerValue(stats, "dataSize");
            storageSize = getIntegerValue(stats, "storageSize");
            numExtents = getIntegerValue(stats, "numExtents");
            indexes = getIntegerValue(stats, "indexes");
            indexSize = getIntegerValue(stats, "indexSize");
            fileSize = getIntegerValue(stats, "fileSize");
            nsSizeMB = getIntegerValue(stats, "nsSizeMB");
            dataFileVersion = stats.get("dataFileVersion") instanceof DBObject?
                             ((DBObject)stats.get("dataFileVersion")).get("major") +"." +
                             ((DBObject)stats.get("dataFileVersion")).get("minor") : "";
            ok = Double.valueOf(1.0).equals(stats.get("ok")) ? Bundle.yes() : Bundle.no();
        }
    }

    private String getIntegerValue(Document stats, String key) {
//    private String getIntegerValue(CommandResult stats, String key) {
        return stats.get(key) instanceof Number ?
               NumberFormat.getIntegerInstance().format(((Number) stats.get(key)).doubleValue()) : "";
    }

    private String getNumberValue(Document stats, String key) {
//    private String getNumberValue(CommandResult stats, String key) {
        return stats.get(key) instanceof Number ?
               NumberFormat.getNumberInstance().format(((Number) stats.get(key)).doubleValue()) : "";
    }

}
