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

import com.mongodb.client.MongoCollection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lombok.AllArgsConstructor;
import org.bson.Document;
import org.netbeans.modules.mongodb.indexes.Index;
import org.netbeans.modules.mongodb.indexes.IndexComparator;
import org.openide.util.Lookup;

/**
 *
 * @author Yann D'Isanto
 */
@AllArgsConstructor
class IndexNodesFactory extends RefreshableChildFactory<Index> {

    private final Lookup lookup;

    @Override
    @SuppressWarnings("unchecked")
    protected boolean createKeys(List<Index> list) {
        MongoCollection<Document> collection = lookup.lookup(MongoCollection.class);
        List<Index> indexes = new ArrayList<>();
        for (Document indexObj : collection.listIndexes()) {
            Index index = Index.fromJson(indexObj);
            indexes.add(index);
        }
        Collections.sort(indexes, new IndexComparator());
        list.addAll(indexes);
        return true;
    }

    @Override
    protected IndexNode createNodeForKey(Index index) {
        return new IndexNode(index, lookup);
    }
}
