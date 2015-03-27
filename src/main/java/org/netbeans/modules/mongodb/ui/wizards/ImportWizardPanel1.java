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

import com.mongodb.DB;
import java.io.File;
import java.nio.charset.Charset;
import javax.swing.JFileChooser;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.openide.WizardDescriptor;
import org.openide.WizardValidationException;
import org.openide.util.ChangeSupport;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle.Messages;

@Messages({
    "validation_no_collection_specified=no collection specified"})
public class ImportWizardPanel1 implements WizardDescriptor.ValidatingPanel<WizardDescriptor>, ChangeListener {

    private static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");

    private final ChangeSupport changeSupport = new ChangeSupport(this);

    private ImportVisualPanel1 component;

    private final DB db;

    public ImportWizardPanel1(DB db) {
        this.db = db;
    }
    
    
    @Override
    public ImportVisualPanel1 getComponent() {
        if (component == null) {
            component = new ImportVisualPanel1(db);
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
        final ImportVisualPanel1 panel = getComponent();
        if(panel.getFileChooser().getSelectedFile() == null) {
            throw new WizardValidationException(null, Bundle.validation_file_missing(), null);
        }
        if(panel.getCollectionEditor().getText().trim().isEmpty()) {
            throw new WizardValidationException(null, Bundle.validation_no_collection_specified(), null);
        }
    }

    @Override
    public boolean isValid() {
        final ImportVisualPanel1 panel = getComponent();
        return panel.getFileChooser().getSelectedFile() != null 
            && panel.getCollectionEditor().getText().trim().isEmpty() == false;
    }

    @Override
    public void readSettings(WizardDescriptor wiz) {
        final ImportVisualPanel1 panel = getComponent();
        final JFileChooser fileChooser = panel.getFileChooser();
        final File file = (File) wiz.getProperty(ImportWizardAction.PROP_FILE);
        fileChooser.setSelectedFile(file);
        panel.getFileField().setText(file != null ? file.getAbsolutePath() : "");
        final Charset charset = (Charset) wiz.getProperty(ImportWizardAction.PROP_ENCODING);
        panel.getEncodingComboBox().setSelectedItem(charset != null ? charset : DEFAULT_CHARSET);
        
        String collection = (String) wiz.getProperty(ImportWizardAction.PROP_COLLECTION);
        if(collection == null) {
            collection = "";
        }
        if(collection.isEmpty() && file != null) {
            collection = file.getName().replaceAll("\\.json$", "");
        }
        if(collection != null) {
            panel.getCollectionEditor().setText(collection);
        }
        final Boolean drop = (Boolean) wiz.getProperty(ImportWizardAction.PROP_DROP);
        panel.getDropCheckBox().setSelected(drop != null ? drop : false);
    }

    @Override
    public void storeSettings(WizardDescriptor wiz) {
        final ImportVisualPanel1 panel = getComponent();
        wiz.putProperty(ImportWizardAction.PROP_FILE, 
            panel.getFileChooser().getSelectedFile());
        wiz.putProperty(ImportWizardAction.PROP_ENCODING, 
            panel.getEncodingComboBox().getSelectedItem());
        wiz.putProperty(ImportWizardAction.PROP_COLLECTION, 
            panel.getCollectionEditor().getText().trim());
        wiz.putProperty(ImportWizardAction.PROP_DROP, 
            panel.getDropCheckBox().isSelected());
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
