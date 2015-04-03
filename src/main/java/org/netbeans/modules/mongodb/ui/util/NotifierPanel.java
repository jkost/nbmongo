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
package org.netbeans.modules.mongodb.ui.util;

import javax.swing.event.ChangeListener;
import org.openide.NotificationLineSupport;
import org.openide.util.ChangeSupport;

/**
 *
 * @author Yann D'Isanto
 */
public class NotifierPanel extends javax.swing.JPanel {

    private static final long serialVersionUID = 1L;

    protected final ChangeSupport changeSupport = new ChangeSupport(this);

    private NotificationLineSupport notificationLineSupport;

    public void addChangeListener(ChangeListener listener) {
        changeSupport.addChangeListener(listener);
    }

    public void removeChangeListener(ChangeListener listener) {
        changeSupport.removeChangeListener(listener);
    }

    protected void fireChange() {
        changeSupport.fireChange();
    }
    
    public NotificationLineSupport getNotificationLineSupport() {
        return notificationLineSupport;
    }

    public void setNotificationLineSupport(NotificationLineSupport notificationLineSupport) {
        this.notificationLineSupport = notificationLineSupport;
    }

    protected void clearNotificationLineSupport() {
        if (notificationLineSupport != null) {
            notificationLineSupport.clearMessages();
        }
    }

    protected void info(String message) {
        if (notificationLineSupport != null) {
            notificationLineSupport.setInformationMessage(message);
        }
    }

    protected void error(String message) {
        if (notificationLineSupport != null) {
            notificationLineSupport.setErrorMessage(message);
        }
    }

    protected void warn(String message) {
        if (notificationLineSupport != null) {
            notificationLineSupport.setWarningMessage(message);
        }
    }
}
