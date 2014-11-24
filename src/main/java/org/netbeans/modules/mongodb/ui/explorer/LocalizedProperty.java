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
