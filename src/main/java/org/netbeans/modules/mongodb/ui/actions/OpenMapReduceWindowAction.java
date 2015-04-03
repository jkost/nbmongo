/* 
 * Copyright (C) 2015 Thomas Werner
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
package org.netbeans.modules.mongodb.ui.actions;

import org.netbeans.modules.mongodb.ui.windows.MapReduceTopComponent;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import org.openide.util.Lookup;
import org.openide.util.NbBundle.Messages;

/**
 * Opens a {@code MapReduceTopComponent}.
 *
 * @author thomaswerner35
 */
@Messages({
    "ACTION_OpenMapReduceWindow_name=Map/Reduce..."
})
public class OpenMapReduceWindowAction extends AbstractAction {

    private static final long serialVersionUID = 1L;
    
    private final Lookup lookup;
    
    public OpenMapReduceWindowAction(Lookup lookup) {
        super(Bundle.ACTION_OpenMapReduceWindow_name());
        this.lookup = lookup;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        final MapReduceTopComponent tc  = new MapReduceTopComponent(lookup);
        tc.open();
        tc.requestActive();
    }

}
