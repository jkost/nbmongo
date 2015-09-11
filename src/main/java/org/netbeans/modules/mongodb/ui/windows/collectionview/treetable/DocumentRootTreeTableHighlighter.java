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
import org.jdesktop.swingx.JXTreeTable;
import org.jdesktop.swingx.decorator.AbstractHighlighter;
import org.jdesktop.swingx.decorator.ComponentAdapter;
import org.jdesktop.swingx.decorator.HighlightPredicate;

/**
 * Highlighter for document node row.
 *
 * @author Yann D'Isanto
 */
public final class DocumentRootTreeTableHighlighter extends AbstractHighlighter {

    public DocumentRootTreeTableHighlighter() {
        super(new HighlightPredicate() {

            @Override
            public boolean isHighlighted(Component renderer, ComponentAdapter adapter) {
                return adapter.getDepth() == 1;
            }
        });
    }

    @Override
    protected Component doHighlight(Component component, ComponentAdapter adapter) {
        JXTreeTable table = (JXTreeTable) adapter.getComponent();
        adapter.isSelected();
        Color background = adapter.isSelected()
                ? table.getSelectionBackground()
                : RenderingOptions.documentsRoot().getBackground();
        component.setBackground(background);
        return component;
    }

}
