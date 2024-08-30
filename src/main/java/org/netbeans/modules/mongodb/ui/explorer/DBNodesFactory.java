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

import com.mongodb.client.MongoClient;
import com.mongodb.MongoSocketException;
import java.util.Collections;
import java.util.List;
import lombok.AllArgsConstructor;
import org.netbeans.modules.mongodb.api.connections.ConnectionInfo;
import org.netbeans.modules.mongodb.DbInfo;
import org.netbeans.modules.mongodb.MongoConnection;
import org.openide.util.Lookup;

/**
 *
 * @author Tim Boudreau
 * @author Yann D'Isanto
 */
@AllArgsConstructor
final class DBNodesFactory extends RefreshableChildFactory<DbInfo> {

    private final Lookup lookup;

    @Override
    protected boolean createKeys(final List<DbInfo> list) {
        ConnectionInfo connectionInfo = lookup.lookup(ConnectionInfo.class);
        MongoConnection connection = lookup.lookup(MongoConnection.class);
        try {
            if (connection.isConnected()) {
                MongoClient mongo = connection.getClient();
                final String connectionDBName = connectionInfo.getMongoURI().getDatabase();
                if (connectionDBName != null) {
                    list.add(new DbInfo(connectionDBName, lookup));
                } else {
                    for (String dbName : mongo.listDatabaseNames()) {
                        list.add(new DbInfo(dbName, lookup));
                    }
                }
            }
        } catch (MongoSocketException ex) {
            connection.disconnect();
        }
        Collections.sort(list);
        return true;
    }

    @Override
    protected DBNode createNodeForKey(DbInfo key) {
        return new DBNode(key);
    }
}
