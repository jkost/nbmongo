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
package org.netbeans.modules.mongodb.options;

import java.awt.Color;
import java.awt.Font;
import java.beans.PropertyEditor;
import java.beans.PropertyEditorManager;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.prefs.Preferences;
import javax.swing.JLabel;
import javax.swing.UIManager;
import org.openide.util.NbPreferences;

/**
 *
 * @author Yann D'Isanto
 */
public enum JsonCellRenderingOptions {

    INSTANCE;
    
    private final Map<LabelCategory, LabelFontConf> labelConfs = new HashMap<>();

    private JsonCellRenderingOptions() {
        load();
    }

    private Preferences getPreferences() {
        return NbPreferences.forModule(JsonCellRenderingOptions.class);
    }

    public LabelFontConf getLabelFontConf(LabelCategory category) {
        final LabelFontConf labelFontConf = labelConfs.get(category);
        return labelFontConf != null ? labelFontConf : Default.LABEL_CONFS.get(category);
    }

    public void setLabelFontConf(LabelCategory category, LabelFontConf labelFontConf) {
        labelConfs.put(category, labelFontConf);
    }

    public void load() {
        final Preferences prefs = getPreferences();
        final PropertyEditor fontEditor = PropertyEditorManager.findEditor(Font.class);
        final StringBuilder sb = new StringBuilder();
        for (LabelCategory category : LabelCategory.values()) {
            final LabelFontConf labelFontConf = getLabelFontConf(category);
            sb.setLength(0);
            sb.append("CATEGORY_").append(category.name());
            final String categoryKey = sb.toString();
            sb.append(".font");
            fontEditor.setValue(labelFontConf.getFont());
            final String fontStr = prefs.get(sb.toString(), fontEditor.getAsText());
            fontEditor.setAsText(fontStr);
            final Font font = (Font) fontEditor.getValue();
            sb.setLength(categoryKey.length());
            sb.append(".foreground");
            final int foregroundRGB = prefs.getInt(sb.toString(), labelFontConf.getForeground().getRGB());
            final Color foreground = new Color(foregroundRGB);
            sb.setLength(categoryKey.length());
            sb.append(".background");
            final int backgroundRGB = prefs.getInt(sb.toString(), labelFontConf.getBackground().getRGB());
            final Color background = new Color(backgroundRGB);
            labelConfs.put(category, new LabelFontConf(font, foreground, background));
        }
    }

    public void store() {
        final Preferences prefs = getPreferences();
        final PropertyEditor fontEditor = PropertyEditorManager.findEditor(Font.class);
        final StringBuilder sb = new StringBuilder();
        for (LabelCategory category : LabelCategory.values()) {
            final LabelFontConf labelFontConf = getLabelFontConf(category);
            sb.setLength(0);
            sb.append("CATEGORY_").append(category.name());
            final String categoryKey = sb.toString();
            sb.append(".font");
            fontEditor.setValue(labelFontConf.getFont());
            prefs.put(sb.toString(), fontEditor.getAsText());
            sb.setLength(categoryKey.length());
            sb.append(".foreground");
            prefs.putInt(sb.toString(), labelFontConf.getForeground().getRGB());
            sb.setLength(categoryKey.length());
            sb.append(".background");
            prefs.putInt(sb.toString(), labelFontConf.getBackground().getRGB());
        }
    }

    public static final class Default {

        public static final Map<LabelCategory, LabelFontConf> LABEL_CONFS;

        static {
            final Font font = new JLabel().getFont();
            final Color textForeground = UIManager.getColor("Tree.textForeground");
            final Color textBackground = UIManager.getColor("Tree.textBackground");
            final Color brown = new Color(0xCC3300);
            final Color purple = new Color(0x990099);
            final Map<LabelCategory, LabelFontConf> map = new HashMap<>();
            map.put(LabelCategory.KEY, new LabelFontConf(
                font.deriveFont(Font.BOLD),
                textForeground,
                textBackground));
            map.put(LabelCategory.DOCUMENT, new LabelFontConf(
                font,
                textForeground,
                new Color(0xF5ECCE)));
            map.put(LabelCategory.COMMENT, new LabelFontConf(
                font.deriveFont(Font.ITALIC),
                new Color(0xA4A4A4),
                textBackground));
            map.put(LabelCategory.ID, new LabelFontConf(
                font,
                Color.LIGHT_GRAY,
                textBackground));
            map.put(LabelCategory.STRING_VALUE, new LabelFontConf(
                font,
                Color.BLUE,
                textBackground));
            map.put(LabelCategory.INT_VALUE, new LabelFontConf(
                font,
                Color.RED,
                textBackground));
            map.put(LabelCategory.DECIMAL_VALUE, new LabelFontConf(
                font,
                brown,
                textBackground));
            map.put(LabelCategory.BOOLEAN_VALUE, new LabelFontConf(
                font,
                purple,
                textBackground));
            LABEL_CONFS = Collections.unmodifiableMap(map);
        }
    }
}
