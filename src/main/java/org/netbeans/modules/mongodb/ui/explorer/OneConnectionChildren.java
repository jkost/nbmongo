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

import com.mongodb.MongoClient;
import com.mongodb.MongoSocketException;
import java.util.List;
import org.netbeans.modules.mongodb.ConnectionInfo;
import org.netbeans.modules.mongodb.DbInfo;
import org.netbeans.modules.mongodb.MongoDisconnect;
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
        MongoClient mongo = lookup.lookup(MongoClient.class);
        try {
            if (mongo != null) {
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
            lookup.lookup(MongoDisconnect.class).close();
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
