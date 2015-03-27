/* 
 * Copyright (C) 2015 Thomas Werner
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
package de.bfg9000.mongonb.ui.core.dialogs;

import java.awt.Color;
import java.awt.Component;
import javax.swing.JCheckBox;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.table.TableCellRenderer;

/**
 * {@code TableCellRenderer} that displays a checkbox.
 *
 * @author thomaswerner35
 */
class IndexManagerBooleanRenderer extends JCheckBox implements TableCellRenderer {

    private final Color tableBackground = UIManager.getDefaults().getColor("Table.background");
    private final Color tableDisabledBg = UIManager.getDefaults().getColor("ComboBox.disabledBackground");
    private final Color tableSelectionBg = UIManager.getDefaults().getColor("Table.selectionBackground");

    {
        setOpaque(true);
        setHorizontalAlignment(SwingConstants.CENTER);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
                     int row, int column) {
        setBackground(isSelected ?
                      tableSelectionBg : table.getModel().isCellEditable(row, 1) ? tableBackground : tableDisabledBg);
        setSelected(Boolean.TRUE.equals(value));
        return this;
    }

}
