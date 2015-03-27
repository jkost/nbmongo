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
package org.netbeans.modules.mongodb.ui.options;

import java.io.File;
import org.netbeans.modules.mongodb.options.MongoNativeToolsFolderPredicate;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.filesystems.FileChooserBuilder;

/**
 *
 * @author Yann D'Isanto
 */
public final class MongoToolsFolderSelectionApprover implements FileChooserBuilder.SelectionApprover {

    @Override
    public boolean approve(File[] selection) {
        if (selection.length == 0) {
            return false;
        }
        final File selectedFolder = selection[0];
        if (selectedFolder.isDirectory()) {
            if (new MongoNativeToolsFolderPredicate().eval(selectedFolder.toPath())) {
                return true;
            }
        }
        DialogDisplayer.getDefault().notify(
            new NotifyDescriptor.Message("The selected folder doesn't contain the mongo tools executables",
                NotifyDescriptor.ERROR_MESSAGE));
        return false;
    }
}
