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
import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.jdesktop.swingx.treetable.TreeTableNode;
import org.netbeans.modules.mongodb.ui.util.JsonPropertyEditor;
import org.netbeans.modules.mongodb.ui.windows.CollectionView;
import org.netbeans.modules.mongodb.ui.windows.collectionview.treetable.DocumentNode;
import org.netbeans.modules.mongodb.ui.windows.collectionview.treetable.JsonPropertyNode;
import org.netbeans.modules.mongodb.util.JsonProperty;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.NbBundle.Messages;

/**
 *
 * @author Yann D'Isanto
 */
@Messages({
    "editJsonPropertyTitle=Edit json property",
    "ACTION_editJsonProperty=Edit json property",
    "ACTION_editJsonProperty_tooltip=Edit Selected JSON Property"
})
public final class EditJsonPropertyNodeAction extends CollectionViewAction {

    @Getter
    @Setter
    private JsonPropertyNode propertyNode;
    
    public EditJsonPropertyNodeAction(CollectionView view, JsonPropertyNode propertyNode) {
        super(view,
            Bundle.ACTION_editJsonProperty(),
            null,
            Bundle.ACTION_editJsonProperty_tooltip());
        this.propertyNode = propertyNode;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JsonProperty property = propertyNode.getUserObject();
        JsonProperty newProperty = JsonPropertyEditor.show(property);
        if (newProperty == null) {
            return;
        }
        getView().getTreeTableModel().setUserObject(propertyNode, newProperty);
        TreeTableNode parentNode = propertyNode.getParent();
        DBObject parent = (DBObject) parentNode.getUserObject();
        if (newProperty.getName().equals(property.getName()) == false) {
            parent.removeField(property.getName());
        }
        parent.put(newProperty.getName(), newProperty.getValue());
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
