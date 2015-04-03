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

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import org.openide.util.NbBundle.Messages;

/**
 *
 * @author Yann D'Isanto
 */
@Messages("LBL_refresh=Refresh")
final class RefreshChildrenAction extends AbstractAction {

    private final RefreshableChildFactory<?> childFactory;

    public RefreshChildrenAction(RefreshableChildFactory<?> childFactory) {
        this(Bundle.LBL_refresh(), childFactory);
    }
    
    public RefreshChildrenAction(String name, RefreshableChildFactory<?> childFactory) {
        super(name);
        this.childFactory = childFactory;
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        childFactory.refresh();
    }
}
