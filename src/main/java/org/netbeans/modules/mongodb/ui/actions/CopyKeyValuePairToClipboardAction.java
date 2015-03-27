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
package org.netbeans.modules.mongodb.ui.actions;

import com.mongodb.BasicDBObject;
import com.mongodb.util.JSON;
import java.awt.datatransfer.StringSelection;
import java.util.Map;
import org.netbeans.modules.mongodb.util.JsonProperty;
import org.openide.util.NbBundle.Messages;

/**
 *
 * @author Yann D'Isanto
 */
@Messages({"ACTION_copyKeyValuePairToClipboard=Copy key/value pair"})
public final class CopyKeyValuePairToClipboardAction extends CopyObjectToClipboardAction<JsonProperty> {

    private static final long serialVersionUID = 1L;

    public CopyKeyValuePairToClipboardAction(JsonProperty property) {
        super(Bundle.ACTION_copyKeyValuePairToClipboard(), property);
    }

    @Override
    public StringSelection convertToStringSelection(JsonProperty property) {
        return new StringSelection(convertToString(property));
    }

    private String convertToString(JsonProperty property) {
        StringBuilder sb = new StringBuilder();
        return sb
            .append(property.getName())
            .append(':')
            .append(formatPropertyValue(property.getValue()))
            .toString();
    }
    
    private String formatPropertyValue(Object value) {
        if (value instanceof Map) {
            return JSON.serialize(new BasicDBObject((Map) value));
        }
        StringBuilder sb = new StringBuilder();
        sb.append(value);
        if(value instanceof String) {
            sb.insert(0, '"');
            sb.append('"');
        }
        return sb.toString();
    }
}
