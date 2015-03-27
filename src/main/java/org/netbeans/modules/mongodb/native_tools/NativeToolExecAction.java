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

import java.util.Map;
import java.util.concurrent.Callable;
import org.netbeans.modules.mongodb.ui.actions.ExecutionAction;
import org.netbeans.modules.mongodb.ui.native_tools.NativeToolOptionsDialog;
import org.netbeans.modules.mongodb.util.ProcessCreator;
import org.openide.util.Lookup;

/**
 *
 * @author Yann D'Isanto
 */
public abstract class NativeToolExecAction extends ExecutionAction {

    protected final MongoNativeTool tool;

    public NativeToolExecAction(String name, Lookup lookup, MongoNativeTool tool) {
        super(name, lookup);
        this.tool = tool;
    }

    @Override
    protected final String getDisplayName() {
        return tool.getExecBaseName();
    }

    protected abstract Map<String, String> getOptionsFromContext();

    @Override
    protected final Callable<Process> getProcessCreator() {
        final NativeToolOptionsDialog dialog = NativeToolOptionsDialog.get(tool);
        if (dialog.show(getOptionsFromContext())) {
            return new ProcessCreator.Builder(tool.getExecFullPath().toString())
                    .options(dialog.getOptions())
                    .args(dialog.getArgs())
                    .build();
        }
        return null;
    }
}
