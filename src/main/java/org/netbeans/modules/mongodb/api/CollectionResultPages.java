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

import java.util.ArrayList;
import static java.util.Collections.emptyList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bson.BsonDocument;

/**
 *
 * @author Yann D'Isanto
 */
public final class CollectionResultPages {

    @Getter
    private CollectionResult queryResult;
    
    @Getter
    private int pageSize;
    
    private final boolean editable = false;
    
    @Getter
    private List<BsonDocument> currentPageItems = emptyList();
    
    /**
     * Global offset of the first element of the current page
     */
    private int pageFirstElementOffset = 0;
    
    private int pagesCount = 0;

    public CollectionResultPages(int pageSize) {
        this(CollectionResult.EMPTY, pageSize);
    }

    
    public CollectionResultPages(CollectionResult queryResult, int pageSize) {
        this.queryResult = queryResult;
        this.pageSize = pageSize;
    }

    public void setQueryResult(CollectionResult queryResult) {
        this.queryResult = queryResult;
        pageFirstElementOffset = 0;
        refresh();
    }

    public long getTotalElementsCount() {
        return queryResult.getTotalElementsCount();
    }
    
    public int getPageCount() {
//        final double pageCount = (double) queryResult.getTotalElementsCount() / (double) pageSize;
//        return (int) Math.ceil(pageCount);
        return pagesCount;
    }

    public int getPageIndex() {
        return (pageFirstElementOffset / pageSize) + 1;
    }

    public void setPageIndex(int pageIndex) {
        pageFirstElementOffset = (pageIndex - 1) * pageSize;
        refresh();
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
        setPageIndex((pageFirstElementOffset / pageSize) + 1);
    }

    
    public boolean canMoveForward() {
        return getPageIndex() < getPageCount();
    }

    public boolean canMoveBackward() {
        return pageFirstElementOffset > 0;
    }

    public void moveFirst() {
        if (pageFirstElementOffset == 0) {
            return;
        }
        pageFirstElementOffset = 0;
        refresh();
    }

    public void moveLast() {
        setPageIndex(getPageCount()); // page index start with 1
    }

    /**
     * Moves the window position forward (pagePosition += pageSize)
     */
    public void moveForward() {
        if (!canMoveForward()) {
            return;
        }
        pageFirstElementOffset += pageSize;
        refresh();
    }

    /**
     * Moves the window position reverse (pagePosition -= pageSize)
     */
    public void moveBackward() {
        if (!canMoveBackward()) {
            return;
        }
        pageFirstElementOffset = Math.max(pageFirstElementOffset - pageSize, 0);
        refresh();
    }
    
    public void refresh() {
        int pageIndex = getPageIndex();
        final double pageCount = (double) queryResult.getTotalElementsCount() / (double) pageSize;
        pagesCount = (int) Math.ceil(pageCount);
        currentPageItems = queryResult.get(pageFirstElementOffset, pageSize);
        for (Listener listener : listeners) {
            listener.pageChanged(this, pageIndex, currentPageItems);
        }
    }
    
    public void updateDocument(BsonDocument oldValue, BsonDocument newValue) {
        updateDocument(currentPageItems.indexOf(oldValue), newValue);
    }
    
    public void updateDocument(int index, BsonDocument newValue) {
        if(editable == false) {
            throw new UnsupportedOperationException("not editable");
        }
        BsonDocument oldValue = currentPageItems.set(index, newValue);
        for (Listener listener : listeners) {
            listener.pageObjectUpdated(index, oldValue, newValue);
        }
    }

    private final List<Listener> listeners = new ArrayList<>();

    public void addListener(Listener listener) {
        listeners.add(listener);
    }

    public void removeListener(Listener listener) {
        listeners.remove(listener);
    }

    public static interface Listener {

        void pageChanged(CollectionResultPages source, int pageIndex, List<BsonDocument> page);
        
        void pageObjectUpdated(int index, BsonDocument oldValue, BsonDocument newValue);
    }
}
