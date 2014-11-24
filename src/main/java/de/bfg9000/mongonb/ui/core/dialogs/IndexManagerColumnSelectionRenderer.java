package de.bfg9000.mongonb.ui.core.dialogs;

import java.awt.Color;
import java.awt.Component;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JTable;
import javax.swing.ListCellRenderer;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.table.TableCellRenderer;

/**
 * {@code TableCellRenderer} that displays icons to visualize the selection options for index key attributes.
 *
 * @author thomaswerner35
 */
class IndexManagerColumnSelectionRenderer extends JLabel implements TableCellRenderer, ListCellRenderer {

    private static final String PREFIX = "/de/bfg9000/mongonb/ui/core/images/";

    private final Icon ICON_ASC = new ImageIcon(getClass().getResource(PREFIX +"columns-sorted-asc.png"));
    private final Icon ICON_DESC = new ImageIcon(getClass().getResource(PREFIX +"columns-sorted-desc.png"));
    private final Icon ICON_NONE = new ImageIcon(getClass().getResource(PREFIX +"columns-sorted-none.png"));
    private final Color listBackground = UIManager.getDefaults().getColor("List.background");
    private final Color listSelectionBg = UIManager.getDefaults().getColor("List.selectionBackground");
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
        setValue(value);
        return this;
    }

    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
                     boolean cellHasFocus) {
        setBackground(isSelected ? listSelectionBg : listBackground);
        setValue(value);
        return this;
    }

    private void setValue(Object value) {
        if(IndexManagerTableModel.KeySelection.Ascending.equals(value))
            setIcon(ICON_ASC);
        else if(IndexManagerTableModel.KeySelection.Descending.equals(value))
            setIcon(ICON_DESC);
        else
            setIcon(ICON_NONE);
    }

}
