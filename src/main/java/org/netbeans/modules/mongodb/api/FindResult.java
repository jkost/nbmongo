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

import com.mongodb.client.MongoCollection;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bson.BsonDocument;
import org.bson.conversions.Bson;

/**
 *
 * @author Yann D'Isanto
 */
@AllArgsConstructor
public final class FindResult implements CollectionResult {

    private final MongoCollection<BsonDocument> collection;
    
    @Getter
    @Setter
    private Bson filter;

    @Getter
    @Setter
    private Bson projection;

    @Getter
    @Setter
    private Bson sort;

    @Override
    public long getTotalElementsCount() {
        return collection.count(filter);
    }

    @Override
    public List<BsonDocument> get(long offset, int count) {
        return collection
                .find(filter != null ? filter : new BsonDocument())
                .projection(projection)
                .sort(sort)
                .skip((int) offset)
                .limit(count)
                .into(new ArrayList<BsonDocument>());
    }
}
