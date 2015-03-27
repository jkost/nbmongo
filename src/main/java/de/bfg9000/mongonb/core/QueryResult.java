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
package de.bfg9000.mongonb.core;

import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MapReduceOutput;
import java.util.Iterator;
import java.util.LinkedList;
import lombok.Getter;

/**
 * Encapsulates the results of a query.
 *
 * @author thomaswerner35
 */
public interface QueryResult {

    int getCount();

    QueryExecutor getExecutor();

    boolean hasNext();

    boolean isCapped();

    DBObject next();

    public static final QueryResult EMPTY = new QueryResult() {

        @Override
        public int getCount() {
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
        public DBObject next() {
            return null;
        }

    };

    public class DBCursorResult implements QueryResult {

        private final DBCursor cursor;

        @Getter
        private final QueryExecutor executor;

        private Integer count = null;

        public DBCursorResult(DBCursor cursor, QueryExecutor queryExecutor) {
            this.cursor = cursor;
            this.executor = queryExecutor;
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
        public DBObject next() {
            return cursor.next();
        }

        @Override
        public int getCount() {
            return null == count ? count = cursor.count() : count;
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

        private final java.util.Collection<DBObject> data;

        private final Iterator<DBObject> iterator;

        public MapReduceResult(MapReduceOutput out, QueryExecutor queryExecutor) {
            this.data = new LinkedList<>();
            int num = 0;
            for (DBObject object : out.results()) {
                if (++num <= MAX_SIZE) {
                    data.add(object);
                } else {
                    capped = true;
                    break;
                }
            }
            iterator = data.iterator();
            this.executor = queryExecutor;
        }

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        @Override
        public DBObject next() {
            return iterator.next();
        }

    }

    public class CollectionResult implements QueryResult {

        @Getter
        private final QueryExecutor executor;

        private final int count;

        private final Iterator<? extends DBObject> iterator;

        public CollectionResult(java.util.Collection<? extends DBObject> data, QueryExecutor queryExecutor) {
            count = data.size();
            iterator = data.iterator();
            this.executor = queryExecutor;
        }

        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        @Override
        public DBObject next() {
            return iterator.next();
        }

        @Override
        public int getCount() {
            return count;
        }

        @Override
        public boolean isCapped() {
            return false;
        }
    }

}
