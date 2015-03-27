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
package de.bfg9000.mongonb.ui.core.windows;

import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import de.bfg9000.mongonb.core.QueryExecutor;
import de.bfg9000.mongonb.core.QueryResult;

/**
 *
 * Executes a query asynchronously, then updates the UI.
 *
 * @author Yann D'Isanto
 */
public final class QueryWorker extends QueryResultWorker implements QueryExecutor {

    private final DBCollection collection;

    private final DBObject criteria;

    private final DBObject projection;

    private final DBObject sort;

    public QueryWorker(String name, DBCollection collection, DBObject criteria, DBObject projection, DBObject sort, int cacheLoadingBlockSize) {
        super(name, cacheLoadingBlockSize);
        this.collection = collection;
        this.criteria = criteria;
        this.projection = projection;
        this.sort = sort;
    }

    @Override
    protected QueryResult createQuery() throws Exception {
        DBCursor cursor = collection.find(criteria, projection);
        if (sort != null) {
            cursor.sort(sort);
        }
        return new QueryResult.DBCursorResult(cursor, this);
    }
}
