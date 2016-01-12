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
package org.netbeans.modules.mongodb.util;

import lombok.Getter;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;
import org.openide.util.RequestProcessor;
import org.openide.util.TaskListener;

/**
 *
 * @author Yann D'Isanto
 */
public final class Tasks {

    private static final RequestProcessor DEFAULT_REQUEST_PROCESSOR = new RequestProcessor(Tasks.class);
    
    public static <T extends Runnable> NbTask<T> create(String label, T runnable) {
        return create(DEFAULT_REQUEST_PROCESSOR, label, runnable, false);
    }
    public static <T extends Runnable> NbTask<T> create(String label, T runnable, boolean cancellable) {
        return create(DEFAULT_REQUEST_PROCESSOR, label, runnable, cancellable);
    }
    
    public static <T extends Runnable> NbTask<T> create(RequestProcessor requestProcessor, String label, T runnable) {
        return create(requestProcessor, label, runnable, false);
    }
    
    public static <T extends Runnable> NbTask<T> create(RequestProcessor requestProcessor, String label, T runnable, boolean cancellable) {
        return new SimpleTask<>(requestProcessor, runnable, label, cancellable);
    }
    
    public static abstract class NbTask<T extends Runnable> {

        private final RequestProcessor requestProcessor;

        @Getter
        private final T runnable;
        
        private final boolean cancellable;

        public NbTask(RequestProcessor requestProcessor, T runnable, boolean cancellable) {
            this.requestProcessor = requestProcessor;
            this.runnable = runnable;
            this.cancellable = cancellable;
        }

        public abstract String getLabel();

        public final RequestProcessor.Task execute() {
            final RequestProcessor.Task task = requestProcessor.create(runnable);
            final ProgressHandle progressHandle = cancellable 
                    ? ProgressHandleFactory.createHandle(getLabel(), task)
                    : ProgressHandleFactory.createHandle(getLabel());
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
            return task;
        }

    }
    
    public static class SimpleTask<T extends Runnable> extends NbTask<T> {

        @Getter
        private final String label;

        public SimpleTask(RequestProcessor requestProcessor, T runnable, String label, boolean cancellable) {
            super(requestProcessor, runnable, cancellable);
            this.label = label;
        }
    
    }

    private Tasks() {
    }

}
