/*
 * Copyright (C) 2016 Yann D'Isanto
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
package org.netbeans.modules.mongodb.ui.components.repositories;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import org.netbeans.modules.mongodb.util.Repository;
import org.netbeans.modules.mongodb.util.Repository.RepositoryItem;
import org.openide.util.NbBundle.Messages;

/**
 *
 * @author Yann D'Isanto
 */
@Messages({
    "ACTION_load=load"
})
public class LoadItemAction<T extends RepositoryItem> extends AbstractAction {

    private static final long serialVersionUID = 1L;

    private final Repository<T, ?> repository;
    
    private final Callback<T> callback;

    public LoadItemAction(Repository<T, ?> repository, Callback<T> callback) {
        super(Bundle.ACTION_load());
        this.repository = repository;
        this.callback = callback;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        T item = RepositoryPanel.showLoadDialog(repository);
        if(item != null) {
            callback.loadSelectedItem(item);
        }
    }

    public static interface Callback<T extends RepositoryItem> {
        
        void loadSelectedItem(T item);
    }
}
