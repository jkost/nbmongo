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
package org.netbeans.modules.mongodb.ui.explorer;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.prefs.BackingStoreException;
import org.netbeans.modules.mongodb.api.connections.ConnectionInfo;
import org.netbeans.modules.mongodb.util.PrefsRepositories;
import org.netbeans.modules.mongodb.util.Repository.PrefsRepository;
import org.openide.util.Exceptions;

/**
 *
 * @author Yann D'Isanto
 */
class ConnectionNodesFactory extends RefreshableChildFactory<ConnectionInfo> {

    @Override
    protected boolean createKeys(List<ConnectionInfo> list) {
        PrefsRepository<ConnectionInfo> repo = PrefsRepositories.CONNECTIONS.get();
        try {
            for (ConnectionInfo connectionInfo : repo.all().values()) {
                list.add(connectionInfo);
            }
        } catch (BackingStoreException ex) {
            Exceptions.printStackTrace(ex);
        }
        Collections.sort(list, new Comparator<ConnectionInfo>() {
            @Override
            public int compare(ConnectionInfo c1, ConnectionInfo c2) {
                return c1.getDisplayName().compareTo(c2.getDisplayName());
            }
        });
        return true;
    }

    @Override
    protected ConnectionNode createNodeForKey(ConnectionInfo key) {
        return new ConnectionNode(key);
    }
}
