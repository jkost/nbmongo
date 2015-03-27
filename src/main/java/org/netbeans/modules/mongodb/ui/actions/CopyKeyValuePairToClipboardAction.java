/*
 * The MIT License
 *
 * Copyright 2014 Yann D'Isanto.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
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
