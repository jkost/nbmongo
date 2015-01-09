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
package org.netbeans.modules.mongodb.ui.windows.queryresultpanel.actions;

import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoException;
import com.mongodb.util.JSON;
import java.awt.event.ActionEvent;
import javax.swing.ImageIcon;
import lombok.Getter;
import lombok.Setter;
import org.netbeans.modules.mongodb.resources.Images;
import org.netbeans.modules.mongodb.ui.util.JsonEditor;
import org.netbeans.modules.mongodb.ui.windows.QueryResultPanel;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.NbBundle.Messages;

/**
 *
 * @author Yann D'Isanto
 */
@Messages({
    "editDocumentTitle=Edit document",
    "ACTION_editDocument=Edit document",
    "ACTION_editDocument_tooltip=Edit Document"
})
public class EditDocumentAction extends QueryResultPanelAction {

    private static final long serialVersionUID = 1L;

    @Getter
    @Setter
    private DBObject document;

    public EditDocumentAction(QueryResultPanel resultPanel) {
        super(resultPanel,
            Bundle.ACTION_editDocument(),
            new ImageIcon(Images.EDIT_DOCUMENT_ICON),
            Bundle.ACTION_editDocument_tooltip());
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (document == null) {
            return;
        }
        final DBObject modifiedDocument = JsonEditor.show(
            Bundle.editDocumentTitle(),
            JSON.serialize(document));
        if (modifiedDocument != null) {
            try {
                final DBCollection dbCollection = getResultPanel().getLookup().lookup(DBCollection.class);
                dbCollection.save(modifiedDocument);
                getResultPanel().getResultCache().editObject(document, modifiedDocument);
            } catch (MongoException ex) {
                DialogDisplayer.getDefault().notify(
                    new NotifyDescriptor.Message(ex.getLocalizedMessage(), NotifyDescriptor.ERROR_MESSAGE));
            }
        }
    }
}
