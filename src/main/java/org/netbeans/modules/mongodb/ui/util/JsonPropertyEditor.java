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

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.beans.PropertyEditorManager;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JLabel;
import javax.swing.JPanel;
import lombok.AllArgsConstructor;
import lombok.Getter;
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

    private static final Map<Class<?>, ValueBean<?>> BEANS = new HashMap<>();

    static {
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
            PropertyPanel valuePanel = new PropertyPanel(new PropertySupport.Reflection(valueBean, valueBean.getValueType(), "value"));
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
            if (DialogDisplayer.getDefault().notify(desc) == NotifyDescriptor.OK_OPTION) {
                Object newValue = valueBean.getValue();
                if(newValue instanceof BigDecimal) {
                    BigDecimal numberValue = (BigDecimal) newValue;
                    if(numberValue.signum() == 0 || numberValue.scale() <= 0 || numberValue.stripTrailingZeros().scale() <= 0) {
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

    public static Object show(String name, Object value) {
        ValueBean<Object> bean = (ValueBean<Object>) BEANS.get(value.getClass());
        if (value instanceof Number) {
            value = numberToBigDecimal((Number) value);
        }
        bean.setValue(value);
        try {
            PropertyPanel propertyPanel = new PropertyPanel(new PropertySupport.Reflection(bean, bean.getValueType(), "value"));
            JPanel panel = new JPanel(new BorderLayout(5, 0));
            panel.add(new JLabel(name), BorderLayout.WEST);
            panel.add(propertyPanel, BorderLayout.CENTER);
            final DialogDescriptor desc = new DialogDescriptor(panel, "edit value");
            if (DialogDisplayer.getDefault().notify(desc) == NotifyDescriptor.OK_OPTION) {
                Object newValue = bean.getValue();
                if(newValue instanceof BigDecimal) {
                    BigDecimal numberValue = (BigDecimal) newValue;
                    if(numberValue.signum() == 0 || numberValue.scale() <= 0 || numberValue.stripTrailingZeros().scale() <= 0) {
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
