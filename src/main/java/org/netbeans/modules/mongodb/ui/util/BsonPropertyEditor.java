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
import java.util.EnumMap;
import java.util.Map;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bson.BsonBoolean;
import org.bson.BsonDouble;
import org.bson.BsonInt32;
import org.bson.BsonInt64;
import org.bson.BsonNumber;
import org.bson.BsonString;
import org.bson.BsonType;
import org.bson.BsonValue;
import org.netbeans.modules.mongodb.util.BsonProperty;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.explorer.propertysheet.PropertyPanel;
import org.openide.nodes.PropertySupport;

/**
 *
 * @author Yann D'Isanto
 */
public class BsonPropertyEditor {

    public static boolean isQuickEditableBsonValue(BsonValue value) {
        switch (value.getBsonType()) {
            case BOOLEAN:
            case STRING:
            case INT32:
            case INT64:
            case DOUBLE:
                return true;
            default:
                return false;
        }
    }

    private static final Map<BsonType, ValueBean<?>> BEANS = new EnumMap<>(BsonType.class);

    static {
        PropertyEditorManager.registerEditor(Boolean.class, BooleanPropertyEditor.class);
        PropertyEditorManager.registerEditor(Boolean.TYPE, BooleanPropertyEditor.class);
        PropertyEditorManager.registerEditor(BigDecimal.class, BigDecimalPropertyEditor.class);
        BEANS.put(BsonType.BOOLEAN, new BooleanBean());
        BEANS.put(BsonType.STRING, new StringBean());
        BEANS.put(BsonType.DOUBLE, new NumberBean());
        BEANS.put(BsonType.INT32, new NumberBean());
        BEANS.put(BsonType.INT64, new NumberBean());
    }

    @SuppressWarnings("unchecked")
    public static BsonProperty show(BsonProperty property) {
        BsonValue value = property.getValue();
        StringBean nameBean = new StringBean(property.getName());
        ValueBean<Object> valueBean = (ValueBean<Object>) BEANS.get(value.getBsonType());
        valueBean.setBsonValue(value);
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
                return new BsonProperty(nameBean.getValue(), valueBean.getBsonValue());
            }
        } catch (NoSuchMethodException ex) {
            throw new AssertionError(ex);
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public static BsonValue show(String name, BsonValue value) {
        ValueBean<Object> bean = (ValueBean<Object>) BEANS.get(value.getBsonType());
        bean.setBsonValue(value);
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
                return bean.getBsonValue();
            }
        } catch (NoSuchMethodException ex) {
            throw new AssertionError();
        }
        return null;
    }

    @AllArgsConstructor
    public static abstract class ValueBean<T> {

        @Getter
        @Setter
        protected T value;

        @Getter
        private final Class<T> valueType;

        public abstract void setBsonValue(BsonValue value);

        public abstract BsonValue getBsonValue();
    }

    public static class BooleanBean extends ValueBean<Boolean> {

        public BooleanBean() {
            this(false);
        }

        public BooleanBean(boolean value) {
            super(value, Boolean.class);
        }

        @Override
        public void setBsonValue(BsonValue bson) {
            this.value = ((BsonBoolean) bson).getValue();
        }

        @Override
        public BsonValue getBsonValue() {
            return getValue() ? BsonBoolean.TRUE : BsonBoolean.FALSE;
        }

        @Override // generated by lombok in super class but mandatory because of reflection invokation
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
        public void setBsonValue(BsonValue bson) {
            this.value = ((BsonString) bson).getValue();
        }

        @Override
        public BsonValue getBsonValue() {
            return new BsonString(getValue());
        }

        @Override // generated by lombok in super class but mandatory because of reflection invokation
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
        public void setBsonValue(BsonValue bson) {
            BsonNumber number = (BsonNumber) bson;
            switch (number.getBsonType()) {
                case INT32:
                    this.value = new BigDecimal(number.intValue());
                    break;
                case INT64:
                    this.value = new BigDecimal(number.longValue());
                    break;
                case DOUBLE:
                    this.value = new BigDecimal(number.doubleValue());
                    break;
                default:
                    throw new AssertionError();
            }
        }

        @Override
        public BsonValue getBsonValue() {
            if (value.signum() == 0 || value.scale() <= 0 || value.stripTrailingZeros().scale() <= 0) {
                long lValue = value.longValue();
                int iValue = (int) lValue;
                if (iValue == lValue) {
                    return new BsonInt32(iValue);
                } else {
                    return new BsonInt64(lValue);
                }
            } else {
                return new BsonDouble(value.doubleValue());
            }
        }

        @Override // generated by lombok in super class but mandatory because of reflection invokation
        public void setValue(BigDecimal value) {
            this.value = value;
        }
    }
}
