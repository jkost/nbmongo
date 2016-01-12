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
import java.util.EnumMap;
import java.util.Map;
import java.util.prefs.Preferences;
import javax.swing.JLabel;
import javax.swing.UIManager;
import lombok.Builder;
import lombok.Setter;
import lombok.Value;
import org.bson.BsonType;
import org.openide.util.NbPreferences;

/**
 *
 * @author Yann D'Isanto
 */
public interface RenderingOptions {

    RenderingOptionsItem fallback();
    
    RenderingOptionsItem comment();

    RenderingOptionsItem key();

    RenderingOptionsItem documentRoot();

    RenderingOptionsItem documentId();

    RenderingOptionsItem get(BsonType bsonType);
    
    RenderingOptionsItem get(BsonType bsonType, RenderingOptionsItem fallback);

    static enum PrefsRenderingOptions implements RenderingOptions {

        INSTANCE;

        @Setter
        private RenderingOptionsItem fallback;

        @Setter
        private RenderingOptionsItem comment;

        @Setter
        private RenderingOptionsItem key;

        @Setter
        private RenderingOptionsItem documentRoot;

        @Setter
        private RenderingOptionsItem documentId;

        private final Map<BsonType, RenderingOptionsItem> bsonOptions = new EnumMap<>(BsonType.class);

        private PrefsRenderingOptions() {
            load();
        }

        @Override
        public RenderingOptionsItem fallback() {
            return fallback != null ? fallback : DEFAULT.fallback();
        }

        @Override
        public RenderingOptionsItem comment() {
            return comment != null ? comment : DEFAULT.comment();
        }

        @Override
        public RenderingOptionsItem key() {
            return key != null ? key : DEFAULT.key();
        }

        @Override
        public RenderingOptionsItem documentRoot() {
            return documentRoot != null ? documentRoot : DEFAULT.documentRoot();
        }

        @Override
        public RenderingOptionsItem documentId() {
            return documentId != null ? documentId : DEFAULT.documentId();
        }

        @Override
        public RenderingOptionsItem get(BsonType bsonType) {
            return get(bsonType, fallback);
        }

        @Override
        public RenderingOptionsItem get(BsonType bsonType, RenderingOptionsItem fallback) {
            RenderingOptionsItem bro = bsonOptions.get(bsonType);
            return bro != null ? bro : DEFAULT.get(bsonType, fallback);
        }

        public void set(BsonType type, RenderingOptionsItem options) {
            bsonOptions.put(type, options);
        }

    private static Preferences prefs() {
        return NbPreferences.forModule(RenderingOptions.class).node("ui").node("bson-rendering");
    }

    public void load() {
        final Preferences prefs = prefs();
        final StringBuilder sb = new StringBuilder();
        for (BsonType bsonType : BsonType.values()) {
            sb.setLength(0);
            sb.append("BsonType.").append(bsonType.name());
            final String optionsKey = sb.toString();
            bsonOptions.put(bsonType, loadOptions(prefs, optionsKey, DEFAULT.get(bsonType)));
        }
        comment = loadOptions(prefs, "comment", DEFAULT.comment());
        key = loadOptions(prefs, "key", DEFAULT.key());
        documentRoot = loadOptions(prefs, "documentRoot", DEFAULT.documentRoot());
        documentId = loadOptions(prefs, "documentId", DEFAULT.documentId());
    }

    private RenderingOptionsItem loadOptions(Preferences prefs, String key, RenderingOptionsItem defaultOptions) {
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
        return new RenderingOptionsItem(font, foreground, background);
    }

    public void store() {
        final Preferences prefs = prefs();
        final StringBuilder sb = new StringBuilder();
        for (BsonType bsonType : BsonType.values()) {
            sb.setLength(0);
            sb.append("BsonType.").append(bsonType.name());
            final String optionsKey = sb.toString();
            storeOptions(prefs, optionsKey, get(bsonType));
        }
        storeOptions(prefs, "comment", comment());
        storeOptions(prefs, "key", key());
        storeOptions(prefs, "documentRoot", documentRoot());
        storeOptions(prefs, "documentId", documentId());
    }

    private void storeOptions(Preferences prefs, String key, RenderingOptionsItem options) {
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

    }

    static RenderingOptions DEFAULT = new RenderingOptions() {

        private final Map<BsonType, RenderingOptionsItem> bsonOptions = new EnumMap<>(BsonType.class);
        private final RenderingOptionsItem fallback;
        private final RenderingOptionsItem comment;
        private final RenderingOptionsItem key;
        private final RenderingOptionsItem documentRoot;
        private final RenderingOptionsItem documentId;

        {
            final Font font = new JLabel().getFont();
            final Color textForeground = UIManager.getColor("Tree.textForeground");
            final Color textBackground = UIManager.getColor("Tree.textBackground");
            final Color brown = new Color(0xCC3300);
            final Color purple = new Color(0x990099);
            fallback = new RenderingOptionsItem(font, textForeground, textBackground);
            comment = new RenderingOptionsItem(
                    font.deriveFont(Font.ITALIC),
                    new Color(0xA4A4A4),
                    textBackground);
            key = new RenderingOptionsItem(
                    font.deriveFont(Font.BOLD),
                    textForeground,
                    textBackground);
            documentRoot = new RenderingOptionsItem(
                    font,
                    textForeground,
                    new Color(0xF5ECCE));
            documentId = new RenderingOptionsItem(
                    font,
                    Color.LIGHT_GRAY,
                    textBackground);
            bsonOptions.put(BsonType.ARRAY, comment);
            bsonOptions.put(BsonType.DOCUMENT, comment);
            bsonOptions.put(BsonType.OBJECT_ID, documentId);
            bsonOptions.put(BsonType.STRING, new RenderingOptionsItem(
                    font,
                    Color.BLUE,
                    textBackground));
            bsonOptions.put(BsonType.INT32, new RenderingOptionsItem(
                    font,
                    Color.RED,
                    textBackground));
            bsonOptions.put(BsonType.DOUBLE, new RenderingOptionsItem(
                    font,
                    brown,
                    textBackground));
            bsonOptions.put(BsonType.INT64, new RenderingOptionsItem(
                    font,
                    Color.RED,
                    textBackground));
            bsonOptions.put(BsonType.BOOLEAN, new RenderingOptionsItem(
                    font,
                    purple,
                    textBackground));
            bsonOptions.put(BsonType.NULL, new RenderingOptionsItem(
                    font,
                    purple,
                    textBackground));
            bsonOptions.put(BsonType.UNDEFINED, new RenderingOptionsItem(
                    font,
                    purple,
                    textBackground));
        }

        @Override
        public RenderingOptionsItem fallback() {
            return fallback;
        }

        @Override
        public RenderingOptionsItem comment() {
            return comment;
        }

        @Override
        public RenderingOptionsItem key() {
            return key;
        }

        @Override
        public RenderingOptionsItem documentRoot() {
            return documentRoot;
        }

        @Override
        public RenderingOptionsItem documentId() {
            return documentId;
        }

        @Override
        public RenderingOptionsItem get(BsonType bsonType) {
            return get(bsonType, fallback);
        }

        @Override
        public RenderingOptionsItem get(BsonType bsonType, RenderingOptionsItem fallback) {
            RenderingOptionsItem bro = bsonOptions.get(bsonType);
            return bro != null ? bro : fallback;
        }

    };

    @Value
    @Builder
    static class RenderingOptionsItem {

        Font font;

        Color foreground;

        Color background;

        public RenderingOptionsItemBuilder asBuilder() {
            return RenderingOptionsItem.builder().font(font).background(background).foreground(foreground);
        }

    }

}

