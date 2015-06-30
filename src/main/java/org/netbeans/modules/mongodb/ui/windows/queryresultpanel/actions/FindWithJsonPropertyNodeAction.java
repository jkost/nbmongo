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
package org.netbeans.modules.mongodb.ui.windows.queryresultpanel.actions;

import java.awt.event.ActionEvent;
import lombok.Getter;
import lombok.Setter;
import org.netbeans.modules.mongodb.CollectionInfo;
import org.netbeans.modules.mongodb.ui.util.JsonPropertyEditor;
import org.netbeans.modules.mongodb.ui.windows.CollectionView;
import org.netbeans.modules.mongodb.ui.windows.QueryResultPanel;
import org.netbeans.modules.mongodb.ui.windows.collectionview.treetable.JsonPropertyNode;
import org.netbeans.modules.mongodb.util.JsonProperty;
import org.openide.util.Lookup;
import org.openide.util.NbBundle.Messages;

/**
 *
 * @author Yann D'Isanto
 */
@Messages({
    "findWithJsonPropertyTitle=Find with json property",
    "ACTION_findWithJsonProperty=Find with json property",
    "ACTION_findWithJsonProperty_tooltip=Find with selected JSON Property"
})
public final class FindWithJsonPropertyNodeAction extends QueryResultPanelAction {

    private static final long serialVersionUID = 1L;

    @Getter
    @Setter
    private JsonPropertyNode propertyNode;

    public FindWithJsonPropertyNodeAction(QueryResultPanel resultPanel, JsonPropertyNode propertyNode) {
        super(resultPanel,
            Bundle.ACTION_findWithJsonProperty(),
            null,
            Bundle.ACTION_findWithJsonProperty_tooltip());
        this.propertyNode = propertyNode;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void actionPerformed(ActionEvent e) {
        JsonProperty property = propertyNode.getUserObject();
        Lookup lookup = getResultPanel().getLookup();
        CollectionInfo collection = lookup.lookup(CollectionInfo.class);
        if (collection != null) {
            CollectionView view = new CollectionView(collection, lookup, property.asDocument());
//            view.getQueryEditor().setCriteria(property.asDocument());
//            view.updateQueryFieldsFromEditor();
            view.open();
            view.requestActive();
        }
    }
}
