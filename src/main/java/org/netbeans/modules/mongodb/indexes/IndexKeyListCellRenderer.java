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
package org.netbeans.modules.mongodb.indexes;

import java.awt.Component;
import java.util.HashMap;
import java.util.Map;
import javax.swing.ImageIcon;
import javax.swing.JList;
import javax.swing.JTextPane;
import javax.swing.ListCellRenderer;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;
import org.netbeans.modules.mongodb.indexes.Index.KeySort;
import org.netbeans.modules.mongodb.resources.Images;
import org.openide.util.Exceptions;

/**
 *
 * @author Yann D'Isanto
 */
public class IndexKeyListCellRenderer extends JTextPane implements ListCellRenderer<Index.Key> {
    
    private static final long serialVersionUID = 1L;

    private final Style fieldsStyle;

    private final Style ascIcon;

    private final Style descIcon;
    
    private final Map<KeySort, Style> sortStyles;

    public IndexKeyListCellRenderer() {
        StyledDocument document = getStyledDocument();
        Style def = StyleContext.getDefaultStyleContext().
            getStyle(StyleContext.DEFAULT_STYLE);
        fieldsStyle = document.addStyle("fields", def);
        StyleConstants.setAlignment(fieldsStyle, StyleConstants.ALIGN_CENTER);

        ascIcon = document.addStyle("sortAsc", def);
        StyleConstants.setAlignment(ascIcon, StyleConstants.ALIGN_CENTER);
        StyleConstants.setIcon(ascIcon, new ImageIcon(Images.SORT_ASC_ICON, KeySort.ASCENDING.toString()));

        descIcon = document.addStyle("sortDesc", def);
        StyleConstants.setAlignment(descIcon, StyleConstants.ALIGN_CENTER);
        StyleConstants.setIcon(descIcon, new ImageIcon(Images.SORT_DESC_ICON, KeySort.DESCENDING.toString()));
        
        sortStyles = new HashMap<>(2);
        sortStyles.put(KeySort.ASCENDING, ascIcon);
        sortStyles.put(KeySort.DESCENDING, descIcon);
    }

    @Override
    public Component getListCellRendererComponent(JList<? extends Index.Key> list, Index.Key key, int index, boolean isSelected, boolean cellHasFocus) {
        if (isSelected) {
            setForeground(list.getSelectionForeground());
            setBackground(list.getSelectionBackground());
        } else {
            setForeground(list.getForeground());
            setBackground(list.getBackground());
        }
        setText("");
        if (key == null) {
            return this;
        }
        StyledDocument document = getStyledDocument();
        try {
            document.insertString(document.getLength(), key.getField(), fieldsStyle);
            document.insertString(document.getLength(), " ", fieldsStyle);
            document.insertString(document.getLength(), " ", sortStyles.get(key.getSort()));
        } catch (BadLocationException ex) {
            Exceptions.printStackTrace(ex);
        }
        return this;
    }
}
