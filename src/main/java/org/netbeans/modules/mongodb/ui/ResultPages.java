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
package org.netbeans.modules.mongodb.ui;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import org.bson.BsonDocument;

/**
 *
 * @author Yann D'Isanto
 */
public final class ResultPages implements ResultCache.Listener {

    @Getter
    private ResultCache cache;

    @Getter
    private int pageSize;

    private int pageFirstElementOffset = 0;

    public ResultPages() {
        this(ResultCache.EMPTY, 20);
    }
    
    public ResultPages(int pageSize) {
        this(ResultCache.EMPTY, pageSize);
    }
    
    public ResultPages(ResultCache cache, int pageSize) {
        this.cache = cache;
        cache.addListener(this);
        this.pageSize = pageSize;
    }

    public void setCache(ResultCache cache) {
        if(this.cache != null) {
            this.cache.removeListener(this);
        }
        this.cache = cache;
        cache.addListener(this);
        if (pageFirstElementOffset > cache.getObjectsCount()) {
            pageFirstElementOffset = 0;
        }
        firePageChanged();
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
        firePageChanged();
    }

    public void moveLast() {
        setPageIndex(getPageCount()); // page index start with 1
        firePageChanged();
    }

    /**
     * Moves the window position forward (pagePosition += pageSize)
     */
    public void moveForward() {
        if (!canMoveForward()) {
            return;
        }
        pageFirstElementOffset += pageSize;
        firePageChanged();
    }

    /**
     * Moves the window position reverse (pagePosition -= pageSize)
     */
    public void moveBackward() {
        if (!canMoveBackward()) {
            return;
        }
        pageFirstElementOffset = Math.max(pageFirstElementOffset - pageSize, 0);
        firePageChanged();
    }

    public List<BsonDocument> getPageContent() {
        return cache.get(pageFirstElementOffset, pageSize);
    }

    public long getTotalElementsCount() {
        return cache.getObjectsCount();
    }

    public int getPageCount() {
        final double pageCount = (double) getTotalElementsCount() / (double) pageSize;
        return (int) Math.ceil(pageCount);
    }

    public int getPageIndex() {
        return (pageFirstElementOffset / pageSize) + 1;
    }

    public void setPageIndex(int pageIndex) {
        pageFirstElementOffset = (pageIndex - 1) * pageSize;
        firePageChanged();
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
        cache.setLoadingBlockSize(pageSize);
        setPageIndex((pageFirstElementOffset / pageSize) + 1);
        firePageChanged();
    }

    private final List<ResultPagesListener> listeners = new ArrayList<>();

    public void addResultPagesListener(ResultPagesListener listener) {
        listeners.add(listener);
    }

    public void removeResultPagesListener(ResultPagesListener listener) {
        listeners.remove(listener);
    }

    void firePageChanged() {
        int pageIndex = getPageIndex();
        List<BsonDocument> page = getPageContent();
        for (ResultPagesListener listener : listeners) {
            listener.pageChanged(this, pageIndex, page);
        }
    }

    @Override
    public void objectUpdated(int index, BsonDocument oldValue, BsonDocument newValue) {
        for (ResultPagesListener listener : listeners) {
            listener.pageObjectUpdated(index, oldValue, newValue);
        }
    }
    
    public static interface ResultPagesListener {

        void pageChanged(ResultPages source, int pageIndex, List<BsonDocument> page);
        
        void pageObjectUpdated(int index, BsonDocument oldValue, BsonDocument newValue);
    }
}
