/* 
 * The MIT License
 *
 * Copyright 2013 Tim Boudreau.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.netbeans.modules.mongodb.api.connections;

import com.mongodb.MongoClientURI;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.UUID;
import java.util.prefs.BackingStoreException;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import org.netbeans.modules.mongodb.util.PrefsRepositories;
import org.netbeans.modules.mongodb.util.Repository;
import org.netbeans.modules.mongodb.util.Repository.PrefsRepository;
import org.openide.util.Exceptions;
import org.openide.util.Parameters;

/**
 *
 * @author Tim Boudreau
 * @author Yann D'Isanto
 */
@EqualsAndHashCode(of = {"id"})
@AllArgsConstructor
@Getter
public final class ConnectionInfo implements Comparable<ConnectionInfo>, AutoCloseable, Repository.RepositoryItem {

    public static final String DEFAULT_URI = "mongodb://localhost"; //NOI18N
    
    public static final String PROPERTY_DISPLAY_NAME = "displayName"; //NOI18N

    public static final String PROPERTY_ID = "id"; //NOI18N

    public static final String PROPERTY_URI = "uri"; //NOI18N


    @NonNull
    private final UUID id;

    @NonNull
    private String displayName;

    @NonNull
    private String uri;

    private final PropertyChangeSupport supp = new PropertyChangeSupport(this);

    public ConnectionInfo(String displayName, String uri) {
        this(UUID.randomUUID(), displayName, uri);
    }

    public ConnectionInfo(String id, String displayName, String uri) {
        this(UUID.fromString(id), displayName, uri);
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

    public void setDisplayName(@NonNull String displayName) {
        String old = getDisplayName();
        if (displayName.equals(this.displayName) == false) {
            this.displayName = displayName;
            supp.firePropertyChange(PROPERTY_DISPLAY_NAME, old, displayName.trim());
        }
    }

    public MongoClientURI getMongoURI() {
        return new MongoClientURI(uri);
    }

    public void setMongoURI(MongoClientURI uri) {
        Parameters.notNull(PROPERTY_URI, uri);
        final MongoClientURI old = getMongoURI();
        if (!old.equals(uri)) {
            this.uri = uri.getURI();
            supp.firePropertyChange(PROPERTY_URI, old, uri);
        }
    }

    private void save() {
        try {
            PrefsRepositories.CONNECTIONS.get().put(this);
        } catch (BackingStoreException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    public void delete() {
        try {
            PrefsRepositories.CONNECTIONS.get().remove(this);
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

    @Override
    public String getKey() {
        return id.toString();
    }
}
