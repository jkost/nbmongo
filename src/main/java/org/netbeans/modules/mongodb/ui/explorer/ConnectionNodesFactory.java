/* 
 * The MIT License
 *
 * Copyright 2013 Tim Boudreau.
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
package org.netbeans.modules.mongodb.ui.explorer;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import org.netbeans.modules.mongodb.ConnectionInfo;
import org.openide.util.Exceptions;

/**
 *
 * @author Tim Boudreau
 */
class ConnectionNodesFactory extends RefreshableChildFactory<ConnectionInfo> {

    private ConnectionInfo[] connections() {
        try {
            Preferences prefs = MongoServicesNode.prefs();
            String[] kids = prefs.childrenNames();
            ConnectionInfo[] result = new ConnectionInfo[kids.length];
            for (int i = 0; i < kids.length; i++) {
                String kid = kids[i];
                Preferences node = prefs.node(kid);
                UUID uuid;
                try {
                    uuid = UUID.fromString(kid);
                } catch(IllegalArgumentException ex) {
                    // old connection info, need migration
                    uuid = UUID.randomUUID();
                    Preferences oldNode = node;
                    node = prefs.node(uuid.toString());
                    for (String key : oldNode.keys()) {
                        node.put(key, oldNode.get(key, null));
                    }
                    node.flush();
                    oldNode.removeNode();
                    oldNode.flush();
                }
                result[i] = new ConnectionInfo(uuid, node);
            }
            return result;
        } catch (BackingStoreException ex) {
            Exceptions.printStackTrace(ex);
            return new ConnectionInfo[0];
        }
    }

    @Override
    protected boolean createKeys(List<ConnectionInfo> list) {
        list.addAll(Arrays.asList(connections()));
        return true;
    }

    @Override
    protected ConnectionNode createNodeForKey(ConnectionInfo key) {
        return new ConnectionNode(key);
    }
}
