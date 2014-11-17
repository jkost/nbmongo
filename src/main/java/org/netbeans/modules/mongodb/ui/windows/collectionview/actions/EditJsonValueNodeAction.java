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
package org.netbeans.modules.mongodb.ui.windows.collectionview.actions;

import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoException;
import java.awt.event.ActionEvent;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.jdesktop.swingx.treetable.TreeTableNode;
import org.netbeans.modules.mongodb.ui.util.JsonPropertyEditor;
import org.netbeans.modules.mongodb.ui.windows.CollectionView;
import org.netbeans.modules.mongodb.ui.windows.collectionview.treetable.DocumentNode;
import org.netbeans.modules.mongodb.ui.windows.collectionview.treetable.JsonValueNode;
import org.netbeans.modules.mongodb.util.JsonProperty;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.NbBundle.Messages;

/**
 *
 * @author Yann D'Isanto
 */
@Messages({
    "editJsonValueTitle=Edit json value",
    "ACTION_editJsonValue=Edit json value",
    "ACTION_editJsonValue_tooltip=Edit Selected JSON Value"
})
public final class EditJsonValueNodeAction extends CollectionViewAction {

    @Getter
    @Setter
    private JsonValueNode valueNode;

    public EditJsonValueNodeAction(CollectionView view, JsonValueNode valueNode) {
        super(view,
            Bundle.ACTION_editJsonValue(),
            null,
            Bundle.ACTION_editJsonValue_tooltip());
        this.valueNode = valueNode;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void actionPerformed(ActionEvent e) {
        Object value = valueNode.getUserObject();
        Object newValue = JsonPropertyEditor.show("value", value);
        if (newValue == null || newValue.equals(value)) {
            return;
        }
        getView().getTreeTableModel().setUserObject(valueNode, newValue);
        TreeTableNode parentNode = valueNode.getParent();
        JsonProperty parent = (JsonProperty) parentNode.getUserObject();
        List<Object> list = (List<Object>) parent.getValue();
        int index = list.indexOf(value);
        list.set(index, newValue);
        while ((parentNode instanceof DocumentNode) == false) {
            parentNode = parentNode.getParent();
        }
        try {
            final DBCollection dbCollection = getView().getLookup().lookup(DBCollection.class);
            dbCollection.save((DBObject) parentNode.getUserObject());
        } catch (MongoException ex) {
            DialogDisplayer.getDefault().notify(
                new NotifyDescriptor.Message(ex.getLocalizedMessage(), NotifyDescriptor.ERROR_MESSAGE));
        }
    }
}
