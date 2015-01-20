package de.bfg9000.mongonb.ui.core.windows;

import com.mongodb.DBObject;
import de.bfg9000.mongonb.core.QueryResult;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

/**
 * Caches the data of a database query. Uses lazy loading to get new results
 * when required.
 *
 * @author thomaswerner35
 * @author Yann D'Isanto
 */
public final class ResultCache {

    /**
     * A {@code ResultCache} instance that has no content.
     */
    public static final ResultCache EMPTY = new ResultCache(QueryResult.EMPTY, 0);

    private final List<DBObject> cache = new ArrayList<>();

    @Getter
    protected QueryResult queryResult;

    @Getter
    @Setter
    private int loadingBlockSize;
    
    private final List<Listener> listeners = new ArrayList<>();

    public ResultCache(QueryResult queryResult, int loadingBlockSize) {
        this.queryResult = queryResult;
        this.loadingBlockSize = loadingBlockSize;
        loadNextObjects(loadingBlockSize);
    }

    public List<DBObject> get(int offset, int length) {
        int toIndex = offset + length;
        int missingObjects = toIndex - cache.size();
        loadNextObjects(missingObjects);
        return cache.subList(offset, Math.min(cache.size(), toIndex));
    }
    
    public void editObject(DBObject oldObject, DBObject newObject) {
        int index = cache.indexOf(oldObject);
        if(index > -1) {
            editObject(index, newObject);
        }
    }
    
    public void editObject(int index, DBObject object) {
        if(index < cache.size()) {
            DBObject oldValue = cache.set(index, object);
            fireObjectUpdated(index, oldValue, object);
        }
    }

    public int getObjectsCount() {
        return queryResult.getCount();
    }

    private void loadNextObjects(int size) {
        if (size <= 0) {
            return;
        }
        size = Math.max(size, loadingBlockSize);
        for (int i = 0; i < size; i++) {
            if (queryResult.hasNext()) {
                cache.add(queryResult.next());
            }
        }
    }
    
    public void addListener(Listener listener) {
        listeners.add(listener);
    }
    
    public void removeListener(Listener listener) {
        listeners.remove(listener);
    }
    
    private void fireObjectUpdated(int index, DBObject oldValue, DBObject newValue) {
        for (Listener listener : listeners) {
            listener.objectUpdated(index, oldValue, newValue);
        }
    }
    
    public static interface Listener {
        
        void objectUpdated(int index, DBObject oldValue, DBObject newValue);
    }

}
