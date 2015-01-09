package de.bfg9000.mongonb.ui.core.windows;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * This class can be used to cache a LIFO list of query entries. The internal list of query entries is limited to 20
 * entries.
 *
 * @author thomaswerner35
 */
class QueryHistory {

    public static final String PROPERTY_ITEMS = "items";

    private static final int CAPACITY = 20;

    /**
     * Interface with no special methods. It's used just to provide a common base type.
     */
    interface QueryHistoryItem {
        @Override
        String toString();
    }

    private final List<QueryHistoryItem> items = new LinkedList<>();
    private final PropertyChangeSupport propSupport = new PropertyChangeSupport(this);

    /**
     * Adds a new {@code QueryHistoryItem} to the history.
     *
     * @param item The item to be added
     */
    public void add(QueryHistoryItem item) {
        if(items.isEmpty() || (!items.get(0).equals(item))) {
            final List<QueryHistoryItem> old = Collections.unmodifiableList(new ArrayList<QueryHistoryItem>(items));
            items.add(0, item);
            if(CAPACITY < items.size())
                items.remove(CAPACITY);
            propSupport.firePropertyChange(PROPERTY_ITEMS, old, getItems());
        }
    }

    /**
     * @return an unmodifiable copy of the history item list
     */
    public List<QueryHistoryItem> getItems() {
        return Collections.unmodifiableList(items);
    }

    /**
     * Add a {@code PropertyChangeListener} to the listener list.
     * The listener is registered for all properties. The same listener object may be added more than once, and will be
     * called as many times as it is added. If <code>listener</code> is null, no exception is thrown and no action is
     * taken.
     *
     * @param listener  The {@code PropertyChangeListener} to be added
     */
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propSupport.addPropertyChangeListener(listener);
    }

    /**
     * Remove a {@code PropertyChangeListener} from the listener list.
     * This removes a {@code PropertyChangeListener} that was registered for all properties. If <code>listener</code>
     * was added more than once to the same event source, it will be notified one less time after being removed. If
     * <code>listener</code> is null, or was never added, no exception is thrown and no action is taken.
     *
     * @param listener  The {@code PropertyChangeListener} to be removed
     */
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        propSupport.removePropertyChangeListener(listener);
    }

}
