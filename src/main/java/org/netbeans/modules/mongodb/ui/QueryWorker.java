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
package org.netbeans.modules.mongodb.ui;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.netbeans.modules.mongodb.QueryExecutor;
import org.netbeans.modules.mongodb.QueryResult;

/**
 *
 * Executes a query asynchronously, then updates the UI.
 *
 * @author Yann D'Isanto
 */
public final class QueryWorker extends QueryResultWorker implements QueryExecutor {

    private final MongoCollection<Document> collection;

    private final Document criteria;

    private final Document projection;

    private final Document sort;

    public QueryWorker(String name, MongoCollection<Document> collection, Document criteria, Document projection, Document sort, int cacheLoadingBlockSize) {
        super(name, cacheLoadingBlockSize);
        this.collection = collection;
        this.criteria = criteria;
        this.projection = projection;
        this.sort = sort;
    }

    @Override
    protected QueryResult createQuery() throws Exception {
        FindIterable<Document> query = criteria != null ? collection.find(criteria) : collection.find();
        query = query.projection(projection).sort(sort);
        long size = criteria != null ? collection.count(criteria) : collection.count();
        return new QueryResult.DBCursorResult(query.iterator(), this, size);
    }
}
