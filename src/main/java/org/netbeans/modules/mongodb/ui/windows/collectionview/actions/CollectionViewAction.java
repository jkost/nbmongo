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
package org.netbeans.modules.mongodb.ui.windows.collectionview.actions;

import javax.swing.AbstractAction;
import static javax.swing.Action.SHORT_DESCRIPTION;
import javax.swing.Icon;
import org.netbeans.modules.mongodb.ui.windows.CollectionView;

/**
 *
 * @author Yann D'Isanto
 */
public abstract class CollectionViewAction extends AbstractAction {

    private final CollectionView view;

    public CollectionViewAction(CollectionView view, String name) {
        this(view, name, null, null);
    }
    
    public CollectionViewAction(CollectionView view, String name, Icon icon) {
        this(view, name, icon, null);
    }

    public CollectionViewAction(CollectionView view, String name, Icon icon, String shortDescription) {
        super(name, icon);
        putValue(SHORT_DESCRIPTION, shortDescription);
        this.view = view;
    }

    public final CollectionView getView() {
        return view;
    }
}
