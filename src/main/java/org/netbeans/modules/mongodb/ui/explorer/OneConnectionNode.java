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
package org.netbeans.modules.mongodb.ui.explorer;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBObject;
import org.netbeans.modules.mongodb.properties.ConnectionNameProperty;
import org.netbeans.modules.mongodb.properties.MongoClientURIProperty;
import org.netbeans.modules.mongodb.resources.Images;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.MongoException;
import de.bfg9000.mongonb.ui.core.windows.MapReduceTopComponent;
import org.netbeans.modules.mongodb.ui.util.TopComponentUtils;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.netbeans.modules.mongodb.ConnectionInfo;
import org.netbeans.modules.mongodb.MongoConnection;
import org.netbeans.modules.mongodb.MongoConnection.ConnectionState;
import org.netbeans.modules.mongodb.native_tools.MongoNativeToolsAction;
import org.netbeans.modules.mongodb.properties.MongoClientURIPropertyEditor;
import org.netbeans.modules.mongodb.ui.util.DatabaseNameValidator;
import org.netbeans.modules.mongodb.ui.util.ValidatingInputLine;
import org.netbeans.modules.mongodb.ui.windows.CollectionView;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.PropertySupport;
import org.openide.nodes.Sheet;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle.Messages;
import org.openide.util.WeakListeners;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;
import org.openide.util.lookup.Lookups;
import org.openide.util.lookup.ProxyLookup;
import org.openide.windows.TopComponent;

/**
 *
 * @author Tim Boudreau
 * @author Yann D'Isanto
 */
@Messages({
    "ACTION_Delete=Delete",
    "ACTION_Connect=Connect",
    "ACTION_Disconnect=Disconnect",
    "ACTION_CreateDatabase=Create database",
    "createDatabaseText=Database name:",
    "waitWhileConnecting=Please wait while connecting to mongo database"
})
final class OneConnectionNode extends AbstractNode implements PropertyChangeListener {

    private static final Logger LOG = Logger.getLogger(OneConnectionNode.class.getName());

    private final MongoConnection connectionHandler;

    private final ConnectionConverter converter = new ConnectionConverter();

    private OneConnectionChildren childFactory;

    OneConnectionNode(ConnectionInfo connection) {
        this(connection, new InstanceContent());
    }

    OneConnectionNode(ConnectionInfo connection, InstanceContent content) {
        this(connection, content, new ProxyLookup(new AbstractLookup(content), Lookups.fixed(connection)));
    }

    OneConnectionNode(ConnectionInfo connection, InstanceContent content, ProxyLookup lkp) {
        this(connection, content, lkp, new OneConnectionChildren(lkp));
    }

    OneConnectionNode(final ConnectionInfo connection, InstanceContent content, ProxyLookup lkp, final OneConnectionChildren childFactory) {
        super(Children.create(childFactory, true), lkp);
        this.childFactory = childFactory;
        setDisplayName(connection.getDisplayName());
        setName(connection.getId().toString());
        childFactory.setParentNode(this);
        connection.addPropertyChangeListener(WeakListeners.propertyChange(this, connection));
        connectionHandler = new MongoConnection(lkp);
        connectionHandler.addConnectionStateListener(new MongoConnection.ConnectionStateListener() {

            @Override
            public void connectionStateChanged(MongoConnection.ConnectionState newState) {
                fireIconChange();
                childFactory.refresh();
                updateSheet();
                if (newState == ConnectionState.DISCONNECTED) {
                    for (TopComponent topComponent : TopComponentUtils.findAll(connection, CollectionView.class, MapReduceTopComponent.class)) {
                        topComponent.close();
                    }
                }
            }
        });
        content.add(connectionHandler);
    }

    public boolean isConnected() {
        return connectionHandler.isConnected();
    }

    @Override
    public Image getIcon(int ignored) {
        return isConnected()
            ? Images.CONNECTION_ICON
            : Images.CONNECTION_DISCONNECTED_ICON;
    }

    @Override
    public Image getOpenedIcon(int ignored) {
        return getIcon(ignored);
    }

    @Override
    public String getShortDescription() {
        final ConnectionInfo connection = getLookup().lookup(ConnectionInfo.class);
        return connection.getMongoURI().toString();
    }

    @Override
    public Action[] getActions(boolean ignored) {
        final Action connectAction = new ConnectAction();
        final Action disconnectAction = new DisconnectAction();
        final Action createDatabaseAction = new CreateDatabaseAction();
        final Action refreshAction = new RefreshChildrenAction(childFactory);
        refreshAction.setEnabled(isConnected());
        createDatabaseAction.setEnabled(isConnected());
        connectAction.setEnabled(isConnected() == false);
        disconnectAction.setEnabled(isConnected());

        final List<Action> actions = new LinkedList<>();
        actions.add(connectAction);
        actions.add(disconnectAction);
        actions.add(null);
        actions.add(createDatabaseAction);
        actions.add(refreshAction);
        actions.add(new DeleteAction());
        actions.add(null);
        actions.add(new MongoNativeToolsAction(getLookup()));
        final Action[] orig = super.getActions(ignored);
        if (orig.length > 0) {
            actions.add(null);
        }
        actions.addAll(Arrays.asList(orig));
        return actions.toArray(new Action[actions.size()]);
    }

    @Override
    public Action getPreferredAction() {
        return isConnected() ? null : new ConnectAction();
    }

    public void refreshChildren() {
        childFactory.refresh();
    }

    @Override
    protected Sheet createSheet() {
        final Sheet sheet = Sheet.createDefault();
        sheet.put(buildSheetSet());
        return sheet;
    }

    private Sheet.Set buildSheetSet() {
        final Sheet.Set set = Sheet.createPropertiesSet();
        set.put(new ConnectionNameProperty(getLookup()));
        if (isConnected()) {
            set.put(new MongoClientURIProperty(getLookup()));
        } else {
            final ConnectionInfo connection = getLookup().lookup(ConnectionInfo.class);
            try {
                final PropertySupport.Reflection<MongoClientURI> uriProperty
                    = new PropertySupport.Reflection<>(connection, MongoClientURI.class, MongoClientURIProperty.KEY);
                uriProperty.setPropertyEditorClass(MongoClientURIPropertyEditor.class);
                uriProperty.setDisplayName(MongoClientURIProperty.displayName());
                set.put(uriProperty);
            } catch (NoSuchMethodException ex) {
                Exceptions.printStackTrace(ex);
                throw new AssertionError();
            }
        }
        return set;
    }

    private void updateSheet() {
        getSheet().put(buildSheetSet());
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        switch (evt.getPropertyName()) {
            case ConnectionInfo.PREFS_KEY_DISPLAY_NAME:
                setDisplayName((String) evt.getNewValue());
                break;
            case ConnectionInfo.PREFS_KEY_URI:
                connectionHandler.disconnect();
                break;
        }
    }

    private final class ConnectionConverter implements InstanceContent.Convertor<ConnectionInfo, MongoClient> {

        @Override
        public MongoClient convert(ConnectionInfo t) {
            return connectionHandler.getClient();
        }

        @Override
        public Class<? extends MongoClient> type(ConnectionInfo t) {
            return MongoClient.class;
        }

        @Override
        public String id(ConnectionInfo t) {
            return "mongo"; //NOI18N
        }

        @Override
        public String displayName(ConnectionInfo t) {
            return id(t);
        }
    }

    private final class DeleteAction extends AbstractAction {

        public DeleteAction() {
            super(Bundle.ACTION_Delete());
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            final ConnectionInfo info = getLookup().lookup(ConnectionInfo.class);
            connectionHandler.disconnect();
            info.delete();
            ((MongoServicesNode) getParentNode()).getChildrenFactory().refresh();
        }
    }

    private final class ConnectAction extends AbstractAction {

        public ConnectAction() {
            super(Bundle.ACTION_Connect());
        }

        @Override
        public void actionPerformed(ActionEvent e) {
//            connect(true);
            connectionHandler.connect();
        }

    }

    private final class DisconnectAction extends AbstractAction {

        public DisconnectAction() {
            super(Bundle.ACTION_Disconnect());
        }

        @Override
        public void actionPerformed(ActionEvent e) {
//            disconnecter.close();
            connectionHandler.disconnect();
        }

    }

    public final class CreateDatabaseAction extends AbstractAction {

        public CreateDatabaseAction() {
            super(Bundle.ACTION_CreateDatabase());
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            final NotifyDescriptor.InputLine input = new ValidatingInputLine(
                Bundle.createDatabaseText(),
                Bundle.ACTION_CreateDatabase(),
                new DatabaseNameValidator(getLookup()));
            final Object dlgResult = DialogDisplayer.getDefault().notify(input);
            if (dlgResult.equals(NotifyDescriptor.OK_OPTION)) {
                final String dbName = input.getInputText().trim();
                MongoConnection connection = getLookup().lookup(MongoConnection.class);
                try {
                    final DB db = connection.getClient().getDB(dbName);
                    final DBObject collectionOptions = new BasicDBObject("capped", false);
                    db.createCollection("default", collectionOptions);
                    refreshChildren();
                } catch (MongoException ex) {
                    DialogDisplayer.getDefault().notify(
                        new NotifyDescriptor.Message(ex.getLocalizedMessage(), NotifyDescriptor.ERROR_MESSAGE));
                }
            }
        }
    }

}
