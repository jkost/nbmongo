package de.bfg9000.mongonb.ui.core.dialogs;

import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import de.bfg9000.mongonb.core.Index;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import lombok.Getter;

/**
 * Manages the data of the IndexManagerDialog. Keeps track of changes etc.
 *
 * @author thomaswerner35
 */
class IndexManagerModel {

    public static final String PROPERTY_INDEXES = "indexes";

//    @Getter private final Collection collection;
    private final List<Index> indexes = new ArrayList<>();

    private final List<Index> indexesToCreate = new ArrayList<>();

    private final List<Index> indexesToDelete = new ArrayList<>();

    private final PropertyChangeSupport propSupport = new PropertyChangeSupport(this);

    @Getter
    private final DBCollection collection;

    public IndexManagerModel(DBCollection collection) {
        this.collection = collection;
        final List<DBObject> indexInfo = collection.getIndexInfo();
        for (DBObject index : indexInfo) {
            indexes.add(new Index(index));
        }
        Collections.sort(indexes, new IndexComparator());
    }

//    public IndexManagerModel(/*Collection collection*/) {
//        this.collection = collection;
//        indexes.addAll(collection.getIndexes());
//        Collections.sort(indexes, new IndexComparator());
//    }
    /**
     * Add a PropertyChangeListener to the listener list. The listener is
     * registered for all properties. The same listener object may be added more
     * than once, and will be called as many times as it is added. If
     * <code>listener</code> is null, no exception is thrown and no action is
     * taken.
     *
     * @param listener The PropertyChangeListener to be added
     */
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propSupport.addPropertyChangeListener(listener);
    }

    /**
     * Remove a PropertyChangeListener from the listener list. This removes a
     * PropertyChangeListener that was registered for all properties. If
     * <code>listener</code> was added more than once to the same event source,
     * it will be notified one less time after being removed. If
     * <code>listener</code> is null, or was never added, no exception is thrown
     * and no action is taken.
     *
     * @param listener The PropertyChangeListener to be removed
     */
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        propSupport.removePropertyChangeListener(listener);
    }

    public int getIndexCount() {
        return indexes.size();
    }

    public Index getIndex(int index) {
        return indexes.get(index);
    }

    public boolean isNew(int index) {
        return null == indexes.get(index).getName();
    }

    public void addIndex() {
        final List<Index> old = Collections.unmodifiableList(new ArrayList<>(this.indexes));
        final Index newIndex = new Index();
        indexes.add(newIndex);
        indexesToCreate.add(newIndex);
        propSupport.firePropertyChange(PROPERTY_INDEXES, old, Collections.unmodifiableList(indexes));
    }

    public void removeIndex(int index) {
        final List<Index> old = Collections.unmodifiableList(new ArrayList<>(this.indexes));
        final Index indexToDelete = indexes.get(index);
        if (isNew(index)) {
            indexesToCreate.remove(indexToDelete);
        } else {
            indexesToDelete.add(indexToDelete);
        }
        indexes.remove(indexToDelete);
        propSupport.firePropertyChange(PROPERTY_INDEXES, old, Collections.unmodifiableList(indexes));
    }

    public java.util.Collection<Index> getIndexes() {
        return Collections.unmodifiableList(indexes);
    }

    public java.util.Collection<Index> getIndexesToCreate() {
        return Collections.unmodifiableList(indexesToCreate);
    }

    public java.util.Collection<Index> getIndexesToDelete() {
        return Collections.unmodifiableList(indexesToDelete);
    }

    private static final class IndexComparator implements Comparator<Index> {

        @Override
        public int compare(Index o1, Index o2) {
            if ("_id_".equals(o1.getName())) {
                return "_id_".equals(o2.getName()) ? 0 : -1;
            }
            return o1.getName().compareTo(o2.getName());
        }

    }

}
