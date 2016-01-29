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
package org.netbeans.modules.mongodb.ui.components;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.JTextComponent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.openide.NotificationLineSupport;
import org.openide.awt.CloseButtonFactory;
import org.openide.util.NbBundle.Messages;

/**
 *
 * @author Yann D'Isanto
 */
@Messages({
    "HINT_search=search for text with Ctrl+F",
    "HINT_nextResult=hit ENTER to search next occurence",
    "HINT_noResultFound=no result found"
})
public final class SearchableTextComponent extends JPanel {

    private static final long serialVersionUID = 1L;

    public static final KeyStroke ESCAPE_KEYSTROKE = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);

    private static final KeyStroke SEARCH_KEYSTROKE = KeyStroke.getKeyStroke(KeyEvent.VK_F, KeyEvent.CTRL_DOWN_MASK);

    private final JTextComponent textComponent;
    private final JPanel searchPane = new JPanel(new BorderLayout());

    private final JTextField searchField = new JTextField();

    private int lastSearchIndex = 0;

    @Getter
    private MessageDisplayer messagesDisplayer;

    public SearchableTextComponent(final JTextComponent textComponent) {
        this(textComponent, MessageDisplayer.DUMMY);
    }

    public SearchableTextComponent(final JTextComponent textComponent, MessageDisplayer messagesDisplayer) {
        super(new BorderLayout(0, 2));
        this.textComponent = textComponent;
        this.messagesDisplayer = messagesDisplayer;
        initComponent();
    }

    private void initComponent() {
        final JScrollPane scrollPane = new JScrollPane(textComponent);
        scrollPane.setPreferredSize(new Dimension(450, 300));
        JButton closeSearchButton = CloseButtonFactory.createBigCloseButton();

        searchPane.add(searchField, BorderLayout.CENTER);
        searchPane.add(closeSearchButton, BorderLayout.EAST);
        final Action closeSearchAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                searchPane.setVisible(false);
                messagesDisplayer.clearMessage();
                textComponent.requestFocusInWindow();
            }
        };
        textComponent.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                messagesDisplayer.infoMessage(Bundle.HINT_search());
            }
        });
        closeSearchButton.addActionListener(closeSearchAction);
        searchField.getInputMap().put(ESCAPE_KEYSTROKE, "closeSearch");
        searchField.getActionMap().put("closeSearch", closeSearchAction);

        textComponent.getInputMap().put(SEARCH_KEYSTROKE, "search");
        textComponent.getActionMap().put("search", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selection = textComponent.getSelectedText();
                if (selection != null && selection.isEmpty() == false) {
                    searchField.setText(selection);
                }
                messagesDisplayer.infoMessage(Bundle.HINT_nextResult());
                searchPane.setVisible(true);
                searchField.selectAll();
                searchField.requestFocusInWindow();

            }
        });
        searchField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String search = searchField.getText();
                if (search.isEmpty()) {
                    return;
                }
                messagesDisplayer.clearMessage();
                int index = textComponent.getText().indexOf(search, lastSearchIndex);
                if (index == -1 && lastSearchIndex > 0) {
                    lastSearchIndex = 0;
                    index = textComponent.getText().indexOf(search);
                }
                if (index > -1) {
                    textComponent.setCaretPosition(index);
                    textComponent.setSelectionStart(index);
                    lastSearchIndex = index + search.length();
                    textComponent.setSelectionEnd(lastSearchIndex);
                    messagesDisplayer.infoMessage(Bundle.HINT_nextResult());
                } else {
                    messagesDisplayer.infoMessage(Bundle.HINT_noResultFound());
                    textComponent.setSelectionEnd(textComponent.getSelectionStart());
                }
            }
        });
        searchField.getDocument().addDocumentListener(new DocumentListener() {

            @Override
            public void insertUpdate(DocumentEvent e) {
                lastSearchIndex = 0;
                messagesDisplayer.infoMessage(Bundle.HINT_nextResult());
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                lastSearchIndex = 0;
                messagesDisplayer.infoMessage(Bundle.HINT_nextResult());
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
            }
        });
        add(scrollPane, BorderLayout.CENTER);
        add(searchPane, BorderLayout.SOUTH);
        searchPane.setVisible(false);

    }

    public void setMessagesDisplayer(MessageDisplayer messagesDisplayer) {
        this.messagesDisplayer = messagesDisplayer;
        messagesDisplayer.infoMessage(Bundle.HINT_search());
    }

    public static interface MessageDisplayer {

        void infoMessage(String text);

        void clearMessage();

        static MessageDisplayer DUMMY = new MessageDisplayer() {
            @Override
            public void infoMessage(String text) {
                System.out.println(text);
            }

            @Override
            public void clearMessage() {
            }
        };

        @AllArgsConstructor
        static final class NotificationLineSupportMessageDisplayer implements MessageDisplayer {

            private NotificationLineSupport notificationLineSupport;

            @Override
            public void infoMessage(String message) {
                notificationLineSupport.setInformationMessage(message);
            }

            @Override
            public void clearMessage() {
                notificationLineSupport.clearMessages();
            }
        }
    }

}
