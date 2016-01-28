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

import javax.swing.JComboBox;
import javax.swing.JPanel;
import net.miginfocom.swing.MigLayout;
import org.netbeans.modules.mongodb.ui.util.ValidatingInputLine.InputValidator;
import org.openide.DialogDescriptor;
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
    
    @SuppressWarnings("unchecked")
    public static <T> T select(String title, T[] values) {
        return select(title, values, null);
    }
    
    @SuppressWarnings("unchecked")
    public static <T> T select(String title, T[] values, T selection) {
        JComboBox<T> combo = new JComboBox<>(values);
        combo.setSelectedItem(selection);
        JPanel panel = new JPanel(new MigLayout());
        panel.add(combo, "gap 5 5 5 5");
        final DialogDescriptor desc = new DialogDescriptor(panel, title);
        T item = null;
        if (NotifyDescriptor.OK_OPTION.equals(DialogDisplayer.getDefault().notify(desc))) {
            item = (T) combo.getSelectedItem();
        }
        return item;
    }
    
    public static String validatingInput(String text, String title, InputValidator validator) {
        return validatingInput(text, title, validator, null);
    }
    
    public static String validatingInput(String text, String title, InputValidator validator, String defaultValue) {
        InputLine input = new ValidatingInputLine(text, title, validator, defaultValue);
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
