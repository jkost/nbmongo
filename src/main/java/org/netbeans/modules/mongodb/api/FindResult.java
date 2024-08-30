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

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.Getter;
import lombok.Setter;
import org.bson.BsonDocument;

/**
 *
 * @author Yann D'Isanto
 */
public final class FindResult implements CollectionResult {

    @Getter
    private final MongoCollection<BsonDocument> collection;
    
    @Getter
    @Setter
    private FindCriteria findCriteria;

    public FindResult(MongoCollection<BsonDocument> collection, FindCriteria findCriteria) {
        Objects.requireNonNull(collection, "collection can't be null");
        this.collection = collection;
        this.findCriteria = findCriteria != null ? findCriteria : FindCriteria.EMPTY;
    }
    
    
    
    @Override
    public long getTotalElementsCount() {
        return collection.countDocuments(findCriteria.getFilter());
    }

    @Override
    public List<BsonDocument> get(long offset, int count) {
        return findIterable()
                .skip((int) offset)
                .limit(count)
                .into(new ArrayList<>());
    }

    @Override
    public Iterable<BsonDocument> iterable() {
        return findIterable();
    }
    
    private FindIterable<BsonDocument> findIterable() {
        return collection
                .find(findCriteria.getFilter() != null ? findCriteria.getFilter() : new BsonDocument())
                .projection(findCriteria.getProjection())
                .sort(findCriteria.getSort());
    }
    
}
