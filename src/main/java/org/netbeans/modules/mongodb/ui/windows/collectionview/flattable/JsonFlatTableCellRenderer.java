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
package org.netbeans.modules.mongodb.ui.windows.collectionview.flattable;

import org.netbeans.modules.mongodb.options.JsonCellRenderingOptions;
import java.awt.Component;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import org.bson.types.ObjectId;
import org.netbeans.modules.mongodb.options.LabelCategory;
import org.netbeans.modules.mongodb.options.LabelFontConf;

/**
 *
 * @author Yann D'Isanto
 */
public final class JsonFlatTableCellRenderer extends DefaultTableCellRenderer {

    private static final Map<Class<?>, LabelCategory> LABEL_CATEGORIES = new HashMap<>();

    static {
        LABEL_CATEGORIES.put(String.class, LabelCategory.STRING_VALUE);
        LABEL_CATEGORIES.put(Integer.class, LabelCategory.INT_VALUE);
        LABEL_CATEGORIES.put(Double.class, LabelCategory.DECIMAL_VALUE);
        LABEL_CATEGORIES.put(Boolean.class, LabelCategory.BOOLEAN_VALUE);
        LABEL_CATEGORIES.put(ObjectId.class, LabelCategory.DOCUMENT);

    }

    private final JsonCellRenderingOptions options = JsonCellRenderingOptions.INSTANCE;

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        setToolTipText(getText());
        if (value != null && isSelected == false) {
            final LabelCategory valueLabelCategory = LABEL_CATEGORIES.get(value.getClass());
            if (valueLabelCategory != null) {
                final LabelFontConf valueFontConf = options.getLabelFontConf(valueLabelCategory);
                setFont(valueFontConf.getFont());
                setForeground(valueFontConf.getForeground());
                setBackground(valueFontConf.getBackground());
            } else {
                setFont(table.getFont());
                setForeground(table.getForeground());
                setBackground(table.getBackground());
            }
        }
        return this;
    }
}
