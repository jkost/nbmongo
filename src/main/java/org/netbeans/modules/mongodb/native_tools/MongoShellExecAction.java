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
import java.util.concurrent.Callable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.netbeans.api.extexecution.ExecutionDescriptor;
import org.netbeans.modules.mongodb.ConnectionInfo;
import org.netbeans.modules.mongodb.ui.actions.ExecutionAction;
import org.netbeans.modules.mongodb.util.ProcessCreator;
import org.openide.util.Lookup;
import org.openide.util.NbBundle.Messages;

/**
 *
 * @author Yann D'Isanto
 */
@Messages({
    "ACTION_MongoShell=Mongo Shell",
    "# {0} - connection uri",
    "mongoShellOutputTitle=mongo - {0}"
})
public final class MongoShellExecAction extends ExecutionAction {

    public MongoShellExecAction(Lookup lookup) {
        super(Bundle.ACTION_MongoShell(), lookup);
    }

    @Override
    protected String getDisplayName() {
        final MongoClientURI uri = getMongoURI();
        return Bundle.mongoShellOutputTitle(uri != null ? uri.toString() : "localhost");
    }

    @Override
    protected ExecutionDescriptor getExecutionDescriptor() {
        return super.getExecutionDescriptor().inputVisible(true).frontWindow(true);
    }

    @Override
    protected Callable<Process> getProcessCreator() {
        final String shellExec = MongoNativeTool.MONGO_SHELL.getExecFullPath().toString();
        final ProcessCreator.Builder builder = new ProcessCreator.Builder(shellExec);
        final MongoClientURI uri = getMongoURI();
        if(uri != null) {
            parseOptionsFromURI(builder, uri);
        }
        return builder.build();
    }

    private void parseOptionsFromURI(ProcessCreator.Builder builder, MongoClientURI uri) {
        if (uri.getUsername() != null && uri.getUsername().isEmpty() == false) {
            builder.option("--username", uri.getUsername());
        }
        if (uri.getPassword() != null && uri.getPassword().length > 0) {
            builder.option("--password", new String(uri.getPassword()));
        }
        if (uri.getHosts() != null && uri.getHosts().isEmpty() == false) {
            final String hostWithPort = uri.getHosts().get(0);
            final Pattern p = Pattern.compile("(.*)(:(\\d+))?");
            final Matcher m = p.matcher(hostWithPort);
            if (m.matches()) {
                final String host = m.group(1);
                final String port = m.group(3);
                if (host.isEmpty() == false) {
                    builder.option("--host", host);
                    if (port != null) {
                        builder.option("--port", port);
                    }
                }
            }
        }
    }

    private MongoClientURI getMongoURI() {
        final ConnectionInfo ci = getLookup().lookup(ConnectionInfo.class);
        return ci != null ? ci.getMongoURI() : null;
    }
}
