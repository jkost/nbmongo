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

import com.mongodb.MongoSocketException;
import com.mongodb.client.MongoDatabase;
import java.util.Collections;
import java.util.List;
import org.netbeans.modules.mongodb.CollectionInfo;
import org.netbeans.modules.mongodb.DbInfo;
import org.netbeans.modules.mongodb.MongoConnection;
import org.openide.util.Lookup;

/**
 * @author Tim Boudreau
 * @author Yann D'Isanto
 */
class CollectionNodesFactory extends RefreshableChildFactory<CollectionInfo> {

    private final Lookup lookup;

    public CollectionNodesFactory(Lookup lookup) {
        this.lookup = lookup;
    }

    @Override
    protected boolean createKeys(final List<CollectionInfo> list) {
        MongoConnection connection = lookup.lookup(MongoConnection.class);
        DbInfo info = lookup.lookup(DbInfo.class);
        try {
            final MongoDatabase db = connection.getClient().getDatabase(info.getDbName());
            for (String name : db.listCollectionNames()) {
                list.add(new CollectionInfo(name, lookup));
            }
        } catch (MongoSocketException ex) {
            lookup.lookup(MongoConnection.class).disconnect();
        }
        Collections.sort(list);
        return true;
    }

    @Override
    protected CollectionNode createNodeForKey(CollectionInfo key) {
        return new CollectionNode(key);
    }
}
