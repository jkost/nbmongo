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
package org.netbeans.modules.mongodb.ui.explorer;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import org.openide.nodes.Node.Property;
import org.openide.util.NbBundle;

/**
 *
 * @author Yann D'Isanto
 */
class LocalizedProperties {

    private final ResourceBundle bundle;

    private final String prefix;

    private final List<Property<?>> properties = new ArrayList<>();

    public LocalizedProperties(Class<?> bundle) {
        this(bundle, bundle.getSimpleName());
    }

    public LocalizedProperties(Class<?> bundle, String prefix) {
        this.bundle = NbBundle.getBundle(bundle);
        this.prefix = prefix;
    }

    public Property[] toArray() {
        return properties.toArray(new Property[properties.size()]);
    }

    public LocalizedProperties booleanProperty(String propertyName, boolean value) {
        return localizedProperty(propertyName, Boolean.class, value);
    }

    public LocalizedProperties intProperty(String propertyName, int value) {
        return localizedProperty(propertyName, Integer.class, value);
    }

    public LocalizedProperties stringProperty(String propertyName, String value) {
        return localizedProperty(propertyName, String.class, value);
    }

    public <T> LocalizedProperties objectProperty(String propertyName, Class<T> propertyType, T value) {
        return localizedProperty(propertyName, propertyType, value);
    }

    public LocalizedProperties objectStringProperty(String propertyName, Object value) {
        return localizedProperty(propertyName, String.class, String.valueOf(value));
    }

    private <T> LocalizedProperties localizedProperty(String propertyName, Class<T> propertyType, T value) {
        properties.add(new LocalizedProperty<>(bundle, prefix, propertyName, propertyType, value));
        return this;
    }

}
