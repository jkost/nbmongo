package de.bfg9000.mongonb.core;

import com.mongodb.CommandResult;
import com.mongodb.DBObject;
import java.text.NumberFormat;
import lombok.Getter;
import lombok.ToString;

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

    public DatabaseStats(CommandResult stats) {
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

    private String getIntegerValue(CommandResult stats, String key) {
        return stats.get(key) instanceof Number ?
               NumberFormat.getIntegerInstance().format(((Number) stats.get(key)).doubleValue()) : "";
    }

    private String getNumberValue(CommandResult stats, String key) {
        return stats.get(key) instanceof Number ?
               NumberFormat.getNumberInstance().format(((Number) stats.get(key)).doubleValue()) : "";
    }

}
