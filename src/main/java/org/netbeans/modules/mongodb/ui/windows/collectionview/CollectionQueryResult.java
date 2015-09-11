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

import com.mongodb.MongoException;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.bson.BsonDocument;
import org.bson.conversions.Bson;
import org.netbeans.modules.mongodb.ui.util.DialogNotification;

/**
 *
 * @author Yann D'Isanto
 */
public final class CollectionQueryResult {

    public static final int DEFAULT_PAGE_SIZE = 20;

    @Getter
    @Setter
    private MongoCollection<BsonDocument> collection;

    @Getter
    private final List<BsonDocument> documents = new ArrayList<>();

    @Getter
    @Setter
    private int pageSize = DEFAULT_PAGE_SIZE;

    @Getter
    @Setter
    private int page = 1;

    @Getter
    private long totalDocumentsCount = 0;

    @Getter
    @Setter
    private Bson criteria;

    @Getter
    @Setter
    private Bson projection;

    @Getter
    @Setter
    private Bson sort;

    @Setter
    private CollectionQueryResultUpdateListener view;

    private boolean viewRefreshNecessary;

    public CollectionQueryResult(MongoCollection<BsonDocument> dbCollection) {
        this.collection = dbCollection;
    }

    public void update() {
        documents.clear();
        fireUpdateStarting();
        if (collection == null) {
            // TODO: error message?
            return;
        }
        try {
            
            totalDocumentsCount = criteria != null ? collection.count(criteria) : collection.count();
            FindIterable<BsonDocument> query = criteria != null ? collection.find(criteria) : collection.find();
            query = query.projection(projection).sort(sort);
            if (pageSize > 0) {
                final int toSkip = (page - 1) * pageSize;
                query.skip(toSkip).limit(pageSize);
            }
            for (BsonDocument document : query) {
                documents.add(document);
                fireDocumentAdded(document);                
            }
        } catch (MongoException ex) {
            DialogNotification.error(ex);

        }
        fireUpdateFinished();
        viewRefreshNecessary = true;
    }

    public void updateDocument(BsonDocument oldDocument, BsonDocument newDocument) {
        int index = documents.indexOf(oldDocument);
        if (index == -1) {
            throw new IllegalArgumentException("try to updated unknown document");
        }
        documents.set(index, newDocument);
        fireDocumentUpdated(newDocument, index);
    }

    public void refreshViewIfNecessary() {
        if (viewRefreshNecessary == false) {
            return;
        }
        fireUpdateStarting();
        for (BsonDocument document : documents) {
            fireDocumentAdded(document);
        }
        fireUpdateFinished();
        viewRefreshNecessary = false;
    }

    private void fireUpdateStarting() {
        if (view != null) {
            view.updateStarting(this);
        }
    }

    private void fireDocumentAdded(BsonDocument document) {
        if (view != null) {
            view.documentAdded(this, document);
        }
    }

    private void fireDocumentUpdated(BsonDocument document, int index) {
        if (view != null) {
            view.documentUpdated(this, document, index);
        }
    }

    private void fireUpdateFinished() {
        if (view != null) {
            view.updateFinished(this);
        }
    }

    public int getPageCount() {
        if (pageSize > 0) {
            final double pageCount = (double) totalDocumentsCount / (double) pageSize;
            return (int) Math.ceil(pageCount);
        }
        return 1;
    }
}
