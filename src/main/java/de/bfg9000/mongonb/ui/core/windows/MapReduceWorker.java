package de.bfg9000.mongonb.ui.core.windows;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import static com.mongodb.MapReduceCommand.OutputType.INLINE;
import com.mongodb.MapReduceOutput;
import de.bfg9000.mongonb.core.QueryExecutor;
import de.bfg9000.mongonb.core.QueryResult;

/**
 *
 * Executes a map/reduce createQuery asynchronously, then updates the UI.
 *
 * @author thomaswerner35
 */
class MapReduceWorker extends QueryResultWorker implements QueryExecutor {

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
