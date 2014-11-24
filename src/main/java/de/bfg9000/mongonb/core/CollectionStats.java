package de.bfg9000.mongonb.core;

import com.mongodb.CommandResult;
import java.text.NumberFormat;
import lombok.Getter;
import lombok.ToString;

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

    public CollectionStats(boolean isCollectionCapped, CommandResult stats) {
        if(null != stats) {
            serverUsed = stats.get("serverUsed").toString();
            ns = stats.get("ns").toString();
            capped = isCollectionCapped ? Bundle.yes() : Bundle.no();
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

    private String getIntegerValue(CommandResult stats, String key) {
        return stats.get(key) instanceof Number ?
               NumberFormat.getIntegerInstance().format(((Number) stats.get(key)).doubleValue()) : "";
    }

    private String getNumberValue(CommandResult stats, String key) {
        return stats.get(key) instanceof Number ?
               NumberFormat.getNumberInstance().format(((Number) stats.get(key)).doubleValue()) : "";
    }

}
