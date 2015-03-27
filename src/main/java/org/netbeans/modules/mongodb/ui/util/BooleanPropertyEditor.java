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

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyEditor;
import java.beans.PropertyEditorSupport;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.KeyStroke;
import lombok.Getter;
import lombok.Setter;
import org.openide.explorer.propertysheet.ExPropertyEditor;
import org.openide.explorer.propertysheet.InplaceEditor;
import static org.openide.explorer.propertysheet.InplaceEditor.COMMAND_SUCCESS;
import org.openide.explorer.propertysheet.PropertyEnv;
import org.openide.explorer.propertysheet.PropertyModel;

/**
 *
 * @author Yann D'Isanto
 */
public class BooleanPropertyEditor extends PropertyEditorSupport implements ExPropertyEditor, InplaceEditor.Factory {
    
    @Getter
    private final InplaceEditor inplaceEditor = new BooleanInplaceEditor();

    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        if (text.isEmpty()) {
            setValue(false);
        } else {
            setValue(Boolean.valueOf(text));
        }

    }

    @Override
    public void attachEnv(PropertyEnv env) {
        env.registerInplaceEditorFactory(this);
    }

    private class BooleanInplaceEditor implements InplaceEditor {

        private final Boolean[] items = { false, true };
        
        private final JComboBox<Boolean> field = new JComboBox<>(items);

        @Getter
        private PropertyEditor propertyEditor = null;

        @Getter
        @Setter
        private PropertyModel propertyModel;

        private final List<ActionListener> listeners = new ArrayList<>();
        
        public BooleanInplaceEditor() {
            field.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    ActionEvent evt = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, COMMAND_SUCCESS);
                    for (ActionListener listener : listeners) {
                        listener.actionPerformed(evt);
                    }
                }
            });
        }

        @Override
        public void connect(PropertyEditor propertyEditor, PropertyEnv env) {
            this.propertyEditor = propertyEditor;
            reset();
        }

        @Override
        public JComponent getComponent() {
            return field;
        }

        @Override
        public void clear() {
            propertyEditor = null;
            propertyModel = null;
        }

        @Override
        public Object getValue() {
            return field.getSelectedItem();
        }

        @Override
        public void setValue(Object value) {
            field.setSelectedItem(value);
        }

        @Override
        public boolean supportsTextEntry() {
            return false;
        }

        @Override
        public void reset() {
            Object value = propertyEditor.getValue();
            if (value != null) {
                setValue(value);
            }
        }

        @Override
        public void addActionListener(ActionListener listener) {
            listeners.add(listener);
        }

        @Override
        public void removeActionListener(ActionListener listener) {
            listeners.remove(listener);
        }

        @Override
        public KeyStroke[] getKeyStrokes() {
            return new KeyStroke[0];
        }

        @Override
        public boolean isKnownComponent(Component component) {
            return component == field || field.isAncestorOf(component);
        }
    }

}
