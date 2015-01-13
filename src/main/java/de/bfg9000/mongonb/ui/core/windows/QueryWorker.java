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
 * @author thomaswerner35
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
