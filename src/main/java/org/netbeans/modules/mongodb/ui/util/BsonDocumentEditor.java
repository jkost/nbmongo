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
package org.netbeans.modules.mongodb.ui.util;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.EditorKit;
import lombok.Getter;
import org.bson.BsonDocument;
import org.bson.json.JsonParseException;
import org.netbeans.api.editor.mimelookup.MimeLookup;
import org.netbeans.modules.mongodb.bson.Bsons;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.NotificationLineSupport;
import org.openide.NotifyDescriptor;
import org.openide.awt.CloseButtonFactory;
import org.openide.util.NbBundle.Messages;

/**
 *
 * @author Yann D'Isanto
 */
@Messages({
    "invalidJson=invalid json",
    "HINT_search=search your document with Ctrl+F",
    "HINT_nextResult=hit ENTER to search next occurence",
    "HINT_noResultFound=no result found"
})
public class BsonDocumentEditor extends JPanel {

    private static final long serialVersionUID = 1L;

    private static final KeyStroke ESCAPE_KEYSTROKE = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);

    private static final KeyStroke SEARCH_KEYSTROKE = KeyStroke.getKeyStroke(KeyEvent.VK_F, KeyEvent.CTRL_DOWN_MASK);

    private final JEditorPane editor = new JEditorPane();

    private final JPanel searchPane;

    private final JTextField searchField = new JTextField();

    private int lastSearchIndex = 0;

    @Getter
    private NotificationLineSupport notificationLineSupport;

    public BsonDocumentEditor() {
        this(true);
    }

    public BsonDocumentEditor(boolean searchPanel) {
        super(new BorderLayout());
        EditorKit editorKit = MimeLookup.getLookup("text/x-json").lookup(EditorKit.class);
        if (editorKit != null) {
            editor.setEditorKit(editorKit);
        }
        final JScrollPane scrollPane = new JScrollPane(editor);
        scrollPane.setPreferredSize(new Dimension(450, 300));
        add(scrollPane, BorderLayout.CENTER);

        if (searchPanel) {
            searchPane = new JPanel(new BorderLayout());
            JButton closeSearchButton = CloseButtonFactory.createBigCloseButton();

            searchPane.add(searchField, BorderLayout.CENTER);
            searchPane.add(closeSearchButton, BorderLayout.EAST);
            final Action closeSearchAction = new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    searchPane.setVisible(false);
                    notificationLineSupport.clearMessages();
                    notificationLineSupport.setInformationMessage(Bundle.HINT_search());
                    editor.requestFocusInWindow();
                }
            };
            closeSearchButton.addActionListener(closeSearchAction);
            searchField.getInputMap().put(ESCAPE_KEYSTROKE, "closeSearch");
            searchField.getActionMap().put("closeSearch", closeSearchAction);

            editor.getInputMap().put(SEARCH_KEYSTROKE, "search");
            editor.getActionMap().put("search", new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String selection = editor.getSelectedText();
                    if (selection != null && selection.isEmpty() == false) {
                        searchField.setText(selection);
                    }
                    notificationLineSupport.clearMessages();
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
                    notificationLineSupport.clearMessages();
                    int index = editor.getText().indexOf(search, lastSearchIndex);
                    if (index == -1 && lastSearchIndex > 0) {
                        lastSearchIndex = 0;
                        index = editor.getText().indexOf(search);
                    }
                    if (index > -1) {
                        editor.setCaretPosition(index);
                        editor.setSelectionStart(index);
                        lastSearchIndex = index + search.length();
                        editor.setSelectionEnd(lastSearchIndex);
                        notificationLineSupport.setInformationMessage(Bundle.HINT_nextResult());
                    } else {
                        notificationLineSupport.setInformationMessage(Bundle.HINT_noResultFound());
                        editor.setSelectionEnd(editor.getSelectionStart());
                    }
                }
            });
            searchField.getDocument().addDocumentListener(new DocumentListener() {

                @Override
                public void insertUpdate(DocumentEvent e) {
                    lastSearchIndex = 0;
                    notificationLineSupport.setInformationMessage(Bundle.HINT_nextResult());
                }

                @Override
                public void removeUpdate(DocumentEvent e) {
                    lastSearchIndex = 0;
                    notificationLineSupport.setInformationMessage(Bundle.HINT_nextResult());
                }

                @Override
                public void changedUpdate(DocumentEvent e) {
                }
            });
            add(searchPane, BorderLayout.SOUTH);
            searchPane.setVisible(false);
        } else {
            searchPane = null;
        }
    }

    public void setNotificationLineSupport(NotificationLineSupport notificationLineSupport) {
        this.notificationLineSupport = notificationLineSupport;
        notificationLineSupport.clearMessages();
        if (searchPane != null && searchPane.isVisible() == false) {
            notificationLineSupport.setInformationMessage(Bundle.HINT_search());
        }
    }

    public String getJson() {
        return editor.getText().trim();
    }

    private void setJson(String json) {
        editor.setText(json);
        editor.setCaretPosition(0);
    }

    public boolean isValidContent() {
        try {
            BsonDocument.parse(getJson());
            return true;
        } catch (JsonParseException ex) {
            return false;
        }
    }

    public BsonDocument getDocument() throws JsonParseException {
        return BsonDocument.parse(getJson());
    }

    public void setDocument(BsonDocument document) {
        setJson(Bsons.shellAndPretty(document != null ? document : new BsonDocument()));
    }

    public static BsonDocument show(String title, BsonDocument document) {
        return show(title, document, true);
    }

    /**
     * Displays a modal dialog to input json.
     *
     * @param title the dialog title
     * @param defaultJson the default json
     * @return a DBObject representing the input json or null if the dialog has
     * been cancelled.
     */
    public static BsonDocument show(String title, BsonDocument document, boolean searchPanel) {
        BsonDocumentEditor editor = new BsonDocumentEditor(searchPanel);
        String json = Bsons.shellAndPretty(document != null ? document : new BsonDocument());
        editor.setJson(json);
        boolean doLoop = true;
        while (doLoop) {
            doLoop = false;
            final DialogDescriptor desc = new DialogDescriptor(editor, title);
            final JDialog dialog = (JDialog) DialogDisplayer.getDefault().createDialog(desc);
            if (searchPanel) {
                editor.setNotificationLineSupport(desc.createNotificationLineSupport());
                // escape key used to close search panel
                dialog.getRootPane().getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).remove(ESCAPE_KEYSTROKE);
            }
            dialog.setVisible(true);
            if (desc.getValue().equals(NotifyDescriptor.OK_OPTION)) {
                try {
                    return editor.getDocument();
                } catch (JsonParseException ex) {
                    DialogNotification.error(Bundle.invalidJson());
                    doLoop = true;
                }
            }
        }
        return null;
    }

    /**
     * Displays a modal dialog to display json text.
     *
     * @param title the dialog title
     * @param json the json to display
     */
    public static void showReadOnly(String title, BsonDocument document) {
        showReadOnly(title, document, true);
    }

    public static void showReadOnly(String title, BsonDocument document, boolean searchPanel) {
        BsonDocumentEditor editor = new BsonDocumentEditor(searchPanel);
        editor.editor.setEditable(false);
        editor.setDocument(document);
        final DialogDescriptor desc = new DialogDescriptor(editor, title, true, NotifyDescriptor.PLAIN_MESSAGE, NotifyDescriptor.OK_OPTION, null);
        final JDialog dialog = (JDialog) DialogDisplayer.getDefault().createDialog(desc);
        if (searchPanel) {
            editor.setNotificationLineSupport(desc.createNotificationLineSupport());
            // escape key used to close search panel
            dialog.getRootPane().getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).remove(ESCAPE_KEYSTROKE);
        }
        dialog.setVisible(true);
    }
}
