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
package org.netbeans.modules.mongodb.ui.native_tools;

import java.util.Map;
import java.util.prefs.Preferences;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JTextField;
import org.netbeans.modules.mongodb.native_tools.MongoNativeTool;
import org.netbeans.modules.mongodb.ui.native_tools.NativeToolOptionsDialog.OptionsAndArgsPanel;
import org.openide.util.NbPreferences;

/**
 *
 * @author Yann D'Isanto
 */
public abstract class AbstractOptionsAndArgsPanel extends JPanel implements OptionsAndArgsPanel {

    private final MongoNativeTool nativeTool;

    public AbstractOptionsAndArgsPanel(MongoNativeTool nativeTool) {
        this.nativeTool = nativeTool;
    }

    @Override
    public final MongoNativeTool getNativeTool() {
        return nativeTool;
    }

    @Override
    public final JPanel getPanel() {
        return this;
    }

    protected final Preferences prefs() {
        return NbPreferences.forModule(MongoDumpOptionsPanel.class).node("native_tools");
    }

    protected final void readOptionFromUI(Map<String, String> options, String optionKey, JTextField textField) {
        final String value = textField.getText().trim();
        if (value.isEmpty() == false) {
            options.put(optionKey, value);
        }
    }

    protected final void readOptionFromUI(Map<String, String> options, String optionKey, JCheckBox checkbox) {
        if (checkbox.isSelected()) {
            options.put(optionKey, "");
        }
    }

    protected final void populateUIWithOption(Map<String, String> options, String optionKey, JTextField textField, String defaultValue) {
        final String optionValue = options.get(optionKey);
        textField.setText(optionValue != null ? optionValue : defaultValue);
    }

    protected final void populateUIWithOption(Map<String, String> options, String optionKey, JTextField textField) {
        AbstractOptionsAndArgsPanel.this.populateUIWithOption(options, optionKey, textField, "");
    }

    protected final void populateUIWithOption(Map<String, String> options, String optionKey, JCheckBox checkbox) {
        checkbox.setSelected(options.containsKey(optionKey));
    }
}
