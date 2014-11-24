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
