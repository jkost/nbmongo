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
package org.netbeans.modules.mongodb.ui.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import javax.swing.AbstractListModel;

/**
 *
 * @author Yann D'Isanto
 */
public class DefaultListModel<E> extends AbstractListModel<E> implements Iterable<E> {

    private static final long serialVersionUID = 1L;

    private List<E> delegate = new ArrayList<>();

    /**
     * Returns the number of components in this list.
     * <p>
     * This method is identical to <code>size</code>, which implements the
     * <code>List</code> interface defined in the 1.2 Collections framework.
     * This method exists in conjunction with <code>setSize</code> so that
     * <code>size</code> is identifiable as a JavaBean property.
     *
     * @return the number of components in this list
     * @see #size()
     */
    @Override
    public int getSize() {
        return delegate.size();
    }

    /**
     * Returns the number of components in this list.
     *
     * @return the number of components in this list
     * @see List#size()
     */
    public int size() {
        return delegate.size();
    }

    /**
     * Returns the component at the specified index.
     * <blockquote>
     * <b>Note:</b> Although this method is not deprecated, the preferred method
     * to use is <code>get(int)</code>, which implements the <code>List</code>
     * interface defined in the 1.2 Collections framework.
     * </blockquote>
     *
     * @param index an index into this list
     * @return the component at the specified index
     * @exception ArrayIndexOutOfBoundsException if the <code>index</code> is
     * negative or greater than the current size of this list
     * @see #get(int)
     */
    @Override
    public E getElementAt(int index) {
        return delegate.get(index);
    }

    /**
     * Tests whether this list has any components.
     *
     * @return  <code>true</code> if and only if this list has no components,
     * that is, its size is zero; <code>false</code> otherwise
     * @see List#isEmpty()
     */
    public boolean isEmpty() {
        return delegate.isEmpty();
    }

    /**
     * Returns an iterator of the components of this list.
     *
     * @return an iterator of the components of this list
     * @see List#iterator()
     */
    @Override
    public Iterator<E> iterator() {
        return delegate.iterator();
    }

    /**
     * Returns a list iterator of the components of this list.
     *
     * @return a list iterator of the components of this list
     * @see List#listIterator()
     */
    public ListIterator<E> listIterator() {
        return delegate.listIterator();
    }

    /**
     * Returns a list iterator of the components of this list, starting at the
     * specified position in the list.
     *
     * @return a list iterator of the components of this list, starting at the
     * specified position in the list
     * @see List#listIterator(int)
     */
    public ListIterator<E> listIterator(int index) {
        return delegate.listIterator(index);
    }

    /**
     * Tests whether the specified object is a component in this list.
     *
     * @param elem an object
     * @return  <code>true</code> if the specified object is the same as a
     * component in this list
     * @see List#contains(Object)
     */
    public boolean contains(E elem) {
        return delegate.contains(elem);
    }

    /**
     * Searches for the first occurrence of <code>elem</code>.
     *
     * @param elem an object
     * @return the index of the first occurrence of the argument in this list;
     * returns <code>-1</code> if the object is not found
     * @see List#indexOf(Object)
     */
    public int indexOf(Object elem) {
        return delegate.indexOf(elem);
    }

    /**
     * Returns the index of the last occurrence of <code>elem</code>.
     *
     * @param elem the desired component
     * @return the index of the last occurrence of <code>elem</code> in the
     * list; returns <code>-1</code> if the object is not found
     * @see List#lastIndexOf(Object)
     */
    public int lastIndexOf(Object elem) {
        return delegate.lastIndexOf(elem);
    }

    /**
     * Returns the component at the specified index. Throws an
     * <code>ArrayIndexOutOfBoundsException</code> if the index is negative or
     * not less than the size of the list.
     * <blockquote>
     * <b>Note:</b> Although this method is not deprecated, the preferred method
     * to use is <code>get(int)</code>, which implements the <code>List</code>
     * interface defined in the 1.2 Collections framework.
     * </blockquote>
     *
     * @param index an index into this list
     * @return the component at the specified index
     * @see List#get(int)
     */
    public E get(int index) {
        return delegate.get(index);
    }

    /**
     * Sets the component at the specified <code>index</code> of this list to be
     * the specified element. The previous component at that position is
     * discarded.
     * <p>
     * Throws an <code>ArrayIndexOutOfBoundsException</code> if the index is
     * invalid.
     * <blockquote>
     * <b>Note:</b> Although this method is not deprecated, the preferred method
     * to use is <code>set(int,Object)</code>, which implements the
     * <code>List</code> interface defined in the 1.2 Collections framework.
     * </blockquote>
     *
     * @param element what the component is to be set to
     * @param index the specified index
     * @see List#set(int, Object)
     */
    public void set(E element, int index) {
        delegate.set(index, element);
        fireContentsChanged(this, index, index);
    }

    /**
     * Deletes the component at the specified index.
     * <p>
     * Throws an <code>ArrayIndexOutOfBoundsException</code> if the index is
     * invalid.
     * <blockquote>
     * <b>Note:</b> Although this method is not deprecated, the preferred method
     * to use is <code>remove(int)</code>, which implements the
     * <code>List</code> interface defined in the 1.2 Collections framework.
     * </blockquote>
     *
     * @param index the index of the object to remove
     * @see List#remove(int)
     */
    public void remove(int index) {
        delegate.remove(index);
        fireIntervalRemoved(this, index, index);
    }

    /**
     * Inserts the specified element as a component in this list at the
     * specified <code>index</code>.
     * <p>
     * Throws an <code>ArrayIndexOutOfBoundsException</code> if the index is
     * invalid.
     * <blockquote>
     * <b>Note:</b> Although this method is not deprecated, the preferred method
     * to use is <code>add(int,Object)</code>, which implements the
     * <code>List</code> interface defined in the 1.2 Collections framework.
     * </blockquote>
     *
     * @param element the component to insert
     * @param index where to insert the new component
     * @exception ArrayIndexOutOfBoundsException if the index was invalid
     * @see List#add(int,Object)
     */
    public void add(E element, int index) {
        delegate.add(index, element);
        fireIntervalAdded(this, index, index);
    }

    /**
     * Adds the specified component to the end of this list.
     *
     * @param element the component to be added
     * @see List#add(Object)
     */
    public void add(E element) {
        int index = delegate.size();
        delegate.add(element);
        fireIntervalAdded(this, index, index);
    }

    /**
     * Removes the first (lowest-indexed) occurrence of the argument from this
     * list.
     *
     * @param obj the component to be removed
     * @return  <code>true</code> if the argument was a component of this list;
     * <code>false</code> otherwise
     * @see List#remove(Object)
     */
    public boolean remove(E obj) {
        int index = indexOf(obj);
        boolean rv = delegate.remove(obj);
        if (index >= 0) {
            fireIntervalRemoved(this, index, index);
        }
        return rv;
    }

    /**
     * Removes all components from this list and sets its size to zero.
     * <blockquote>
     * <b>Note:</b> Although this method is not deprecated, the preferred method
     * to use is <code>clear</code>, which implements the <code>List</code>
     * interface defined in the 1.2 Collections framework.
     * </blockquote>
     *
     * @see List#clear()
     */
    public void clear() {
        int index1 = delegate.size() - 1;
        delegate = new ArrayList<>();
        if (index1 >= 0) {
            fireIntervalRemoved(this, 0, index1);
        }
    }

    /**
     * Returns a string that displays and identifies this object's properties.
     *
     * @return a String representation of this object
     */
    @Override
    public String toString() {
        return delegate.toString();
    }


    /* The remaining methods are included for compatibility with the
     * Java 2 platform Vector class.
     */
    /**
     * Returns an array containing all of the elements in this list in the
     * correct order.
     *
     * @return an array containing the elements of the list
     * @see Vector#toArray()
     */
    public Object[] toArray() {
        return delegate.toArray(new Object[delegate.size()]);
    }

    /**
     * Deletes the components at the specified range of indexes. The removal is
     * inclusive, so specifying a range of (1,5) removes the component at index
     * 1 and the component at index 5, as well as all components in between.
     * <p>
     * Throws an <code>ArrayIndexOutOfBoundsException</code> if the index was
     * invalid. Throws an <code>IllegalArgumentException</code> if
     * <code>fromIndex &gt; toIndex</code>.
     *
     * @param fromIndex the index of the lower end of the range
     * @param toIndex the index of the upper end of the range
     * @see #remove(int)
     */
    public void removeRange(int fromIndex, int toIndex) {
        if (fromIndex > toIndex) {
            throw new IllegalArgumentException("fromIndex must be <= toIndex");
        }
        for (int i = toIndex; i >= fromIndex; i--) {
            delegate.remove(i);
        }
        fireIntervalRemoved(this, fromIndex, toIndex);
    }

    
    public void addAll(Collection<E> items) {
        int index1 = delegate.size();
        if(items.isEmpty() == false && delegate.addAll(items)) {
            fireIntervalAdded(this, index1, delegate.size() - 1);
        }
    }

    public void addAll(int index, Collection<E> items) {
        if(items.isEmpty() == false && delegate.addAll(index, items)) {
            fireIntervalAdded(this, index, index + items.size() - 1);
        }
    }
}
