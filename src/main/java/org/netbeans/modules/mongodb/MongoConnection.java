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

import com.mongodb.MongoException;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import org.netbeans.api.progress.BaseProgressUtils;
import org.netbeans.modules.mongodb.api.connections.ConnectionInfo;
import org.netbeans.modules.mongodb.ui.util.DialogNotification;
import org.openide.util.Lookup;
import org.openide.util.NbBundle.Messages;

/**
 *
 * @author Yann D'Isanto
 */
@Messages({
    "waitWhileConnecting=Please wait while connecting to mongo database"
})
public class MongoConnection {

    @Getter
    private final Lookup lookup;

    private final Object connectionLock = new Object();

    @Getter
    private MongoClient client;

    private final Disconnecter disconnecter = new Disconnecter();

    private final List<ConnectionStateListener> listeners = new ArrayList<>();

    public MongoConnection(Lookup lookup) {
        this.lookup = lookup;
    }

    public MongoClient connect() {
        synchronized (connectionLock) {
            if (client == null) {
                BaseProgressUtils.showProgressDialogAndRun(new Runnable() {

                    @Override
                    public void run() {
                        final ConnectionInfo connection = getLookup().lookup(ConnectionInfo.class);
                        try {
                            client = MongoClients.create(connection.getMongoURI().getURI());
                            fireConnectionStateChanged(ConnectionState.CONNECTED);
                        } catch (MongoException ex) {
                            DialogNotification.error(
                                    "error connectiong to mongo database: " + ex.getLocalizedMessage());
                            MongoClient client = MongoConnection.this.client;
                            MongoConnection.this.client = null;
                            if (client != null) {
                                client.close();
                            }
                        }
                    }
                }, Bundle.waitWhileConnecting());
            }
        }
        return client;
    }

    public synchronized void disconnect() {
        disconnecter.run();
    }

    public boolean isConnected() {
        return client != null;
    }

    public ConnectionState getConnectionState() {
        return isConnected() ? ConnectionState.CONNECTED : ConnectionState.DISCONNECTED;
    }

    public void addConnectionStateListener(ConnectionStateListener listener) {
        listeners.add(listener);
    }

    public void removeConnectionStateListener(ConnectionStateListener listener) {
        listeners.remove(listener);
    }

    private void fireConnectionStateChanged(ConnectionState newState) {
        for (ConnectionStateListener listener : listeners) {
            listener.connectionStateChanged(newState);
        }
    }

    private final class Disconnecter implements AutoCloseable, Runnable {

        @Override
        public void close() {
            run();
        }

        @Override
        public void run() {
            boolean wasConnected = false;
            try {
                synchronized (connectionLock) {
                    MongoClient client = MongoConnection.this.client;
                    MongoConnection.this.client = null;
                    if (client != null) {
                        wasConnected = true;
                        client.close();
                    }
                }
            } finally {
                if (wasConnected) {
                    fireConnectionStateChanged(ConnectionState.DISCONNECTED);
                }
            }
        }
    }

    public static enum ConnectionState {
        CONNECTED,
        DISCONNECTED
    }

    public static interface ConnectionStateListener {

        void connectionStateChanged(ConnectionState newState);

    }
}
