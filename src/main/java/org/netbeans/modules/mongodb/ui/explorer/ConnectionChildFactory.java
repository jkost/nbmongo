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
package org.netbeans.modules.mongodb.ui.explorer;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import org.netbeans.modules.mongodb.ConnectionInfo;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;

/**
 *
 * @author Tim Boudreau
 */
final class ConnectionChildFactory extends RefreshableChildFactory<ConnectionInfo> {

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
    protected Node createNodeForKey(ConnectionInfo key) {
        return new OneConnectionNode(key);
    }
}
