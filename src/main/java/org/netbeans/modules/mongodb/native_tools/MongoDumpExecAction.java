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
package org.netbeans.modules.mongodb.native_tools;

import com.mongodb.MongoClientURI;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.netbeans.api.extexecution.ExecutionDescriptor;
import org.netbeans.modules.mongodb.CollectionInfo;
import org.netbeans.modules.mongodb.api.connections.ConnectionInfo;
import org.netbeans.modules.mongodb.DbInfo;
import org.openide.util.Lookup;
import org.openide.util.NbBundle.Messages;

/**
 *
 * @author Yann D'Isanto
 */
@Messages({
    "ACTION_MongoDump=mongodump"
})
public final class MongoDumpExecAction extends NativeToolExecAction {

    public MongoDumpExecAction(Lookup lookup) {
        super(Bundle.ACTION_MongoDump(), lookup, MongoNativeTool.MONGO_DUMP);
    }

    @Override
    protected ExecutionDescriptor getExecutionDescriptor() {
        return super.getExecutionDescriptor().frontWindow(true);
    }

    @Override
    protected Map<String, String> getOptionsFromContext() {
        final Map<String, String> options = new HashMap<>();
        final ConnectionInfo connectionInfo = getLookup().lookup(ConnectionInfo.class);
        if (connectionInfo != null) {
            final MongoClientURI uri = connectionInfo.getMongoURI();
            parseOptionsFromURI(uri, options);
        }
        final DbInfo dbInfo = getLookup().lookup(DbInfo.class);
        if(dbInfo != null) {
            options.put(MongoDumpOptions.DB, dbInfo.getDbName());
        }
        final CollectionInfo collection = getLookup().lookup(CollectionInfo.class);
        if(collection != null) {
            options.put(MongoDumpOptions.COLLECTION, collection.getName());
        }
        return options;
    }

    private void parseOptionsFromURI(MongoClientURI uri, Map<String, String> options) {
        if (uri.getUsername() != null && uri.getUsername().isEmpty() == false) {
            options.put(MongoDumpOptions.USERNAME, uri.getUsername());
        }
        if (uri.getPassword() != null && uri.getPassword().length > 0) {
            options.put(MongoDumpOptions.PASSWORD, new String(uri.getPassword()));
        }
        if (uri.getHosts() != null && uri.getHosts().isEmpty() == false) {
            final String hostWithPort = uri.getHosts().get(0);
            final Pattern p = Pattern.compile("(.*)(:(\\d+))?");
            final Matcher m = p.matcher(hostWithPort);
            if (m.matches()) {
                final String host = m.group(1);
                final String port = m.group(3);
                if (host.isEmpty() == false) {
                    options.put(MongoDumpOptions.HOST, host);
                    if (port != null) {
                        options.put(MongoDumpOptions.PORT, port);
                    }
                }
            }
        }
        if (uri.getDatabase() != null && uri.getDatabase().isEmpty() == false) {
            options.put(MongoDumpOptions.DB, uri.getDatabase());
        }
        if (uri.getCollection() != null && uri.getCollection().isEmpty() == false) {
            options.put(MongoDumpOptions.COLLECTION, uri.getCollection());
        }
    }
}
