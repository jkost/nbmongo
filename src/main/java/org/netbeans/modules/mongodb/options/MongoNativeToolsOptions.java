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
import java.util.prefs.Preferences;
import org.netbeans.modules.mongodb.preferences.Prefs;
import org.netbeans.modules.mongodb.util.Version;
import org.openide.util.Exceptions;

/**
 *
 * @author Yann D'Isanto
 */
public enum MongoNativeToolsOptions {

    INSTANCE;

    private String toolsFolder;

    private Version toolsVersion;

    private MongoNativeToolsOptions() {
        load();
    }

    private Preferences prefs() {
        return Prefs.of(Prefs.OPTIONS).node(Prefs.NATIVE_TOOLS);
    }

    public void load() {
        Preferences prefs = prefs();
        toolsFolder = prefs.get(Prefs.NativeToolsOptions.FOLDER, null);
        final String versionAsString = prefs.get(Prefs.NativeToolsOptions.VERSION, null);
        if (versionAsString != null) {
            toolsVersion = new Version(versionAsString);
        }
    }

    public void store() {
        Preferences prefs = prefs();
        if (toolsFolder == null) {
            prefs.remove(Prefs.NativeToolsOptions.FOLDER);
            prefs.remove(Prefs.NativeToolsOptions.VERSION);
        } else {
            prefs.put(Prefs.NativeToolsOptions.FOLDER, toolsFolder);
            prefs.put(Prefs.NativeToolsOptions.VERSION, toolsVersion.toString());
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
