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

import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lombok.AllArgsConstructor;
import org.netbeans.modules.mongodb.indexes.Index;
import org.netbeans.modules.mongodb.indexes.IndexComparator;
import org.openide.nodes.Node;
import org.openide.util.Lookup;

/**
 *
 * @author Tim Boudreau
 */
@AllArgsConstructor
final class CollectionChildFactory extends RefreshableChildFactory<Index> {

    private final Lookup lookup;

    @Override
    protected boolean createKeys(List<Index> list) {
        DBCollection collection = lookup.lookup(DBCollection.class);
        List<Index> indexes = new ArrayList<>();
        for (DBObject indexObj : collection.getIndexInfo()) {
            Index index = Index.fromDBObject(indexObj);
            indexes.add(index);
        }
        Collections.sort(indexes, new IndexComparator());
        list.addAll(indexes);
        return true;
    }

    @Override
    protected Node createNodeForKey(Index index) {
        return new IndexNode(index, lookup);
    }
}
