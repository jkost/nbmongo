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
package org.netbeans.modules.mongodb.ui.components.result_panel.views.flattable;

import java.awt.Component;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import org.bson.BsonValue;
import org.netbeans.modules.mongodb.bson.Bsons;
import org.netbeans.modules.mongodb.options.RenderingOptions.PrefsRenderingOptions;
import org.netbeans.modules.mongodb.options.RenderingOptions.RenderingOptionsItem;
import org.netbeans.modules.mongodb.util.BsonUtils;

/**
 *
 * @author Yann D'Isanto
 */
public final class BsonFlatTableCellRenderer extends DefaultTableCellRenderer {

    private static final long serialVersionUID = 1L;

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        BsonValue bsonValue = (BsonValue) value;
        DocumentsFlatTableModel model = (DocumentsFlatTableModel) table.getModel();
        if(bsonValue.isDocument() && model != null && model.isSortDocumentsFields()) {
            bsonValue = BsonUtils.sortDocumentFields(bsonValue.asDocument());
        }
        
        Component component = super.getTableCellRendererComponent(table, Bsons.shell(bsonValue), isSelected, hasFocus, row, column);
        setToolTipText(getText());
        if (value != null && isSelected == false) {
            RenderingOptionsItem rendering = PrefsRenderingOptions.INSTANCE.get(bsonValue.getBsonType());
            component.setFont(rendering.getFont());
            component.setForeground(rendering.getForeground());
            component.setBackground(rendering.getBackground());
        }
        return component;
    }
}
