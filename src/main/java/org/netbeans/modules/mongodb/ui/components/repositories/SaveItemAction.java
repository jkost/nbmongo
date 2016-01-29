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
import lombok.Getter;
import lombok.Setter;
import org.netbeans.modules.mongodb.ui.util.DialogNotification;
import org.netbeans.modules.mongodb.util.Repository;
import org.netbeans.modules.mongodb.util.Repository.RepositoryItem;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle.Messages;

/**
 *
 * @author Yann D'Isanto
 */
@Messages({
    "ACTION_save=save",
    "CONFIRM_title=Overwrite?",
    "# {0} - item key",
    "CONFIRM_message='{0}' already exists, do you want to overwrite it?"
})
public class SaveItemAction<T extends RepositoryItem> extends AbstractAction {

    private static final long serialVersionUID = 1L;

    private final Repository<T, ?> repository;

    @Getter
    @Setter
    private T itemToSave;

    public SaveItemAction(Repository<T, ?> repository) {
        super(Bundle.ACTION_save());
        this.repository = repository;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (itemToSave == null) {
            return;
        }
        try {
            String key = itemToSave.getKey();
            if (repository.exists(key) == false || 
                    DialogNotification.confirm(Bundle.CONFIRM_message(key), Bundle.CONFIRM_title())) {
                repository.put(itemToSave);
            }
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
        }
    }

}
