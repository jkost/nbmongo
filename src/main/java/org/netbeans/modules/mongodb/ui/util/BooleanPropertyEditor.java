/*
 * The MIT License
 *
 * Copyright 2014 Yann D'Isanto.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
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
