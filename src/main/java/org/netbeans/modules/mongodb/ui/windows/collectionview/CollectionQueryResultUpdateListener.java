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
package org.netbeans.modules.mongodb.ui.windows.collectionview;

import com.mongodb.DBObject;

/**
 * A listener to be notified on collection query result update events.
 *
 * @author Yann D'Isanto
 */
public interface CollectionQueryResultUpdateListener {

    /**
     * Invoked on collection query result update start.
     *
     * @param source the collection query result event source
     */
    void updateStarting(CollectionQueryResult source);

    /**
     * Invoked when a document is added to the collection query result during
     * update.
     *
     * @param source the collection query result event source
     * @param document the added document
     */
    void documentAdded(CollectionQueryResult source, DBObject document);

    /**
     * Invoked when a document is updated in the collection query result.
     *
     * @param source the collection query result event source
     * @param document the updated document
     * @param index the document index in the result list
     */
    void documentUpdated(CollectionQueryResult source, DBObject document, int index);

    /**
     * Invoked on collection query result update end.
     *
     * @param source the collection query result event source
     */
    void updateFinished(CollectionQueryResult source);
}
