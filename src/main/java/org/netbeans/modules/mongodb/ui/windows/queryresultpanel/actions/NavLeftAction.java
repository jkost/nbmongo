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
import javax.swing.ImageIcon;
import org.netbeans.modules.mongodb.resources.Images;
import org.netbeans.modules.mongodb.ui.windows.QueryResultPanel;
import org.openide.util.NbBundle.Messages;

/**
 *
 * @author Yann D'Isanto
 */
@Messages({
    "ACTION_navLeft=Previous Page",
    "ACTION_navLeft_tooltip=Previous Page"
})
public final class NavLeftAction extends QueryResultPanelAction {
    
    private static final long serialVersionUID = 1L;

    public NavLeftAction(QueryResultPanel resultPanel) {
        super(resultPanel,
            Bundle.ACTION_navLeft(),
            new ImageIcon(Images.NAV_LEFT_ICON),
            Bundle.ACTION_navLeft_tooltip());
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        getResultPanel().getResultPages().moveBackward();
        getResultPanel().updatePagination();
    }
}
