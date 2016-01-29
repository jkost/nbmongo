/*
 * Copyright (C) 2016 Yann D'Isanto
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
package org.netbeans.modules.mongodb.util;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import lombok.AllArgsConstructor;
import org.netbeans.modules.mongodb.util.Repository.RepositoryItem;

/**
 *
 * @author Yann D'Isanto
 */
public interface Repository<T extends RepositoryItem, E extends Exception> {

    Collection<String> listKeys() throws E;

    Map<String, T> all() throws E;

    T get(String key) throws E;

    void put(T item) throws E;
    
    void remove(String key) throws E;

    void remove(T item) throws E;

    boolean exists(String key) throws E;
    
    @AllArgsConstructor
    static abstract class PrefsRepository<T extends RepositoryItem> implements Repository<T, BackingStoreException> {

        private final Preferences prefs;
        
        @Override
        public final Collection<String> listKeys() throws BackingStoreException {
            prefs.sync();
            return Arrays.asList(prefs.childrenNames());
        }

        @Override
        public final Map<String, T> all() throws BackingStoreException {
            prefs.sync();
            Map<String, T> items = new TreeMap<>();
            for (String key : prefs.childrenNames()) {
                items.put(key, loadItem(prefs.node(key)));
            }
            return items;
        }

        @Override
        public final T get(String key) throws BackingStoreException {
            prefs.sync();
            return loadItem(itemNode(key));
        }

        @Override
        public final void put(T item) throws BackingStoreException {
            storeItem(item, itemNode(item.getKey()));
            prefs.flush();
        }

        @Override
        public final void remove(String key) throws BackingStoreException {
            itemNode(key).removeNode();
        }

        @Override
        public void remove(T item) throws BackingStoreException {
            remove(item.getKey());
        }
        
        @Override
        public final boolean exists(String key) throws BackingStoreException {
            prefs.sync();
            return prefs.nodeExists(key);
        }

        private Preferences itemNode(String key) {
            return prefs.node(key);
        }

        protected abstract T loadItem(Preferences node) throws BackingStoreException;
        
        protected abstract void storeItem(T item, Preferences node) throws BackingStoreException;
    }
    
    public static interface RepositoryItem {
        String getKey();
    }
}
