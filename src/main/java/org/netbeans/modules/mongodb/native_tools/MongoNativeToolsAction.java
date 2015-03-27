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
package org.netbeans.modules.mongodb.native_tools;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import org.netbeans.modules.mongodb.options.MongoNativeToolsOptions;
import org.openide.util.Lookup;
import org.openide.util.NbBundle.Messages;
import org.openide.util.actions.Presenter;

/**
 *
 * @author Yann D'Isanto
 */
@Messages({
    "ACTION_MongoNativeTools=Native Tools",
    "TOOLTIP_configureOptions=Configure native tools path in options"
})
public final class MongoNativeToolsAction extends AbstractAction implements Presenter.Popup {

    private final Lookup lookup;

    public MongoNativeToolsAction(Lookup lookup) {
        super(Bundle.ACTION_MongoNativeTools());
        this.lookup = lookup;
        if(isEnabled() == false) {
            putValue(SHORT_DESCRIPTION, Bundle.TOOLTIP_configureOptions());
        }
    }

    @Override
    public JMenuItem getPopupPresenter() {
        final JMenu menu = new JMenu(this);
        menu.add(new JMenuItem(new MongoShellExecAction(lookup)));
        menu.addSeparator();
        menu.add(new JMenuItem(new MongoDumpExecAction(lookup)));
        menu.add(new JMenuItem(new MongoRestoreExecAction(lookup)));
        menu.add(new JMenuItem(new MongoTopExecAction(lookup)));
        return menu;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // do nothing, only container for native tools action menu items
    }

    @Override
    public boolean isEnabled() {
        return MongoNativeToolsOptions.INSTANCE.isToolsFolderConfigured();
    }

}
