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
package org.netbeans.modules.mongodb.api;

import com.mongodb.client.MongoCursor;
import java.util.ArrayList;
import static java.util.Collections.emptyList;
import java.util.List;
import lombok.AllArgsConstructor;
import org.bson.BsonDocument;

/**
 *
 * @author Yann D'Isanto
 */
@AllArgsConstructor
public final class MongoCursorResult implements CollectionResult {

    public static final int DEFAULT_MAX_SIZE = 10000;

    private int maxSize;
    
    private List<BsonDocument> documents = emptyList();

    public MongoCursorResult(MongoCursor<BsonDocument> cursor) {
        this(cursor, DEFAULT_MAX_SIZE);
    }
    public MongoCursorResult(MongoCursor<BsonDocument> cursor, int maxSize) {
        this.maxSize = maxSize;
        setCursor(cursor);
    }

    public void setCursor(MongoCursor<BsonDocument> cursor) {
        documents = new ArrayList<>();
        while(cursor.hasNext() && documents.size() < maxSize) {
            documents.add(cursor.next());
        }
    }
    
    @Override
    public long getTotalElementsCount() {
        return documents.size();
    }

    @Override
    public List<BsonDocument> get(long offset, int count) {
        int fromIndex = (int) offset;
        int toIndex = fromIndex + count;
        return documents.subList(fromIndex, Math.min(toIndex, documents.size()));
    }

    @Override
    public Iterable<BsonDocument> iterable() {
        return documents;
    }
    
}
