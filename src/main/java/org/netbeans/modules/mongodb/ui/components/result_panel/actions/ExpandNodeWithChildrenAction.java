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
import javax.swing.tree.TreePath;
import org.netbeans.modules.mongodb.ui.components.CollectionResultPanel;
import org.netbeans.modules.mongodb.ui.components.result_panel.views.treetable.BsonValueNode;
import org.openide.util.NbBundle.Messages;

/**
 *
 * @author Yann D'Isanto
 */
@Messages({
    "ACTION_expandNode=Expand children nodes",
    "ACTION_expandNode_tooltip=Expand children nodes"
})
public final class ExpandNodeWithChildrenAction extends QueryResultPanelAction {

    private static final long serialVersionUID = 1L;

    private final TreePath treePath;
    
    public ExpandNodeWithChildrenAction(CollectionResultPanel resultPanel, TreePath treePath) {
        super(resultPanel,
            Bundle.ACTION_expandNode(),
            null,//new ImageIcon(Images.EXPAND_NODE_ICON),
            Bundle.ACTION_expandNode_tooltip());
        this.treePath = treePath;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        expand(treePath);
    }
    
    private void expand(TreePath path) {
        getResultPanel().getResultTreeTable().expandPath(path);
        BsonValueNode node = (BsonValueNode) path.getLastPathComponent();
        for (int i = 0; i < node.getChildCount(); i++) {
            BsonValueNode child = (BsonValueNode) node.getChildAt(i);
            expand(path.pathByAddingChild(child));
        }
    }
    
}
