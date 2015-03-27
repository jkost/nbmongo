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

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Icon;

/**
 * A copy to cliboard action for a given object.
 *
 * @author Yann D'Isanto
 */
public class CopyObjectToClipboardAction<T> extends AbstractAction {
    
    private static final long serialVersionUID = 1L;

    /**
     * The object to copy to the clipboard.
     */
    private final T object;

    /**
     * Creates a new instance.
     *
     * @param name the action name.
     * @param object the object to copy to the clipboard.
     */
    public CopyObjectToClipboardAction(String name, T object) {
        this(name, null, object);
    }

    /**
     * Creates a new instance.
     *
     * @param name the action name.
     * @param icon the action icon
     * @param object the object to copy to the clipboard.
     */
    public CopyObjectToClipboardAction(String name, Icon icon, T object) {
        super(name, icon);
        this.object = object;
    }

    @Override
    public final void actionPerformed(ActionEvent e) {
        final Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(convertToStringSelection(object), null);
    }
    
    /**
     * Converts the specified object into a string to be copied in the
     * clipboard. The default implementation rely on {@code String.valueOf()}
     * method. Overides this method to provide a different implementation.
     *
     * @param object the object to convert in String selection.
     * @return a String selection instance.
     */
    public StringSelection convertToStringSelection(T object) {
        return new StringSelection(String.valueOf(object));
    }
}
