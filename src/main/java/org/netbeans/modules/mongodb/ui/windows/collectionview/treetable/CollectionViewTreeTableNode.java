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
package org.netbeans.modules.mongodb.ui.windows.collectionview.treetable;

import org.jdesktop.swingx.treetable.DefaultMutableTreeTableNode;

/**
 *
 * @author Yann D'Isanto
 */
public abstract class CollectionViewTreeTableNode<T> extends DefaultMutableTreeTableNode implements JsonNode {
    
    public CollectionViewTreeTableNode(T userObject) {
        super(userObject);
    }
    
    public CollectionViewTreeTableNode(T userObject, boolean allowsChildren) {
        super(userObject, allowsChildren);
    }

    @Override
    @SuppressWarnings("unchecked")
    public T getUserObject() {
        return (T) super.getUserObject();
    }
    
    @Override
    public boolean isEditable(int column) {
        return false;
    }    
}
