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
package org.netbeans.modules.mongodb.util;

import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;
import org.openide.util.RequestProcessor;
import org.openide.util.TaskListener;

/**
 *
 * @author Yann D'Isanto
 */
public abstract class AbstractTask<T extends Runnable> implements Runnable {

    private final RequestProcessor requestProcessor;

    private final T runnable;

    public AbstractTask(RequestProcessor requestProcessor, T runnable) {
        this.requestProcessor = requestProcessor;
        this.runnable = runnable;
    }

    public abstract String getLabel();

    public final T getRunnable() {
        return runnable;
    }

    @Override
    public void run() {
        final RequestProcessor.Task task = requestProcessor.create(runnable);
        final ProgressHandle progressHandle = ProgressHandleFactory.createHandle(getLabel(), task);
        task.addTaskListener(new TaskListener() {
            @Override
            public void taskFinished(org.openide.util.Task task) {
                //make sure that we get rid of the ProgressHandle
                //when the task is finished
                progressHandle.finish();
            }
        });
        //start the progresshandle the progress UI will show 500s after
        progressHandle.start();
        //this actually start the task
        task.schedule(0);
    }

}
