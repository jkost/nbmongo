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
package org.netbeans.modules.mongodb.ui.wizards;

import com.mongodb.client.MongoDatabase;
import javax.swing.JComboBox;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.bson.BsonDocument;
import org.netbeans.modules.mongodb.api.FindResult;
import org.netbeans.modules.mongodb.ui.components.FindCriteriaEditor;
import org.openide.WizardDescriptor;
import org.openide.WizardValidationException;
import org.openide.util.ChangeSupport;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle.Messages;

@Messages({
    "validation_no_collection_selected=no collection selected"})
public class ExportWizardPanel1 implements WizardDescriptor.ValidatingPanel<WizardDescriptor>, ChangeListener {

    private ExportVisualPanel1 component;

    private final ChangeSupport changeSupport = new ChangeSupport(this);

    private final MongoDatabase db;

    public ExportWizardPanel1(MongoDatabase db) {
        this.db = db;
    }

    @Override
    public ExportVisualPanel1 getComponent() {
        if (component == null) {
            component = new ExportVisualPanel1(db);
            component.addChangeListener(this);
        }
        return component;
    }

    @Override
    public HelpCtx getHelp() {
        return HelpCtx.DEFAULT_HELP;
    }

    @Override
    public void validate() throws WizardValidationException {
        final JComboBox collectionField = component.getCollectionComboBox();
        if (collectionField.getSelectedIndex() < 0) {
            throw new WizardValidationException(collectionField, Bundle.validation_no_collection_selected(), null);
        }
    }

    @Override
    public boolean isValid() {
        final JComboBox collectionField = component.getCollectionComboBox();
        return collectionField.getSelectedIndex() >= 0;
    }

    @Override
    public void readSettings(WizardDescriptor wiz) {
        final ExportVisualPanel1 panel = getComponent();
        final FindCriteriaEditor criteriaEditor = panel.getCriteriaEditor();
        FindResult documents = (FindResult) wiz.getProperty(ExportWizardAction.PROP_DOCUMENTS);
        if(documents != null) {
            panel.getCollectionComboBox().setSelectedItem(documents.getCollection().getNamespace().getCollectionName());
            criteriaEditor.setFindCriteria(documents.getFindCriteria());
        } else {
            String collection = (String) wiz.getProperty(ExportWizardAction.PROP_COLLECTION);
            if (collection != null) {
                panel.getCollectionComboBox().setSelectedItem(collection);
            }
        }
        panel.updateQueryFieldsFromEditor();
    }

    @Override
    public void storeSettings(WizardDescriptor wiz) {
        ExportVisualPanel1 panel = getComponent();
        String colName = (String) panel.getCollectionComboBox().getSelectedItem();
        wiz.putProperty(ExportWizardAction.PROP_COLLECTION, colName);
        wiz.putProperty(ExportWizardAction.PROP_DOCUMENTS,
                new FindResult(
                        db.getCollection(colName, BsonDocument.class),
                        panel.getCriteriaEditor().getFindCriteria()
                ));
    }

    @Override
    public void addChangeListener(ChangeListener l) {
        changeSupport.addChangeListener(l);
    }

    @Override
    public void removeChangeListener(ChangeListener l) {
        changeSupport.removeChangeListener(l);
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        changeSupport.fireChange();
    }

}
