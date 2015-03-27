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
package org.netbeans.modules.mongodb.util;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import org.openide.util.Exceptions;

/**
 *
 * @author Yann D'Isanto
 */
public final class Exporter implements Runnable {

    private final DB db;

    private final ExportProperties properties;

    public Exporter(DB db, ExportProperties properties) {
        this.db = db;
        this.properties = properties;
    }

    @Override
    public void run() {
        try {
            exportTo(properties.getFile().toPath());
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    private void exportTo(Path exportPath) throws IOException {
        final File exportFile = exportPath.toFile();
        Path backupPath = null;
        if (exportFile.exists()) {
            backupPath = new File(exportFile.getName() + ".export-backup").toPath();
            Files.move(exportPath, backupPath, StandardCopyOption.REPLACE_EXISTING);
        }
        try (Writer writer = new PrintWriter(exportFile, properties.getEncoding().name())) {
            export(writer);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
        if (Thread.interrupted()) {
            if (backupPath != null) {
                Files.move(backupPath, exportPath, StandardCopyOption.REPLACE_EXISTING);
            } else {
                Files.delete(exportPath);
            }
        }
    }

    private void export(Writer writer) {
        final PrintWriter output = new PrintWriter(writer);
        final DBCollection collection = db.getCollection(properties.getCollection());
        final DBCursor cursor = collection.find(properties.getCriteria(), properties.getProjection());
        if (properties.getSort() != null) {
            cursor.sort(properties.getSort());
        }
        if (properties.isJsonArray()) {
            output.print("[");
        }
        boolean first = true;
        for (DBObject document : cursor) {
            if (Thread.interrupted()) {
                return;
            }
            if (first) {
                first = false;
            } else if (properties.isJsonArray()) {
                output.print(",");
            }
            final String json = JSON.serialize(document);
            output.print(json);
            if (properties.isJsonArray() == false) {
                output.println();
            }
            output.flush();
        }
        if (properties.isJsonArray()) {
            output.println("]");
        }
        output.flush();
    }

    public ExportProperties getProperties() {
        return properties;
    }

}
