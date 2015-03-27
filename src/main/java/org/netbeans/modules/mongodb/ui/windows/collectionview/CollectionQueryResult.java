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

import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoException;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;

/**
 *
 * @author Yann D'Isanto
 */
public final class CollectionQueryResult {

    public static final int DEFAULT_PAGE_SIZE = 20;

    @Getter
    @Setter
    private DBCollection dbCollection;

    @Getter
    private final List<DBObject> documents = new ArrayList<>();

    @Getter
    @Setter
    private int pageSize = DEFAULT_PAGE_SIZE;

    @Getter
    @Setter
    private int page = 1;

    @Getter
    private int totalDocumentsCount = 0;

    @Getter
    @Setter
    private DBObject criteria;

    @Getter
    @Setter
    private DBObject projection;

    @Getter
    @Setter
    private DBObject sort;

    @Setter
    private CollectionQueryResultUpdateListener view;

    private boolean viewRefreshNecessary;

    public CollectionQueryResult(DBCollection dbCollection) {
        this.dbCollection = dbCollection;
    }

    public void update() {
        documents.clear();
        fireUpdateStarting();
        if (dbCollection == null) {
            // TODO: error message?
            return;
        }
        try (DBCursor cursor = dbCollection.find(criteria, projection)) {
            if (sort != null) {
                cursor.sort(sort);
            }
            totalDocumentsCount = cursor.count();
            try (DBCursor pageCursor = getPageCursor(cursor)) {
                for (DBObject document : pageCursor) {
                    documents.add(document);
                    fireDocumentAdded(document);
                }
            }
        } catch (MongoException ex) {
            DialogDisplayer.getDefault().notify(
                new NotifyDescriptor.Message(ex.getLocalizedMessage(), NotifyDescriptor.ERROR_MESSAGE));

        }
        fireUpdateFinished();
        viewRefreshNecessary = true;
    }

    public void updateDocument(DBObject oldDocument, DBObject newDocument) {
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
        for (DBObject dBObject : documents) {
            fireDocumentAdded(dBObject);
        }
        fireUpdateFinished();
        viewRefreshNecessary = false;
    }

    private DBCursor getPageCursor(DBCursor queryCursor) {
        if (pageSize > 0) {
            final int toSkip = (page - 1) * pageSize;
            return queryCursor.skip(toSkip).limit(pageSize);
        }
        return queryCursor;
    }

    private void fireUpdateStarting() {
        if (view != null) {
            view.updateStarting(this);
        }
    }

    private void fireDocumentAdded(DBObject document) {
        if (view != null) {
            view.documentAdded(this, document);
        }
    }

    private void fireDocumentUpdated(DBObject document, int index) {
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
