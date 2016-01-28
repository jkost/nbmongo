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

import lombok.Builder;
import lombok.Value;
import org.bson.BsonDocument;

/**
 *
 * @author Yann D'Isanto
 */
@Value
@Builder
public class FindCriteria {

    BsonDocument filter;

    BsonDocument projection;

    BsonDocument sort;

    public FindCriteria(BsonDocument filter, BsonDocument projection, BsonDocument sort) {
        this.filter = filter != null ? filter : new BsonDocument();
        this.projection = projection != null ? projection : new BsonDocument();
        this.sort = sort != null ? sort : new BsonDocument();
    }

    
    public FindCriteria.FindCriteriaBuilder copy() {
        return builder().filter(filter).projection(projection).sort(sort);
    }
    
    public static final FindCriteria EMPTY = new FindCriteria(null, null, null);
    
    public static enum SortOrder {
        ASCENDING,
        DESCENDING
    }
}
