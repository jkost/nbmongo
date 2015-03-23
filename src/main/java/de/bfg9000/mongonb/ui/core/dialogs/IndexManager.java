package de.bfg9000.mongonb.ui.core.dialogs;

import com.mongodb.BasicDBObject;
import com.mongodb.DuplicateKeyException;
import com.mongodb.MongoException;
import de.bfg9000.mongonb.core.Index;
import de.bfg9000.mongonb.core.Index.Key;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.NbBundle;

/**
 * Used to manage {@code Index}es of {@code Collection}s.
 *
 * @author thomaswerner35
 */
class IndexManager {

    private static final ResourceBundle bundle = NbBundle.getBundle(IndexManager.class);

    private final IndexManagerModel model;

    public IndexManager(IndexManagerModel model) {
        this.model = model;
    }

    /**
     * @return {@code null} if there are no duplicate indexes - an error message
     * if there are duplicates.
     */
    public String hasDuplicates() {
        final Map<String, Integer> keyMap = new HashMap<>();
        int pos = 0;
        for (Index index : model.getIndexes()) {
            final String keyString = keysToString(index);
            pos++;
            if (keyMap.containsKey(keyString)) {
                return MessageFormat.format(bundle.getString("IndexManager.duplicateError"), keyMap.get(keyString), pos);
            } else {
                keyMap.put(keyString, pos);
            }
        }
        return null;
    }

    /**
     * Removes all duplicate indexes from the model.
     *
     * @return the number of indexes that have been removed.
     */
    public int removeDuplicates() {
        int result = 0;
        while (null != hasDuplicates()) {
            final Set<String> keys = new HashSet<>();
            final Iterator<Index> indexIterator = model.getIndexes().iterator();
            int indexPos = 0;
            while (indexIterator.hasNext()) {
                final Index index = indexIterator.next();
                final String keyString = keysToString(index);
                if (!keys.add(keyString)) {
                    model.removeIndex(indexPos);
                    result++;
                    break;
                }
                indexPos++;
            }
        }
        return result;
    }

    public String hasEmptyIndexes() {
        final List<Integer> emptyIndexes = new LinkedList<>();
        int pos = 0;
        for (Index index : model.getIndexes()) {
            pos++;
            if (index.getKeys().isEmpty()) {
                emptyIndexes.add(pos);
            }
        }

        if (emptyIndexes.isEmpty()) {
            return null;
        }

        final String template = bundle.getString("IndexManager.emptyError-"
            + (emptyIndexes.size() == 1 ? "single" : "multi"));
        final StringBuilder builder = new StringBuilder();
        for (Iterator iterator = emptyIndexes.iterator(); iterator.hasNext();) {
            builder.append(iterator.next());
            if (iterator.hasNext()) {
                builder.append(", ");
            }
        }
        return MessageFormat.format(template, builder.toString());
    }

    public int removeEmptyIndexes() {
        int result = 0;
        while (null != hasEmptyIndexes()) {
            final Iterator<Index> indexIterator = model.getIndexes().iterator();
            int indexPos = 0;
            while (indexIterator.hasNext()) {
                final Index index = indexIterator.next();
                if (index.getKeys().isEmpty()) {
                    model.removeIndex(indexPos);
                    result++;
                    break;
                }
                indexPos++;
            }
        }
        return result;
    }

    public void modifyCollection() {
        for (Index index : model.getIndexesToDelete()) {
            dropIndex(index);
        }
        for (Index index : model.getIndexesToCreate()) {
            try {
                createIndex(index);
            } catch (DuplicateKeyException dke) {
                final StringBuilder builder = new StringBuilder();
                int keyCount = 0;
                final Iterator<Key> iterator = index.getKeys().iterator();
                while (iterator.hasNext()) {
                    builder.append(iterator.next().getColumn());
                    if (iterator.hasNext()) {
                        builder.append(", ");
                    }
                    keyCount++;
                }

                final String template = bundle.getString("IndexManager.message.duplicateKey"
                    + (1 == keyCount ? "-single" : "-multi"));
                final String msg = MessageFormat.format(template, builder.toString());
                DialogDisplayer.getDefault().notify(new NotifyDescriptor.Message(msg, NotifyDescriptor.ERROR_MESSAGE));
            }
        }
    }

    private String keysToString(Index index) {
        final StringBuilder builder = new StringBuilder();
        for (Key key : index.getKeys()) {
            builder.append(key.getColumn());
        }
        return builder.toString();
    }

    public void createIndex(Index index) throws MongoException {
        final BasicDBObject keys = new BasicDBObject();
        for (Index.Key key : index.getKeys()) {
            keys.append(key.getColumn(), key.isOrderedAscending() ? 1 : -1);
        }
        final BasicDBObject options = new BasicDBObject();
        if (index.isSparse()) {
            options.append("sparse", true);
        }
        if (index.isUnique()) {
            options.append("unique", true);
        }
        if (index.isDropDuplicates()) {
            options.append("dropDups", true);
        }
        model.getCollection().createIndex(keys, options);
    }

    public void dropIndex(Index index) {
        model.getCollection().dropIndex(index.getName());
    }

}
