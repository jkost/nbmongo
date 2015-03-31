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

import com.mongodb.DB;
import com.mongodb.MongoSocketException;
import java.util.LinkedList;
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
            final DB db = connection.getClient().getDB(info.getDbName());
            List<String> names = new LinkedList<>(db.getCollectionNames());
            for (String name : names) {
                list.add(new CollectionInfo(name, lookup));
            }
        } catch (MongoSocketException ex) {
            lookup.lookup(MongoConnection.class).disconnect();
        }
        return true;
    }

    @Override
    protected CollectionNode createNodeForKey(CollectionInfo key) {
        return new CollectionNode(key);
    }
}
