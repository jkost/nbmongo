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

import java.util.ResourceBundle;
import lombok.Getter;
import org.openide.nodes.PropertySupport;
import org.openide.util.NbBundle;

/**
 * Read-Only property that uses localized strings from the bundle file.
 */
class LocalizedProperty extends PropertySupport.ReadOnly<String> {

    private static final ResourceBundle bundle = NbBundle.getBundle(LocalizedProperty.class);

    @Getter
    private final String value;

    public LocalizedProperty(String prefix, String propertyName, String value) {
        super(
            bundle.getString(nameKey(prefix, propertyName)),
            String.class,
            bundle.getString(dislayNameKey(prefix, propertyName)),
            bundle.getString(shortDescriptionKey(prefix, propertyName))
        );
        this.value = value;
    }

    private static String nameKey(String prefix, String propertyName) {
        return new StringBuilder(prefix)
            .append('.')
            .append(propertyName)
            .append(".name")
            .toString();
    }

    private static String dislayNameKey(String prefix, String propertyName) {
        return new StringBuilder(prefix)
            .append('.')
            .append(propertyName)
            .append(".displayname")
            .toString();
    }

    private static String shortDescriptionKey(String prefix, String propertyName) {
        return new StringBuilder(prefix)
            .append('.')
            .append(propertyName)
            .append(".shortdesc")
            .toString();
    }
}
