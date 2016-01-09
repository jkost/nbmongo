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
package org.netbeans.modules.mongodb.ui.components.result_panel.actions;

import java.awt.event.ActionEvent;
import lombok.Getter;
import lombok.Setter;
import org.netbeans.modules.mongodb.CollectionInfo;
import org.netbeans.modules.mongodb.ui.windows.CollectionView;
import org.netbeans.modules.mongodb.ui.components.CollectionResultPanel;
import org.netbeans.modules.mongodb.ui.components.result_panel.views.treetable.BsonPropertyNode;
import org.openide.util.Lookup;
import org.openide.util.NbBundle.Messages;

/**
 *
 * @author Yann D'Isanto
 */
@Messages({
    "ACTION_findWithBsonProperty=Find with this property",
    "ACTION_findWithBsonProperty_tooltip=Perform a find on the collection with this property"
})
public final class FindWithBsonPropertyNodeAction extends QueryResultPanelAction {

    private static final long serialVersionUID = 1L;

    @Getter
    @Setter
    private BsonPropertyNode propertyNode;

    public FindWithBsonPropertyNodeAction(CollectionResultPanel resultPanel, BsonPropertyNode propertyNode) {
        super(resultPanel,
            Bundle.ACTION_findWithBsonProperty(),
            null,
            Bundle.ACTION_findWithBsonProperty_tooltip());
        this.propertyNode = propertyNode;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void actionPerformed(ActionEvent e) {
        Lookup lookup = getResultPanel().getLookup();
        CollectionInfo collection = lookup.lookup(CollectionInfo.class);
        if (collection != null) {
            CollectionView view = new CollectionView(collection, lookup, propertyNode.getBsonProperty().asDocument());
            view.open();
            view.requestActive();
        }
    }
}
