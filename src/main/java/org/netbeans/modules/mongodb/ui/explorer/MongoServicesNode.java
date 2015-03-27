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
package org.netbeans.modules.mongodb.ui.explorer;

import org.netbeans.modules.mongodb.resources.Images;
import org.netbeans.modules.mongodb.ui.components.NewConnectionPanel;
import com.mongodb.MongoClientURI;
import java.awt.event.ActionEvent;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.netbeans.api.core.ide.ServicesTabNodeRegistration;
import org.netbeans.modules.mongodb.ConnectionInfo;
import org.netbeans.modules.mongodb.native_tools.MongoNativeToolsAction;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.util.NbBundle.Messages;
import org.openide.util.NbPreferences;

/**
 * The services tab node for MongoDB connections.
 *
 * @author Tim Boudreau
 * @author Yann D'Isanto
 */
@ServicesTabNodeRegistration(displayName = "Mongo DB", iconResource = Images.MONGO_ICON_PATH, position = 3, name = "mongodb")
@Messages("MongoNodeName=Mongo DB")
public final class MongoServicesNode extends AbstractNode {

    private final ConnectionChildFactory factory;

    public MongoServicesNode() {
        this(new ConnectionChildFactory());
    }

    MongoServicesNode(ConnectionChildFactory factory) {
        super(Children.create(factory, false));
        this.factory = factory;
        setDisplayName(Bundle.MongoNodeName());
        setIconBaseWithExtension(Images.MONGO_ICON_PATH);
    }

    static {
        // By default every connection exception will result in a
        // popup dialog.  Try to crank down the volume.
        Logger mongoLogger = Logger.getLogger("com.mongodb");
        mongoLogger.setUseParentHandlers(false);
    }

    ConnectionChildFactory getChildrenFactory() {
        return factory;
    }

    static Preferences prefs() {
        return NbPreferences.forModule(MongoServicesNode.class).node("connections"); //NOI18N
    }

    @Override
    public Action[] getActions(boolean context) {
        return new Action[]{
            new NewConnectionAction(),
            null,
            new MongoNativeToolsAction(getLookup())
        };
    }

    @Messages("LBL_newConnection=New Connection")
    private class NewConnectionAction extends AbstractAction {

        public NewConnectionAction() {
            super(Bundle.LBL_newConnection());
        }

        @Override
        @Messages("TTL_newConnection=New MongoDB Connection")
        public void actionPerformed(ActionEvent e) {
            final NewConnectionPanel panel = new NewConnectionPanel();
            final DialogDescriptor desc = new DialogDescriptor(panel, Bundle.TTL_newConnection());
            panel.setNotificationLineSupport(desc.createNotificationLineSupport());
            panel.addChangeListener(new ChangeListener() {
                @Override
                public void stateChanged(ChangeEvent e) {
                    desc.setValid(panel.isOk());
                }
            });
            if (NotifyDescriptor.OK_OPTION.equals(DialogDisplayer.getDefault().notify(desc))) {
                final MongoClientURI uri = panel.getMongoURI();
                final String name = panel.getConnectionName();
                final Preferences prefs = prefs();
                try (ConnectionInfo info = new ConnectionInfo(prefs)) {
                    if (!name.isEmpty()) {
                        info.setDisplayName(name);
                    }
                    info.setMongoURI(uri);
                } finally {
                    factory.refresh();
                }
            }
        }
    }
}
