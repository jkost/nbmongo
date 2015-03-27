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
package org.netbeans.modules.mongodb;

import com.mongodb.MongoClientURI;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.UUID;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import org.openide.util.Exceptions;
import org.openide.util.Parameters;

/**
 *
 * @author Tim Boudreau
 * @author Yann D'Isanto
 */
@EqualsAndHashCode(of = {"id"})
public final class ConnectionInfo implements Comparable<ConnectionInfo>, AutoCloseable {

    public static final String PREFS_KEY_DISPLAY_NAME = "displayName"; //NOI18N

    public static final String PREFS_KEY_ID = "id"; //NOI18N

    public static final String PREFS_KEY_URI = "uri"; //NOI18N

    public static final String DEFAULT_URI = "mongodb://localhost"; //NOI18N

    @Getter
    private final UUID id;

    private final Preferences node;

    private final PropertyChangeSupport supp = new PropertyChangeSupport(this);

    public ConnectionInfo(@NonNull UUID id, @NonNull Preferences node) {
        this.node = node;
        this.id = id;
    }

    public ConnectionInfo(@NonNull Preferences parent) {
        id = UUID.randomUUID();
        node = parent.node(id.toString());
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        supp.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        supp.removePropertyChangeListener(listener);
    }

    public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        supp.addPropertyChangeListener(propertyName, listener);
    }

    public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        supp.removePropertyChangeListener(propertyName, listener);
    }

    public String getDisplayName() {
        return node.get(PREFS_KEY_DISPLAY_NAME, getMongoURI().getURI());
    }

    public void setDisplayName(String displayName) {
        Parameters.notNull(PREFS_KEY_DISPLAY_NAME, displayName);
        String old = getDisplayName();
        if (!displayName.equals(old)) {
            if (displayName.trim().isEmpty()) {
                node.remove(PREFS_KEY_DISPLAY_NAME);
            } else {
                node.put(PREFS_KEY_DISPLAY_NAME, displayName.trim());
            }
            supp.firePropertyChange(PREFS_KEY_DISPLAY_NAME, old, displayName.trim());
        }
    }

    public MongoClientURI getMongoURI() {
        return new MongoClientURI(node.get(PREFS_KEY_URI, DEFAULT_URI));
    }
    
    public void setMongoURI(MongoClientURI uri) {
        Parameters.notNull(PREFS_KEY_URI, uri);
        final MongoClientURI old = getMongoURI();
        if (!old.equals(uri)) {
            node.put(PREFS_KEY_URI, uri.getURI());
            supp.firePropertyChange(PREFS_KEY_URI, old, uri);
        }
    }
    
    private void save() {
        try {
            node.parent().flush();
            node.parent().sync();
            node.flush();
            node.parent().sync();
            node.sync();
        } catch (BackingStoreException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    public void delete() {
        try {
            node.removeNode();
        } catch (BackingStoreException ex) {
            Exceptions.printStackTrace(ex);
        }
    }
    
    @Override
    public void close() {
        save();
    }
    
    @Override
    public String toString() {
        return getDisplayName(); //NOI18N
    }

    @Override
    public int compareTo(ConnectionInfo o) {
        return id.compareTo(o.id);
    }
}
