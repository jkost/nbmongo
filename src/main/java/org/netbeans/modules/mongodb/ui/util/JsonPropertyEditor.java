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

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.beans.PropertyEditorManager;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bson.types.ObjectId;
import org.netbeans.modules.mongodb.util.JsonProperty;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.explorer.propertysheet.PropertyPanel;
import org.openide.nodes.PropertySupport;

/**
 *
 * @author Yann D'Isanto
 */
public class JsonPropertyEditor {

    public static boolean isQuickEditableJsonValue(Object value) {
        return !(value instanceof Map || value instanceof List || value instanceof ObjectId);
    }

    private static final Map<Class<?>, ValueBean<?>> BEANS = new HashMap<>();

    static {
        PropertyEditorManager.registerEditor(Boolean.class, BooleanPropertyEditor.class);
        PropertyEditorManager.registerEditor(Boolean.TYPE, BooleanPropertyEditor.class);
        PropertyEditorManager.registerEditor(BigDecimal.class, BigDecimalPropertyEditor.class);
        BEANS.put(Boolean.class, new BooleanBean());
        BEANS.put(String.class, new StringBean());
        BEANS.put(Number.class, new NumberBean());
        BEANS.put(BigDecimal.class, new NumberBean());
        BEANS.put(BigInteger.class, new NumberBean());
        BEANS.put(Double.class, new NumberBean());
        BEANS.put(Float.class, new NumberBean());
        BEANS.put(Long.class, new NumberBean());
        BEANS.put(Integer.class, new NumberBean());
    }

    @SuppressWarnings("unchecked")
    public static JsonProperty show(JsonProperty property) {
        Object value = property.getValue();
        StringBean nameBean = new StringBean(property.getName());
        ValueBean<Object> valueBean = (ValueBean<Object>) BEANS.get(value.getClass());
        if (value instanceof Number) {
            value = numberToBigDecimal((Number) value);
        }
        valueBean.setValue(value);
        try {
            PropertyPanel namePanel = new PropertyPanel(new PropertySupport.Reflection(nameBean, String.class, "value"));
            final PropertyPanel valuePanel = new PropertyPanel(new PropertySupport.Reflection(valueBean, valueBean.getValueType(), "value"));
            JPanel panel = new JPanel(new GridBagLayout());
            panel.add(new JLabel("Name"), new GridBagConstraints(
                0, 0, 1, 1, 1.0, 1.0,
                GridBagConstraints.WEST,
                GridBagConstraints.NONE,
                new Insets(5, 5, 5, 5),
                0, 0)
            );
            panel.add(namePanel, new GridBagConstraints(
                1, 0, 1, 1, 10.0, 1.0,
                GridBagConstraints.WEST,
                GridBagConstraints.HORIZONTAL,
                new Insets(5, 0, 5, 5),
                0, 0)
            );
            panel.add(new JLabel("Value"), new GridBagConstraints(
                0, 1, 1, 1, 1.0, 1.0,
                GridBagConstraints.WEST,
                GridBagConstraints.NONE,
                new Insets(0, 5, 5, 5),
                0, 0)
            );
            panel.add(valuePanel, new GridBagConstraints(
                1, 1, 1, 1, 10.0, 1.0,
                GridBagConstraints.WEST,
                GridBagConstraints.HORIZONTAL,
                new Insets(0, 0, 5, 5),
                0, 0)
            );
            final DialogDescriptor desc = new DialogDescriptor(panel, "edit property");
            SwingUtilities.invokeLater(new Runnable() {

                @Override
                public void run() {
                    valuePanel.requestFocus();
                    valuePanel.transferFocus();
                }
            });
            if (DialogDisplayer.getDefault().notify(desc) == NotifyDescriptor.OK_OPTION) {
                Object newValue = valueBean.getValue();
                if (newValue instanceof BigDecimal) {
                    BigDecimal numberValue = (BigDecimal) newValue;
                    if (numberValue.signum() == 0 || numberValue.scale() <= 0 || numberValue.stripTrailingZeros().scale() <= 0) {
                        newValue = numberValue.intValue();
                    } else {
                        newValue = numberValue.doubleValue();
                    }
                }
                return new JsonProperty(nameBean.getValue(), newValue);
            }
        } catch (NoSuchMethodException ex) {
            throw new AssertionError();
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public static Object show(String name, Object value) {
        ValueBean<Object> bean = (ValueBean<Object>) BEANS.get(value.getClass());
        if (value instanceof Number) {
            value = numberToBigDecimal((Number) value);
        }
        bean.setValue(value);
        try {
            PropertyPanel propertyPanel = new PropertyPanel(new PropertySupport.Reflection(bean, bean.getValueType(), "value"));
            JPanel panel = new JPanel(new GridBagLayout());
            panel.add(new JLabel(name), new GridBagConstraints(
                0, 0, 1, 1, 1.0, 1.0,
                GridBagConstraints.WEST,
                GridBagConstraints.NONE,
                new Insets(5, 5, 5, 5),
                0, 0)
            );
            panel.add(propertyPanel, new GridBagConstraints(
                1, 0, 1, 1, 10.0, 1.0,
                GridBagConstraints.WEST,
                GridBagConstraints.HORIZONTAL,
                new Insets(5, 0, 5, 5),
                0, 0)
            );
            final DialogDescriptor desc = new DialogDescriptor(panel, "edit value");
            if (DialogDisplayer.getDefault().notify(desc) == NotifyDescriptor.OK_OPTION) {
                Object newValue = bean.getValue();
                if (newValue instanceof BigDecimal) {
                    BigDecimal numberValue = (BigDecimal) newValue;
                    if (numberValue.signum() == 0 || numberValue.scale() <= 0 || numberValue.stripTrailingZeros().scale() <= 0) {
                        newValue = numberValue.intValue();
                    } else {
                        newValue = numberValue.doubleValue();
                    }
                }
                return newValue;
            }
        } catch (NoSuchMethodException ex) {
            throw new AssertionError();
        }
        return null;
    }

    private static BigDecimal numberToBigDecimal(Number number) {
        if (number instanceof Integer) {
            return new BigDecimal(number.intValue());
        }
        if (number instanceof Long) {
            return new BigDecimal(number.longValue());
        }
        if (number instanceof Double) {
            return new BigDecimal(number.doubleValue());
        }
        if (number instanceof Float) {
            return new BigDecimal(number.floatValue());
        }
        if (number instanceof Byte) {
            return new BigDecimal(number.byteValue());
        }
        if (number instanceof Short) {
            return new BigDecimal(number.shortValue());
        }
        return new BigDecimal(number.doubleValue());
    }

    @AllArgsConstructor
    public static abstract class ValueBean<T> {

        @Getter
        protected T value;

        @Getter
        private final Class<T> valueType;

        public abstract void setValue(T value);

    }

    public static class BooleanBean extends ValueBean<Boolean> {

        public BooleanBean() {
            this(false);
        }

        public BooleanBean(boolean value) {
            super(value, Boolean.class);
        }

        @Override
        public void setValue(Boolean value) {
            this.value = value;
        }

    }

    public static class StringBean extends ValueBean<String> {

        public StringBean() {
            this("");
        }

        public StringBean(String value) {
            super(value, String.class);
        }

        @Override
        public void setValue(String value) {
            this.value = value;
        }

    }

    public static class NumberBean extends ValueBean<BigDecimal> {

        public NumberBean() {
            this(BigDecimal.ZERO);
        }

        public NumberBean(BigDecimal value) {
            super(value, BigDecimal.class);
        }

        @Override
        public void setValue(BigDecimal value) {
            this.value = value;
        }

    }
}
