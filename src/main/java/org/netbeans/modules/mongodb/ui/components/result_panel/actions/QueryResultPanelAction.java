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
package org.netbeans.modules.mongodb.ui.components.result_panel.actions;

import javax.swing.AbstractAction;
import static javax.swing.Action.SHORT_DESCRIPTION;
import javax.swing.Icon;
import lombok.Getter;
import org.netbeans.modules.mongodb.ui.components.CollectionResultPanel;

/**
 *
 * @author Yann D'Isanto
 */
public abstract class QueryResultPanelAction extends AbstractAction {
    
    private static final long serialVersionUID = 1L;

    @Getter
    private final CollectionResultPanel resultPanel;

    public QueryResultPanelAction(CollectionResultPanel resultPanel, String name) {
        this(resultPanel, name, null, null);
    }
    
    public QueryResultPanelAction(CollectionResultPanel resultPanel, String name, Icon icon) {
        this(resultPanel, name, icon, null);
    }

    public QueryResultPanelAction(CollectionResultPanel resultPanel, String name, Icon icon, String shortDescription) {
        super(name, icon);
        putValue(SHORT_DESCRIPTION, shortDescription);
        this.resultPanel = resultPanel;
    }

}
