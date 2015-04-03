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
import org.openide.util.NbBundle.Messages;

/**
 *
 * @author Yann D'Isanto
 */
@Messages({"ACTION_copyValueToClipboard=Copy value"})
public final class CopyValueToClipboardAction extends CopyObjectToClipboardAction<Object> {
    
    private static final long serialVersionUID = 1L;

    public CopyValueToClipboardAction(Object value) {
        super(Bundle.ACTION_copyValueToClipboard(), value);
    }

    @Override
    public StringSelection convertToStringSelection(Object object) {
        return new StringSelection(convertToString(object));
    }

    private String convertToString(Object value) {
        if (value instanceof Map) {
            return JSON.serialize(new BasicDBObject((Map) value));
        }
        return value.toString();
    }
}
