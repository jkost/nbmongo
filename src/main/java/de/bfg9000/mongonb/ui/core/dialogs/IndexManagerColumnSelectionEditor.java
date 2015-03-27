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

import static de.bfg9000.mongonb.ui.core.dialogs.IndexManagerTableModel.KeySelection.Ascending;
import static de.bfg9000.mongonb.ui.core.dialogs.IndexManagerTableModel.KeySelection.Descending;
import static de.bfg9000.mongonb.ui.core.dialogs.IndexManagerTableModel.KeySelection.None;
import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;

/**
 * {@code TableCellEditor} that displays icons to visualize the selection options for index key attributes.
 *
 * @author thomaswerner35
 */
class IndexManagerColumnSelectionEditor extends DefaultCellEditor {

    IndexManagerColumnSelectionEditor() {
        super(new EditorDelegate());
    }

    private static final class EditorDelegate extends JComboBox {

        @SuppressWarnings("unchecked")
        EditorDelegate() {
            super(new Object[]{ None, Ascending, Descending });
            setRenderer(new IndexManagerColumnSelectionRenderer());

        }

    }

}
