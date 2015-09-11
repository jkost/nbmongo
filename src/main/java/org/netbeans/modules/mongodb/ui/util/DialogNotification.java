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

import org.netbeans.modules.mongodb.ui.util.ValidatingInputLine.InputValidator;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.NotifyDescriptor.Confirmation;
import org.openide.NotifyDescriptor.InputLine;
import org.openide.NotifyDescriptor.Message;

/**
 *
 * @author Yann D'Isanto
 */
public final class DialogNotification {

    public static boolean confirm(Object message, String title) {
        return notify(new Confirmation(message, title, NotifyDescriptor.YES_NO_OPTION)) == NotifyDescriptor.YES_OPTION;
    }
    
    public static boolean confirm(Object message) {
        return notify(new Confirmation(message, NotifyDescriptor.YES_NO_OPTION)) == NotifyDescriptor.YES_OPTION;
    }
    
    public static void error(Throwable throwable) {
        error(throwable.getLocalizedMessage());
    }
    
    public static void error(String message) {
        notify(new Message(message, NotifyDescriptor.ERROR_MESSAGE));
    }
    
    public static String validatingInput(String text, String title, InputValidator validator) {
        InputLine input = new ValidatingInputLine(text, title, validator);
        if(notify(input) == NotifyDescriptor.OK_OPTION) {
            return input.getInputText();
        }
        return null;
    }
    
    private static Object notify(NotifyDescriptor descriptor) {
        return displayer().notify(descriptor);
    }
    
    private static void notifyLater(NotifyDescriptor descriptor) {
        displayer().notifyLater(descriptor);
    }
    
    private static DialogDisplayer displayer() {
        return DialogDisplayer.getDefault();
    }
    
    private DialogNotification() {
    }
}
