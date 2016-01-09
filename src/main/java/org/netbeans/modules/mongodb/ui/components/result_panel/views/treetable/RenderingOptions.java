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
package org.netbeans.modules.mongodb.ui.components.result_panel.views.treetable;

import java.awt.Color;
import java.awt.Font;
import java.beans.PropertyEditor;
import java.beans.PropertyEditorManager;
import java.util.EnumMap;
import java.util.Map;
import java.util.prefs.Preferences;
import javax.swing.JLabel;
import javax.swing.UIManager;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.bson.BsonType;
import org.openide.util.NbPreferences;

/**
 *
 * @author Yann D'Isanto
 */
@AllArgsConstructor
@Getter
@Setter
@Builder
public final class RenderingOptions {

    private final Font font;

    private final Color foreground;

    private final Color background;

    private static final Map<BsonType, RenderingOptions> options = new EnumMap<>(BsonType.class);

    private static RenderingOptions comments;

    private static RenderingOptions keys;

    private static RenderingOptions documentsRoot;

    public static RenderingOptions comments() {
        return comments != null ? comments : Default.comments;
    }

    public static RenderingOptions keys() {
        return keys != null ? keys : Default.keys;
    }

    public static RenderingOptions documentsRoot() {
        return documentsRoot != null ? documentsRoot : Default.documentsRoot;
    }

    public static RenderingOptions get(BsonType type) {
        RenderingOptions bro = options.get(type);
        return bro != null ? bro : Default.get(type);
    }

    private static Preferences prefs() {
        return NbPreferences.forModule(RenderingOptions.class).node("ui").node("bson-rendering");
    }

    public static void load() {
        final Preferences prefs = prefs();
        final StringBuilder sb = new StringBuilder();
        for (BsonType bsonType : BsonType.values()) {
            sb.setLength(0);
            sb.append("BsonType.").append(bsonType.name());
            final String optionsKey = sb.toString();
            final RenderingOptions defaultOptions = get(bsonType);
            options.put(bsonType, loadOptions(prefs, optionsKey, defaultOptions));
        }
        comments = loadOptions(prefs, "comments", comments());
        keys = loadOptions(prefs, "keys", keys());
        documentsRoot = loadOptions(prefs, "documentsRoot", keys());
    }

    private static RenderingOptions loadOptions(Preferences prefs, String key, RenderingOptions defaultOptions) {
        StringBuilder sb = new StringBuilder(key);
        sb.append(".font");
        PropertyEditor fontEditor = fontEditor();
        fontEditor.setValue(defaultOptions.getFont());
        final String fontStr = prefs.get(sb.toString(), fontEditor.getAsText());
        fontEditor.setAsText(fontStr);
        final Font font = (Font) fontEditor.getValue();
        sb.setLength(key.length());
        sb.append(".foreground");
        final int foregroundRGB = prefs.getInt(sb.toString(), defaultOptions.getForeground().getRGB());
        final Color foreground = new Color(foregroundRGB);
        sb.setLength(key.length());
        sb.append(".background");
        final int backgroundRGB = prefs.getInt(sb.toString(), defaultOptions.getBackground().getRGB());
        final Color background = new Color(backgroundRGB);
        return new RenderingOptions(font, foreground, background);
    }

    public static void store() {
        final Preferences prefs = prefs();
        final StringBuilder sb = new StringBuilder();
        for (BsonType bsonType : BsonType.values()) {
            sb.setLength(0);
            sb.append("BsonType.").append(bsonType.name());
            final String optionsKey = sb.toString();
            storeOptions(prefs, optionsKey, get(bsonType));
        }
        storeOptions(prefs, "comments", comments());
        storeOptions(prefs, "keys", keys());
        storeOptions(prefs, "documentsRoot", documentsRoot());
    }

    private static void storeOptions(Preferences prefs, String key, RenderingOptions options) {
        PropertyEditor fontEditor = fontEditor();
        final StringBuilder sb = new StringBuilder(key);
        sb.append(".font");
        fontEditor.setValue(options.getFont());
        prefs.put(sb.toString(), fontEditor.getAsText());
        sb.setLength(key.length());
        sb.append(".foreground");
        prefs.putInt(sb.toString(), options.getForeground().getRGB());
        sb.setLength(key.length());
        sb.append(".background");
        prefs.putInt(sb.toString(), options.getBackground().getRGB());
    }

    private static PropertyEditor cachedFontEditor;

    private static PropertyEditor fontEditor() {
        if (cachedFontEditor == null) {
            cachedFontEditor = PropertyEditorManager.findEditor(Font.class);
        }
        return cachedFontEditor;
    }

    private static final class Default {

        static final Map<BsonType, RenderingOptions> options = new EnumMap<>(BsonType.class);
        static final RenderingOptions fallback;
        static final RenderingOptions comments;
        static final RenderingOptions keys;
        static final RenderingOptions documentsRoot;

        static {
            final Font font = new JLabel().getFont();
            final Color textForeground = UIManager.getColor("Tree.textForeground");
            final Color textBackground = UIManager.getColor("Tree.textBackground");
            final Color brown = new Color(0xCC3300);
            final Color purple = new Color(0x990099);
            fallback = new RenderingOptions(font, textForeground, textBackground);
            comments = new RenderingOptions(
                    font.deriveFont(Font.ITALIC),
                    new Color(0xA4A4A4),
                    textBackground);
            keys = new RenderingOptions(
                    font.deriveFont(Font.BOLD),
                    textForeground,
                    textBackground);
            documentsRoot = new RenderingOptions(
                    font,
                    textForeground,
                    new Color(0xF5ECCE));
            options.put(BsonType.ARRAY, comments);
            options.put(BsonType.DOCUMENT, comments);
            options.put(BsonType.OBJECT_ID, new RenderingOptions(
                    font,
                    Color.LIGHT_GRAY,
                    textBackground));
            options.put(BsonType.STRING, new RenderingOptions(
                    font,
                    Color.BLUE,
                    textBackground));
            options.put(BsonType.INT32, new RenderingOptions(
                    font,
                    Color.RED,
                    textBackground));
            options.put(BsonType.DOUBLE, new RenderingOptions(
                    font,
                    brown,
                    textBackground));
            options.put(BsonType.INT64, new RenderingOptions(
                    font,
                    Color.RED,
                    textBackground));
            options.put(BsonType.BOOLEAN, new RenderingOptions(
                    font,
                    purple,
                    textBackground));
            options.put(BsonType.NULL, new RenderingOptions(
                    font,
                    purple,
                    textBackground));
            options.put(BsonType.UNDEFINED, new RenderingOptions(
                    font,
                    purple,
                    textBackground));

//            map.put(LabelCategory.KEY, new LabelFontConf(
//                font.deriveFont(Font.BOLD),
//                textForeground,
//                textBackground));
        }

        static RenderingOptions get(BsonType type) {
            RenderingOptions bro = options.get(type);
            return bro != null ? bro : fallback;
        }
    }

}
