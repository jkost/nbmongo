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
package org.netbeans.modules.mongodb;

import com.mongodb.client.MapReduceIterable;
import com.mongodb.client.MongoCursor;
import java.util.Iterator;
import java.util.LinkedList;
import lombok.Getter;
import org.bson.BsonDocument;

/**
 * Encapsulates the results of a query.
 *
 * @author thomaswerner35
 */
public interface QueryResult {

    long getCount();

    QueryExecutor getExecutor();

    boolean hasNext();

    boolean isCapped();

    BsonDocument next();

    public static final QueryResult EMPTY = new QueryResult() {

        @Override
        public long getCount() {
            return 0;
        }

        @Override
        public QueryExecutor getExecutor() {
            return null;
        }

        @Override
        public boolean hasNext() {
            return false;
        }

        @Override
        public boolean isCapped() {
            return false;
        }

        @Override
        public BsonDocument next() {
            return null;
        }

    };

    public class MongoCursorResult implements QueryResult {

        private final MongoCursor<BsonDocument> cursor;

        @Getter
        private final QueryExecutor executor;

        @Getter
        private final long count;

        public MongoCursorResult(MongoCursor<BsonDocument> cursor, QueryExecutor queryExecutor, long count) {
            this.cursor = cursor;
            this.executor = queryExecutor;
            this.count = count;
        }

        @Override
        public boolean hasNext() {
            return cursor.hasNext();
        }

        @Override
        public boolean isCapped() {
            return false;
        }

        @Override
        public BsonDocument next() {
            return cursor.next();
        }

        @Override
        protected void finalize() throws Throwable {
            super.finalize();
            cursor.close();
        }
    }

    public class MapReduceResult implements QueryResult {

        private static final int MAX_SIZE = 10000;

        @Getter
        private final QueryExecutor executor;

        @Getter
        private boolean capped = false;

        private final java.util.Collection<BsonDocument> data;

        private final Iterator<BsonDocument> iterator;

        public MapReduceResult(MapReduceIterable<BsonDocument> out, QueryExecutor queryExecutor) {
            this.data = new LinkedList<>();
            int num = 0;
            for (BsonDocument document : out) {
                if (++num <= MAX_SIZE) {
                    data.add(document);
                } else {
                    capped = true;
                    break;
                }
            }
            iterator = data.iterator();
            this.executor = queryExecutor;
        }

        @Override
        public long getCount() {
            return data.size();
        }

        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        @Override
        public BsonDocument next() {
            return iterator.next();
        }

    }
    
    public class CollectionResult implements QueryResult {

        @Getter
        private final QueryExecutor executor;

        private final int count;

        private final Iterator<? extends BsonDocument> iterator;

        public CollectionResult(java.util.Collection<? extends BsonDocument> data, QueryExecutor queryExecutor) {
            count = data.size();
            iterator = data.iterator();
            this.executor = queryExecutor;
        }

        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        @Override
        public BsonDocument next() {
            return iterator.next();
        }

        @Override
        public long getCount() {
            return count;
        }

        @Override
        public boolean isCapped() {
            return false;
        }
    }

}
