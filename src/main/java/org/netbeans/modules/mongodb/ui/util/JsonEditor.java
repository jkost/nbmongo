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
import org.bson.Document;
import org.bson.json.JsonParseException;
import org.netbeans.api.editor.mimelookup.MimeLookup;
import org.netbeans.modules.mongodb.util.Json;
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
public class JsonEditor extends JPanel {

    private static final long serialVersionUID = 1L;

    private static final KeyStroke ESCAPE_KEYSTROKE = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);

    private static final KeyStroke SEARCH_KEYSTROKE = KeyStroke.getKeyStroke(KeyEvent.VK_F, KeyEvent.CTRL_DOWN_MASK);

    private final JEditorPane editor = new JEditorPane();

    private final JPanel searchPane = new JPanel(new BorderLayout());

    private final JTextField searchField = new JTextField();

    private int lastSearchIndex = 0;

    @Getter
    private NotificationLineSupport notificationLineSupport;

    private JsonEditor() {
        super(new BorderLayout());
        EditorKit editorKit = MimeLookup.getLookup("text/x-json").lookup(EditorKit.class);
        if (editorKit != null) {
            editor.setEditorKit(editorKit);
        }
        final JScrollPane scrollPane = new JScrollPane(editor);
        scrollPane.setPreferredSize(new Dimension(450, 300));
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
        add(scrollPane, BorderLayout.CENTER);
        add(searchPane, BorderLayout.SOUTH);
        searchPane.setVisible(false);
    }

    public void setNotificationLineSupport(NotificationLineSupport notificationLineSupport) {
        this.notificationLineSupport = notificationLineSupport;
        notificationLineSupport.clearMessages();
        if (searchPane.isVisible() == false) {
            notificationLineSupport.setInformationMessage(Bundle.HINT_search());
        }
    }

    public String getJson() {
        return editor.getText().trim();
    }

    public void setJson(String json) {
        editor.setText(json);
        editor.setCaretPosition(0);
    }

    public void setJson(Document document) {
        editor.setText(Json.prettify(document));
        editor.setCaretPosition(0);
    }

    /**
     * Displays a modal dialog to input json.
     *
     * @param title the dialog title
     * @param defaultJson the default json
     * @return a DBObject representing the input json or null if the dialog has
     * been cancelled.
     */
    public static Document show(String title, Document document) {
        JsonEditor editor = new JsonEditor();
        String json = document != null ? Json.prettify(document) : "{}";
        boolean doLoop = true;
        while (doLoop) {
            doLoop = false;
            editor.setJson(json);
            final DialogDescriptor desc = new DialogDescriptor(editor, title);
            editor.setNotificationLineSupport(desc.createNotificationLineSupport());
            final JDialog dialog = (JDialog) DialogDisplayer.getDefault().createDialog(desc);
            // escape key used to close search panel
            dialog.getRootPane().getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).remove(ESCAPE_KEYSTROKE);
            dialog.setVisible(true);
            if (desc.getValue().equals(NotifyDescriptor.OK_OPTION)) {
                try {
                    json = editor.getJson();
                    return Document.parse(json);
                } catch (JsonParseException ex) {
                    DialogDisplayer.getDefault().notify(
                        new NotifyDescriptor.Message(Bundle.invalidJson(), NotifyDescriptor.ERROR_MESSAGE));
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
    public static void showReadOnly(String title, Document document) {
        JsonEditor editor = new JsonEditor();
        editor.editor.setEditable(false);
        editor.setJson(Json.prettify(document));
        final DialogDescriptor desc = new DialogDescriptor(editor, title, true, NotifyDescriptor.PLAIN_MESSAGE, NotifyDescriptor.OK_OPTION, null);
        editor.setNotificationLineSupport(desc.createNotificationLineSupport());
        final JDialog dialog = (JDialog) DialogDisplayer.getDefault().createDialog(desc);
        // escape key used to close search panel
        dialog.getRootPane().getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).remove(ESCAPE_KEYSTROKE);
        dialog.setVisible(true);
    }
}
