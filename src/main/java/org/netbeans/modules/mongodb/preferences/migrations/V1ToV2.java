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
package org.netbeans.modules.mongodb.preferences.migrations;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.EnumMap;
import java.util.Map;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import org.bson.BsonType;
import org.netbeans.modules.mongodb.api.connections.ConnectionInfo;
import org.netbeans.modules.mongodb.options.RenderingOptions;
import org.netbeans.modules.mongodb.options.RenderingOptions.PrefsRenderingOptions;
import org.netbeans.modules.mongodb.options.RenderingOptions.RenderingOptionsItem;
import org.netbeans.modules.mongodb.preferences.Migrations.Migration;
import org.netbeans.modules.mongodb.preferences.Prefs;
import org.netbeans.modules.mongodb.ui.components.CollectionResultPanel;
import org.netbeans.modules.mongodb.util.PrefsRepositories;
import org.netbeans.modules.mongodb.util.Repository.PrefsRepository;

/**
 *
 * @author Yann D'Isanto
 */
public class V1ToV2 extends Migration {

    public V1ToV2(Migration next) {
        super(Prefs.Version.V2, next);
    }

    @Override
    protected void performMigration(Preferences prefs) throws Exception {
        migrateNativeToolsOptions(prefs);
        migrateNativeTools(prefs);
        migrateBsonRenderingOptions(prefs);
        migrateCollectionResultsPanel(prefs);
        migrateConnections(prefs);
        prefs.node("ui").removeNode();
    }

    private void migrateNativeToolsOptions(Preferences prefs) {
        Preferences v1Node = prefs;
        Preferences v2Node = Prefs.of(Prefs.OPTIONS).node(Prefs.NATIVE_TOOLS);
        String toolsFolder = v1Node.get("mongodb-tools-folder", null);
        if (toolsFolder == null) {
            // try to use mongo shell executable path (present in older plugin version)
            String mongoExecPath = v1Node.get("mongo-exec-path", null);
            if (mongoExecPath != null) {
                Path toolsFolderPath = Paths.get(mongoExecPath).getParent();
                if (Files.isDirectory(toolsFolderPath)) {
                    toolsFolder = toolsFolderPath.toString();
                }
            }
        }
        if (toolsFolder != null) {
            v2Node.put(Prefs.NativeToolsOptions.FOLDER, toolsFolder);
        }
        migrateString("mongodb-tools-version", Prefs.NativeToolsOptions.VERSION, v1Node, v2Node);
        v1Node.remove("mongo-exec-path");
        v1Node.remove("mongodb-tools-folder");
        v1Node.remove("mongodb-tools-version");
    }
    
    private void migrateNativeTools(Preferences prefs) throws BackingStoreException {
        Preferences v1Node = prefs.node("native_tools");
        Preferences v2Node = Prefs.of(Prefs.NATIVE_TOOLS);
        migrateString("dump-restore-path", v1Node, v2Node);
        v1Node.removeNode();
        
    }
    
    private void migrateBsonRenderingOptions(Preferences prefs) {
        Preferences v1Node = prefs.node("ui").node("bson-rendering");
        Preferences v2Node = Prefs.of(Prefs.OPTIONS).node(Prefs.BSON_RENDERING);
        
        Map<BsonType, RenderingOptions.RenderingOptionsItem> bsonOptions = new EnumMap<>(BsonType.class);
        StringBuilder sb = new StringBuilder();
        for (BsonType bsonType : BsonType.values()) {
            sb.setLength(0);
            sb.append("BsonType.").append(bsonType.name());
            String optionsKey = sb.toString();
            bsonOptions.put(bsonType, PrefsRenderingOptions.loadOptions(v1Node, optionsKey, RenderingOptions.DEFAULT.get(bsonType)));
        }
        RenderingOptionsItem fallback = PrefsRenderingOptions.loadOptions(v1Node, "fallback", RenderingOptions.DEFAULT.comment());
        RenderingOptionsItem comment = PrefsRenderingOptions.loadOptions(v1Node, "comment", RenderingOptions.DEFAULT.comment());
        RenderingOptionsItem key = PrefsRenderingOptions.loadOptions(v1Node, "key", RenderingOptions.DEFAULT.key());
        RenderingOptionsItem documentRoot = PrefsRenderingOptions.loadOptions(v1Node, "documentRoot", RenderingOptions.DEFAULT.documentRoot());
        RenderingOptionsItem documentId = PrefsRenderingOptions.loadOptions(v1Node, "documentId", RenderingOptions.DEFAULT.documentId());
        
        sb = new StringBuilder();
        for (BsonType bsonType : BsonType.values()) {
            sb.setLength(0);
            sb.append("BsonType.").append(bsonType.name());
            final String optionsKey = sb.toString();
            
            RenderingOptionsItem options = bsonOptions.get(bsonType);
            if(options == null) {
                options = RenderingOptions.DEFAULT.get(bsonType, fallback);
            }
            PrefsRenderingOptions.storeOptions(v2Node, optionsKey, options);
        }
        PrefsRenderingOptions.storeOptions(v2Node, "fallback", fallback);
        PrefsRenderingOptions.storeOptions(v2Node, "comment", comment);
        PrefsRenderingOptions.storeOptions(v2Node, "key", key);
        PrefsRenderingOptions.storeOptions(v2Node, "documentRoot", documentRoot);
        PrefsRenderingOptions.storeOptions(v2Node, "documentId", documentId);
    }

    private void migrateCollectionResultsPanel(Preferences prefs) throws BackingStoreException {
        Preferences v1Node = prefs.node(CollectionResultPanel.class.getName());
        Preferences v2Node = Prefs.of(Prefs.OPTIONS).node(Prefs.BSON_RENDERING);
        
        boolean displayDocumentEditionShortcutHint = v1Node.getBoolean("display-document-edition-shortcut-hint", true);
        v2Node.putBoolean("display-document-edition-shortcut-hint", displayDocumentEditionShortcutHint);
        String resultView = v1Node.get("result-view", null);
        if(resultView != null) {
            v2Node.put("result-view", resultView);
        }
        int pageSize = v1Node.getInt("result-view-table-page-size", 0);
        if(pageSize > 0) {
            v2Node.putInt("result-view-table-page-size", pageSize);
        }
        v1Node.removeNode();
    }
    
    private void migrateConnections(Preferences prefs) throws BackingStoreException {
        Preferences v1Node = prefs.node("connections");
        PrefsRepository<ConnectionInfo> repo = PrefsRepositories.CONNECTIONS.get();
        for (String id : v1Node.childrenNames()) {
            Preferences ciNode = v1Node.node(id);
            repo.put(new ConnectionInfo(
                    id, 
                    ciNode.get("displayName", null), 
                    ciNode.get("uri", null)));
        }
        v1Node.removeNode();
    }
    
}
