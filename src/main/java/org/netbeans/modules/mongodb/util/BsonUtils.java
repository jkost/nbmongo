/*
 * Copyright (C) 2016 Yann D'Isanto
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
package org.netbeans.modules.mongodb.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.bson.BsonDocument;
import org.bson.BsonValue;
import org.netbeans.modules.mongodb.ui.components.CollectionResultPanel;

/**
 *
 * @author Yann D'Isanto
 */
public final class BsonUtils {

    
    public static BsonDocument sortDocumentFields(BsonDocument document) {
        List<Map.Entry<String, BsonValue>> fields = new ArrayList<>(document.entrySet());
        Collections.sort(fields, CollectionResultPanel.DOCUMENT_FIELD_ENTRY_KEY_COMPARATOR);
        BsonDocument sortedDocument = new BsonDocument();
        BsonValue _id = document.get("_id");
        if (_id != null) {
            sortedDocument.append("_id", _id);
        }
        for (Map.Entry<String, BsonValue> entry : fields) {
            if ("_id".equals(entry.getKey()) == false) {
                BsonValue value = entry.getValue();
                if(value.isDocument()) {
                    value = sortDocumentFields(value.asDocument());
                }
                sortedDocument.append(entry.getKey(), value);
            }
        }
        return sortedDocument;
    }
    
    
    private BsonUtils() {
    }
    
}
