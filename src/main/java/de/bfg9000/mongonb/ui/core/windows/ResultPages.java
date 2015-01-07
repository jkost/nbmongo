/*
 * The MIT License
 *
 * Copyright 2014 Yann D'Isanto.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package de.bfg9000.mongonb.ui.core.windows;

import com.mongodb.DBObject;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;

/**
 *
 * @author Yann D'Isanto
 */
public final class ResultPages {

    @Getter
    private ResultCache cache;

    @Getter
    private int pageSize;

    private int pageFirstElementOffset = 0;

    public ResultPages(ResultCache cache, int pageSize) {
        this.cache = cache;
        this.pageSize = pageSize;
    }

    public void setCache(ResultCache cache) {
        this.cache = cache;
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

    public List<DBObject> getPageContent() {
        return cache.get(pageFirstElementOffset, pageSize);
    }

    public int getTotalElementsCount() {
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
        List<DBObject> page = getPageContent();
        for (ResultPagesListener listener : listeners) {
            listener.pageChanged(this, pageIndex, page);
        }
    }

    public static interface ResultPagesListener {

        void pageChanged(ResultPages source, int pageIndex, List<DBObject> page);
    }
}
