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

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.InsertOneModel;
import com.mongodb.client.model.WriteModel;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import org.bson.BsonDocument;
import org.bson.BsonType;
import org.netbeans.modules.mongodb.bson.Bsons;
import org.openide.util.Exceptions;

/**
 *
 * @author Yann D'Isanto
 */
public final class Importer implements Runnable {

    private final MongoDatabase db;

    private final ImportProperties properties;

    private final Runnable onDone;

    public Importer(MongoDatabase db, ImportProperties properties) {
        this(db, properties, null);
    }

    public Importer(MongoDatabase db, ImportProperties properties, Runnable onDone) {
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
        if (onDone != null) {
            onDone.run();
        }
    }

    @SuppressWarnings("unchecked")
    private void importFrom(Reader reader) throws IOException {
        final MongoCollection<BsonDocument> collection = db.getCollection(properties.getCollection(), BsonDocument.class);
        final BufferedReader br = new BufferedReader(reader);
        String line;
        List<WriteModel<BsonDocument>> requests = new ArrayList<>();
        while ((line = br.readLine()) != null) {
            if (Thread.interrupted()) {
                return;
            }
            line = line.trim();
            if (line.isEmpty()) {
                continue;
            }
            if (line.charAt(0) == '[') {
                List<BsonDocument> array = (List<BsonDocument>) Bsons.fromJson(line, BsonType.ARRAY);
                for (BsonDocument document : array) {
                    requests.add(new InsertOneModel<>(document));
                }
            } else {
                requests.add(new InsertOneModel<>(BsonDocument.parse(line)));
            }
        }
        if (requests.isEmpty() == false) {
            collection.bulkWrite(requests);
        }
    }

    public ImportProperties getProperties() {
        return properties;
    }

}
