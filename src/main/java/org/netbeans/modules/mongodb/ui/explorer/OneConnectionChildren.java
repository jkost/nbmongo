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

import com.mongodb.MongoClient;
import com.mongodb.MongoSocketException;
import java.util.List;
import org.netbeans.modules.mongodb.ConnectionInfo;
import org.netbeans.modules.mongodb.DbInfo;
import org.netbeans.modules.mongodb.MongoConnection;
import org.openide.nodes.Node;
import org.openide.util.Lookup;

/**
 *
 * @author Tim Boudreau
 * @author Yann D'Isanto
 */
final class OneConnectionChildren extends RefreshableChildFactory<DbInfo> {

    private OneConnectionNode parentNode;

    private final Lookup lookup;

    public OneConnectionChildren(Lookup lookup) {
        this.lookup = lookup;
    }

    @Override
    protected boolean createKeys(final List<DbInfo> list) {
        if (parentNode == null) {
            return true;
        }
        ConnectionInfo connectionInfo = lookup.lookup(ConnectionInfo.class);
        MongoConnection connection = lookup.lookup(MongoConnection.class);
        try {
            if (connection.isConnected()) {
                MongoClient mongo = connection.getClient();
                final String connectionDBName = connectionInfo.getMongoURI().getDatabase();
                if (connectionDBName != null) {
                    list.add(new DbInfo(connectionDBName, lookup));
                } else {
                    for (String dbName : mongo.getDatabaseNames()) {
                        list.add(new DbInfo(dbName, lookup));
                    }
                }
            }
        } catch (MongoSocketException ex) {
            connection.disconnect();
        }
        return true;
    }

    @Override
    protected Node createNodeForKey(DbInfo key) {
        return new OneDbNode(key);
    }

    public OneConnectionNode getParentNode() {
        return parentNode;
    }

    public void setParentNode(OneConnectionNode parentNode) {
        this.parentNode = parentNode;
    }

}
