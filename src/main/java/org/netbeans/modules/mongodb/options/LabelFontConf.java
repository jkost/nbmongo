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

/**
 *
 * @author Yann D'Isanto
 */
public final class LabelFontConf implements Cloneable {

    private final Font font;

    private final Color foreground;

    private final Color background;

    public static final class Builder {

        private Font font;

        private Color foreground;

        private Color background;

        public Builder() {
        }

        public Builder(LabelFontConf defaultConf) {
            this.font = defaultConf.font;
            this.foreground = defaultConf.foreground;
            this.background = defaultConf.background;
        }

        public Builder font(Font font) {
            this.font = font;
            return this;
        }

        public Builder foreground(Color foreground) {
            this.foreground = foreground;
            return this;
        }

        public Builder background(Color background) {
            this.background = background;
            return this;
        }

        public Font getFont() {
            return font;
        }

        public Color getForeground() {
            return foreground;
        }

        public Color getBackground() {
            return background;
        }

        public LabelFontConf build() {
            return new LabelFontConf(this);
        }
    }

    public LabelFontConf(Font font, Color foreground, Color background) {
        this.font = font;
        this.foreground = foreground;
        this.background = background;
    }

    private LabelFontConf(Builder builder) {
        this.font = builder.font;
        this.foreground = builder.foreground;
        this.background = builder.background;
    }

    public Font getFont() {
        return font;
    }

    public Color getForeground() {
        return foreground;
    }

    public Color getBackground() {
        return background;
    }

    @Override
    public LabelFontConf clone() {
        try {
            return (LabelFontConf) super.clone();
        } catch (CloneNotSupportedException ex) {
            throw new AssertionError();
        }
    }
}
