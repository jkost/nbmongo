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

import java.util.Collections;
import static java.util.Collections.emptyList;
import java.util.List;
import org.bson.BsonDocument;

/**
 *
 * @author Yann D'Isanto
 */
public interface CollectionResult {
    
    long getTotalElementsCount();
    
    List<BsonDocument> get(long offset, int count);
    
    Iterable<BsonDocument> iterable();
    
    
    CollectionResult EMPTY = new CollectionResult() {

        @Override
        public long getTotalElementsCount() {
            return 0;
        }

        @Override
        public List<BsonDocument> get(long offset, int count) {
            return emptyList();
        }

        @Override
        public Iterable<BsonDocument> iterable() {
            return Collections.emptyList();
        }
        
    };
}
