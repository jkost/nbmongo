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
import com.mongodb.DBObject;
import com.mongodb.util.JSON;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import org.openide.util.Exceptions;

/**
 *
 * @author Yann D'Isanto
 */
public final class Importer implements Runnable {

    private final DB db;

    private final ImportProperties properties;
    
    private final Runnable onDone;

    public Importer(DB db, ImportProperties properties) {
        this(db, properties, null);
    }

    public Importer(DB db, ImportProperties properties, Runnable onDone) {
        this.db = db;
        this.properties = properties;
        this.onDone = onDone;
    }

    
    @Override
    public void run() {
        try (InputStream input = new FileInputStream(properties.getFile())) {
            importFrom(new InputStreamReader(input, properties.getEncoding().name()));
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
        if(onDone != null) {
            onDone.run();
        }
    }

    private void importFrom(Reader reader) throws IOException {
        final DBCollection collection = db.getCollection(properties.getCollection());
        final BufferedReader br = new BufferedReader(reader);
        String line;
        while ((line = br.readLine()) != null) {
            if(Thread.interrupted()) {
                return;
            }
            collection.insert(parseLine(line));
        }
    }

    @SuppressWarnings("unchecked")
    private List<DBObject> parseLine(String line) {
        final Object obj = JSON.parse(line);
        if (obj instanceof List) {
            return (List<DBObject>) obj;
        }
        final List<DBObject> list = new ArrayList<>(1);
        list.add((DBObject) obj);
        return list;
    }

    public ImportProperties getProperties() {
        return properties;
    }

}
