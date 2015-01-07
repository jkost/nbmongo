package de.bfg9000.mongonb.ui.core.windows;

import com.mongodb.DBObject;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;

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
