/*
 * Copyright (C) 2016 Yann D'Isanto
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
package org.netbeans.modules.mongodb.ui.components.result_panel.views.text;

import java.awt.BorderLayout;
import java.util.List;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import lombok.Getter;
import lombok.Setter;
import org.bson.BsonDocument;
import org.netbeans.modules.mongodb.api.CollectionResultPages;
import org.netbeans.modules.mongodb.bson.Bsons;
import org.netbeans.modules.mongodb.ui.components.CollectionResultPanel;
import org.netbeans.modules.mongodb.util.BsonUtils;
import org.netbeans.modules.mongodb.ui.components.SearchableTextComponent;
import org.netbeans.modules.mongodb.ui.util.JsonUIUtils;

/**
 *
 * @author Yann D'Isanto
 */
public final class ResultsTextView extends JPanel implements CollectionResultPanel.View, CollectionResultPages.Listener, SearchableTextComponent.MessageDisplayer {

    private static final long serialVersionUID = 1L;
    
    private final JEditorPane textComponent;

    private final JLabel messageLabel = new JLabel();
    
    @Getter
    private final CollectionResultPages pages;

    @Getter
    @Setter
    private boolean sortDocumentsFields;

    public ResultsTextView(CollectionResultPages pages) {
        super(new BorderLayout(5, 5));
        textComponent = new JEditorPane();
        JsonUIUtils.setJsonEditorKit(textComponent);
        textComponent.setEditable(false);
        this.pages = pages;
        add(new SearchableTextComponent(textComponent, this), BorderLayout.CENTER);
        add(messageLabel, BorderLayout.SOUTH);
        pages.addListener(this);
        refreshTextComponent();
    }

    @Override
    public void pageChanged(CollectionResultPages source, int pageIndex, List<BsonDocument> page) {
        refreshTextComponent();
    }

    @Override
    public void pageObjectUpdated(int index, BsonDocument oldValue, BsonDocument newValue) {
        refreshTextComponent();
    }

    @Override
    public void infoMessage(String text) {
        messageLabel.setVisible(true);
        messageLabel.setText(text);
    }

    @Override
    public void clearMessage() {
        messageLabel.setVisible(false);
    }

    
    private void refreshTextComponent() {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                long index = (pages.getPageIndex() - 1) * pages.getPageSize();
                StringBuilder textBuilder = new StringBuilder();
                for (BsonDocument document : pages.getCurrentPageItems()) {
                    if (sortDocumentsFields) {
                        document = BsonUtils.sortDocumentFields(document);
                    }
                    textBuilder
                        .append("\n")
                        .append("/* ").append(index++).append(" */")
                        .append("\n")
                        .append(document.toJson(Bsons.SHELL_PRETTY))
                        .append("\n");
                }
                textComponent.setText(textBuilder.toString());
                textComponent.setCaretPosition(0);
            }
        });
    }
    
}
