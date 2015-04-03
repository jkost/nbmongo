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
package org.netbeans.modules.mongodb.ui.windows.queryresultpanel.actions;

import java.awt.event.ActionEvent;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import org.netbeans.modules.mongodb.resources.Images;
import org.netbeans.modules.mongodb.ui.windows.QueryResultPanel;
import org.netbeans.modules.mongodb.ui.windows.QueryResultPanel.ResultView;
import org.openide.util.NbBundle.Messages;

/**
 *
 * @author Yann D'Isanto
 */
@Messages({
    "ACTION_displayResultsAsTreeTable=Display results in tree table",
    "ACTION_displayResultsAsTreeTable_tooltip=Display results in tree table",
    "ACTION_displayResultsAsFlatTable=Display results in flat table",
    "ACTION_displayResultsAsFlatTable_tooltip=Display results in flat table"
})
public final class ChangeResultViewAction extends QueryResultPanelAction {
    
    private static final long serialVersionUID = 1L;

    private final ResultView resultView;

    private ChangeResultViewAction(QueryResultPanel resultPanel, ResultView resultView, String name, Icon icon, String shortDescription) {
        super(resultPanel, name, icon, shortDescription);
        this.resultView = resultView;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        getResultPanel().changeResultView(resultView);
    }

    public static ChangeResultViewAction create(QueryResultPanel resultPanel, ResultView resultView) {
        switch (resultView) {
            case FLAT_TABLE:
                return new ChangeResultViewAction(resultPanel, resultView, 
                    Bundle.ACTION_displayResultsAsFlatTable(), 
                    new ImageIcon(Images.FLAT_TABLE_VIEW_ICON), 
                    Bundle.ACTION_displayResultsAsFlatTable_tooltip());
            case TREE_TABLE:
                return new ChangeResultViewAction(resultPanel, resultView, 
                    Bundle.ACTION_displayResultsAsTreeTable(), 
                    new ImageIcon(Images.TREE_TABLE_VIEW_ICON), 
                    Bundle.ACTION_displayResultsAsTreeTable_tooltip());
            default:
                throw new AssertionError();
        }
    }
}
