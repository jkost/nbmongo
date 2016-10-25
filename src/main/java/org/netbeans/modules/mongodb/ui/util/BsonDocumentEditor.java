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
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import lombok.Getter;
import org.bson.BsonDocument;
import org.bson.json.JsonParseException;
import org.netbeans.modules.mongodb.bson.Bsons;
import org.netbeans.modules.mongodb.ui.components.SearchableTextComponent;
import org.netbeans.modules.mongodb.ui.components.SearchableTextComponent.MessageDisplayer.NotificationLineSupportMessageDisplayer;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.NotificationLineSupport;
import org.openide.NotifyDescriptor;
import org.openide.util.NbBundle.Messages;

/**
 *
 * @author Yann D'Isanto
 */
@Messages({
    "invalidJson=invalid json"
})
public class BsonDocumentEditor extends JPanel {

    private static final long serialVersionUID = 1L;

    private final JEditorPane editor = new JEditorPane();
    
    private final SearchableTextComponent searchableEditor;

    @Getter
    private NotificationLineSupport notificationLineSupport;

    private BsonDocumentEditor() {
        super(new BorderLayout());
        JsonUIUtils.setJsonEditorKit(editor);
        searchableEditor = new SearchableTextComponent(editor);
        add(searchableEditor, BorderLayout.CENTER);
    }

    public void setNotificationLineSupport(NotificationLineSupport notificationLineSupport) {
        this.notificationLineSupport = notificationLineSupport;
        notificationLineSupport.clearMessages();
        searchableEditor.setMessagesDisplayer(new NotificationLineSupportMessageDisplayer(notificationLineSupport));
    }

    public String getJson() {
        return editor.getText().trim();
    }

    private void setJson(String json) {
        editor.setText(json);
        editor.setCaretPosition(0);
    }

    public void setDocument(BsonDocument document) {
        setJson(Bsons.shellAndPretty(document != null ? document : new BsonDocument()));
    }

    /**
     * Displays a modal dialog to input a BsonDocument.
     *
     * @param title the dialog title
     * @param document a bson document or {@literal null} for empty document
     * @return a DBObject representing the input json or null if the dialog has
     * been cancelled.
     */
    public static BsonDocument show(String title, BsonDocument document) {
        BsonDocumentEditor editor = new BsonDocumentEditor();
        String json = Bsons.shellAndPretty(document != null ? document : new BsonDocument());
        boolean doLoop = true;
        while (doLoop) {
            doLoop = false;
            editor.setJson(json);
            final DialogDescriptor desc = new DialogDescriptor(editor, title);
            editor.setNotificationLineSupport(desc.createNotificationLineSupport());
            final JDialog dialog = (JDialog) DialogDisplayer.getDefault().createDialog(desc);
            // escape key used to close search panel
            dialog.getRootPane().getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).remove(SearchableTextComponent.ESCAPE_KEYSTROKE);
            dialog.setVisible(true);
            if (desc.getValue().equals(NotifyDescriptor.OK_OPTION)) {
                json = editor.getJson();
                try {
                    return BsonDocument.parse(json);
                } catch (JsonParseException ex) {
                    DialogNotification.error(Bundle.invalidJson());
                    doLoop = true;
                }
            }
        }
        return null;
    }

    /**
     * Displays a modal dialog to display a BsonDocument.
     *
     * @param title the dialog title
     * @param document the bson document
     */
    public static void showReadOnly(String title, BsonDocument document) {
        BsonDocumentEditor editor = new BsonDocumentEditor();
        editor.editor.setEditable(false);
        editor.setDocument(document);
        final DialogDescriptor desc = new DialogDescriptor(editor, title, true, NotifyDescriptor.PLAIN_MESSAGE, NotifyDescriptor.OK_OPTION, null);
        editor.setNotificationLineSupport(desc.createNotificationLineSupport());
        final JDialog dialog = (JDialog) DialogDisplayer.getDefault().createDialog(desc);
        // escape key used to close search panel
        dialog.getRootPane().getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).remove(SearchableTextComponent.ESCAPE_KEYSTROKE);
        dialog.setVisible(true);
    }
}
