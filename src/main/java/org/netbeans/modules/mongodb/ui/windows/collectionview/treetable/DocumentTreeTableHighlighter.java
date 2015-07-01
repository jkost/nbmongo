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
package org.netbeans.modules.mongodb.ui.windows.collectionview.treetable;

import java.awt.Color;
import java.awt.Component;
import org.bson.Document;
import org.jdesktop.swingx.decorator.AbstractHighlighter;
import org.jdesktop.swingx.decorator.ComponentAdapter;
import org.jdesktop.swingx.decorator.HighlightPredicate;
import org.netbeans.modules.mongodb.options.JsonCellRenderingOptions;
import org.netbeans.modules.mongodb.options.LabelCategory;

/**
 * Highlighter for document node row.
 * 
 * @author Yann D'Isanto
 */
public final class DocumentTreeTableHighlighter extends AbstractHighlighter {

    public DocumentTreeTableHighlighter() {
        super(new HighlightPredicate() {

            @Override
            public boolean isHighlighted(Component renderer, ComponentAdapter adapter) {
                final Object value = adapter.getValue();
                if (value instanceof Document) {
                    final Document document = (Document) value;
                    return document.get("_id") != null;
                }
                return false;
            }
        });
    }

    @Override
    protected Component doHighlight(Component component, ComponentAdapter adapter) {
        final Color background = JsonCellRenderingOptions.INSTANCE
            .getLabelFontConf(LabelCategory.DOCUMENT)
            .getBackground();
        component.setBackground(background);
        return component;
    }

}
