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
package org.netbeans.modules.mongodb.ui.actions;

import java.awt.event.ActionEvent;
import java.util.concurrent.Callable;
import javax.swing.AbstractAction;
import org.netbeans.api.extexecution.ExecutionDescriptor;
import org.netbeans.api.extexecution.ExecutionService;
import org.openide.util.Lookup;

/**
 *
 * @author Yann D'Isanto
 */
public abstract class ExecutionAction extends AbstractAction {

    private final Lookup lookup;

    public ExecutionAction(String name, Lookup lookup) {
        super(name);
        this.lookup = lookup;
    }

    
    @Override
    public final void actionPerformed(ActionEvent e) {
        final Callable<Process> processCreator = getProcessCreator();
        if(processCreator == null) {
            return;
        }
        final ExecutionService service = ExecutionService.newService(
            processCreator,
            getExecutionDescriptor(),
            getDisplayName());
        service.run();
    }

    public final Lookup getLookup() {
        return lookup;
    }
    
    protected abstract String getDisplayName();
    
    protected ExecutionDescriptor getExecutionDescriptor() {
        return new ExecutionDescriptor();
    }
    
    /**
     * @return the process creator or null if no process should be executed.
     */
    protected abstract Callable<Process> getProcessCreator();
    
}
