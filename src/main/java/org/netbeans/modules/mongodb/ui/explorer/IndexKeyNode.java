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
package org.netbeans.modules.mongodb.ui.explorer;

import org.netbeans.modules.mongodb.indexes.Index;
import org.netbeans.modules.mongodb.resources.Images;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.util.Lookup;

/**
 *
 * @author Yann D'Isanto
 */
final class IndexKeyNode extends AbstractNode {

    private final Index.Key key;

    public IndexKeyNode(Index.Key key, Lookup lookup) {
        super(Children.LEAF, lookup);
        this.key = key;
        setIconBaseWithExtension(getIconPath());
    }

    @Override
    public String getName() {
        return key.getField();
    }

    public String getIconPath() {
        switch (key.getSort()) {
            case ASCENDING:
                return Images.SORT_ASC_ICON_PATH;
            case DESCENDING:
                return Images.SORT_DESC_ICON_PATH;
            default:
                throw new AssertionError();
        }
    }
}
