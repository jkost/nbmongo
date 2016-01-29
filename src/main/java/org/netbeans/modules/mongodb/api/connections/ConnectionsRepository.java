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
package org.netbeans.modules.mongodb.api.connections;

import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import org.netbeans.modules.mongodb.preferences.Prefs;
import org.netbeans.modules.mongodb.util.Repository;

/**
 *
 * @author Yann D'Isanto
 */
public final class ConnectionsRepository extends Repository.PrefsRepository<ConnectionInfo> {

    public static final String NAME = "connections";
    
    public ConnectionsRepository() {
        super(Prefs.of(Prefs.REPOSITORIES).node(NAME));
    }

    @Override
    protected ConnectionInfo loadItem(Preferences node) throws BackingStoreException {
        return new ConnectionInfo(
                node.get(ConnectionInfo.PROPERTY_ID, null),
                node.get(ConnectionInfo.PROPERTY_DISPLAY_NAME, null),
                node.get(ConnectionInfo.PROPERTY_URI, null)
        );
    }

    @Override
    protected void storeItem(ConnectionInfo item, Preferences node) throws BackingStoreException {
        node.put(ConnectionInfo.PROPERTY_ID, item.getId().toString());
        node.put(ConnectionInfo.PROPERTY_DISPLAY_NAME, item.getDisplayName());
        node.put(ConnectionInfo.PROPERTY_URI, item.getUri());
    }

}
