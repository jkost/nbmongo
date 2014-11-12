/*
 * The MIT License
 *
 * Copyright 2014 Yann D'Isanto.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.netbeans.modules.mongodb.ui.util;

import com.mongodb.DBObject;
import com.mongodb.util.JSON;
import com.mongodb.util.JSONParseException;
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
import lombok.Setter;
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
    "invalidJson=invalid json"
})
public class JsonEditor extends JPanel {

    private static final KeyStroke ESCAPE_KEYSTROKE = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);

    private static final KeyStroke SEARCH_KEYSTROKE = KeyStroke.getKeyStroke(KeyEvent.VK_F, KeyEvent.CTRL_DOWN_MASK);

    private final JEditorPane editor = new JEditorPane();

    private final JPanel searchPane = new JPanel(new BorderLayout());

    private final JTextField searchField = new JTextField();

    private int lastSearchIndex = 0;

    @Getter
    @Setter
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
                } else {
                    notificationLineSupport.setInformationMessage("no result found");
                }
            }
        });
        searchField.getDocument().addDocumentListener(new DocumentListener() {

            @Override
            public void insertUpdate(DocumentEvent e) {
                lastSearchIndex = 0;
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                lastSearchIndex = 0;
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
            }
        });
        add(scrollPane, BorderLayout.CENTER);
        add(searchPane, BorderLayout.SOUTH);
        searchPane.setVisible(false);
    }

    public String getJson() {
        return editor.getText().trim();
    }

    public void setJson(String json) {
        editor.setText(json);
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
    public static DBObject show(String title, String defaultJson) {
        JsonEditor editor = new JsonEditor();
        String json = defaultJson.trim().isEmpty() ? "{}" : Json.prettify(defaultJson);
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
                    return (DBObject) JSON.parse(json);
                } catch (JSONParseException ex) {
                    DialogDisplayer.getDefault().notify(
                        new NotifyDescriptor.Message(Bundle.invalidJson(), NotifyDescriptor.ERROR_MESSAGE));
                    doLoop = true;
                }
            }
        }
        return null;
    }

}
