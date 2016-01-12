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
package org.netbeans.modules.mongodb.options;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import org.netbeans.modules.mongodb.util.Version;
import org.openide.util.Exceptions;
import org.openide.util.NbPreferences;

/**
 *
 * @author Yann D'Isanto
 */
public enum MongoNativeToolsOptions {

    INSTANCE;

    private static final String TOOLS_FOLDER = "mongodb-tools-folder";

    private static final String TOOLS_VERSION = "mongodb-tools-version";

    private String toolsFolder;

    private Version toolsVersion;

    private MongoNativeToolsOptions() {
        load();
    }

    private Preferences getPreferences() {
        return NbPreferences.forModule(MongoNativeToolsOptions.class);
    }

    public void load() {
        Preferences prefs = getPreferences();
        toolsFolder = prefs.get(TOOLS_FOLDER, null);
        if(toolsFolder == null) {
            // try to use mongo shell executable path (a plugin older version option)
            String mongoExecPath = prefs.get("mongo-exec-path", null);
            if(mongoExecPath != null) {
                Path toolsFolderPath = Paths.get(mongoExecPath).getParent();
                if(Files.isDirectory(toolsFolderPath)) {
                    toolsFolder = toolsFolderPath.toString();
                }
            }
        }
        final String versionAsString = prefs.get(TOOLS_VERSION, null);
        if (versionAsString != null) {
            toolsVersion = new Version(versionAsString);
        }
    }

    public void store() {
        Preferences prefs = getPreferences();
        if (toolsFolder == null) {
            prefs.remove(TOOLS_FOLDER);
            prefs.remove(TOOLS_VERSION);
        } else {
            prefs.put(TOOLS_FOLDER, toolsFolder);
            prefs.put(TOOLS_VERSION, toolsVersion.toString());
        }
    }

    public boolean isToolsFolderConfigured() {
        return toolsFolder != null;
    }

    public String getToolsFolder() {
        return toolsFolder;
    }

    public Version getToolsVersion() {
        return toolsVersion;
    }

    public void setToolsFolder(String toolsFolder) {
        this.toolsFolder = toolsFolder;
        if (toolsFolder != null) {
            try {
                toolsVersion = readVersion(toolsFolder);
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
        } else {
            toolsVersion = null;
        }
    }

    private static Version readVersion(String toolsFolder) throws IOException {
        final Process process = new ProcessBuilder(
            new File(toolsFolder, "mongo").getAbsolutePath(),
            "--version"
        ).start();
        process.getOutputStream().close();
        process.getErrorStream().close();
        try (InputStream input = process.getInputStream()) {
            final String version = readVersion(input);
            return new Version(version);
        }
    }

    private static String readVersion(InputStream input) throws IOException {
        try (InputStreamReader isr = new InputStreamReader(input);
            BufferedReader reader = new BufferedReader(isr)) {
            final String line = reader.readLine();
            return line.substring(line.lastIndexOf(" ")).trim();
        }
    }
}
