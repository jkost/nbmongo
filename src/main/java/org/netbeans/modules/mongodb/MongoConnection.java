/*
 * The MIT License
 *
 * Copyright 2015 Yann D'Isanto.
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
package org.netbeans.modules.mongodb;

import com.mongodb.MongoClient;
import com.mongodb.MongoException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import org.netbeans.api.progress.ProgressUtils;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
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
                ProgressUtils.showProgressDialogAndRun(new Runnable() {

                    @Override
                    public void run() {
                        final ConnectionInfo connection = getLookup().lookup(ConnectionInfo.class);
                        try {
                            client = new MongoClient(connection.getMongoURI());
                            client.getDatabaseNames();  // ensure connection works
                            fireConnectionStateChanged(ConnectionState.CONNECTED);
                        } catch (MongoException ex) {
                            DialogDisplayer.getDefault().notify(
                                new NotifyDescriptor.Message(
                                    "error connectiong to mongo database: " + ex.getLocalizedMessage(),
                                    NotifyDescriptor.ERROR_MESSAGE));
                            MongoClient client = MongoConnection.this.client;
                            MongoConnection.this.client = null;
                            if (client != null) {
                                client.close();
                            }
                        } catch (UnknownHostException ex) {
                            DialogDisplayer.getDefault().notify(
                                new NotifyDescriptor.Message(
                                    "unknown server: " + ex.getLocalizedMessage(),
                                    NotifyDescriptor.ERROR_MESSAGE));
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
//            RequestProcessor.getDefault().post(this);
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
