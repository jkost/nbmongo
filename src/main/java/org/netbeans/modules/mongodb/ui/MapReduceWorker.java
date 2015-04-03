/* 
 * Copyright (C) 2015 Thomas Werner
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

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import static com.mongodb.MapReduceCommand.OutputType.INLINE;
import com.mongodb.MapReduceOutput;
import org.netbeans.modules.mongodb.QueryExecutor;
import org.netbeans.modules.mongodb.QueryResult;

/**
 *
 * Executes a map/reduce createQuery asynchronously, then updates the UI.
 *
 * @author thomaswerner35
 */
public class MapReduceWorker extends QueryResultWorker implements QueryExecutor {

    private final DBCollection collection;

    private final String mapFunction;

    private final String reduceFunction;

    public MapReduceWorker(DBCollection collection, String map, String reduce, String name, int cacheLoadingBlockSize) {
        super(name, cacheLoadingBlockSize);
        this.collection = collection;
        this.mapFunction = map;
        this.reduceFunction = reduce;
    }

    @Override
    protected QueryResult createQuery() throws Exception {
        final MapReduceOutput out = collection.mapReduce(mapFunction, reduceFunction, null, INLINE, new BasicDBObject());
        return new QueryResult.MapReduceResult(out, this);
    }

}
