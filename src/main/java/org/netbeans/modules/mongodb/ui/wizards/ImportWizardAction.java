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
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.io.File;
import java.nio.charset.Charset;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.AbstractAction;
import javax.swing.JComponent;
import org.netbeans.modules.mongodb.util.ImportProperties;
import org.netbeans.modules.mongodb.util.ImportPropertiesBuilder;
import org.netbeans.modules.mongodb.util.ImportTask;
import org.netbeans.modules.mongodb.util.Importer;
import org.openide.DialogDisplayer;
import org.openide.WizardDescriptor;
import org.openide.util.Lookup;
import org.openide.util.NbBundle.Messages;

@Messages({"ACTION_Import=Import"})
public final class ImportWizardAction extends AbstractAction {

    public static final String PROP_COLLECTION = "collection";

    public static final String PROP_FILE = "file";

    public static final String PROP_ENCODING = "encoding";

    public static final String PROP_DROP = "drop";

    private final Lookup lookup;

    private final Runnable onDone;

    private final Map<String, Object> defaultProperties;

    public ImportWizardAction(Lookup lookup, Map<String, Object> defaultProperties) {
        this(lookup, null, defaultProperties);
    }

    public ImportWizardAction(Lookup lookup, Runnable onDone) {
        this(lookup, onDone, new HashMap<String, Object>());
    }

    public ImportWizardAction(Lookup lookup, Runnable onDone, Map<String, Object> defaultProperties) {
        super(Bundle.ACTION_Import());
        this.lookup = lookup;
        this.onDone = onDone;
        this.defaultProperties = defaultProperties;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        List<WizardDescriptor.Panel<WizardDescriptor>> panels = new ArrayList<>();
        panels.add(new ImportWizardPanel1(lookup.lookup(DB.class)));
        String[] steps = new String[panels.size()];
        for (int i = 0; i < panels.size(); i++) {
            Component c = panels.get(i).getComponent();
            // Default step name to component name of panel.
            steps[i] = c.getName();
            if (c instanceof JComponent) { // assume Swing components
                JComponent jc = (JComponent) c;
                jc.putClientProperty(WizardDescriptor.PROP_CONTENT_SELECTED_INDEX, i);
                jc.putClientProperty(WizardDescriptor.PROP_CONTENT_DATA, steps);
                jc.putClientProperty(WizardDescriptor.PROP_AUTO_WIZARD_STYLE, true);
                jc.putClientProperty(WizardDescriptor.PROP_CONTENT_DISPLAYED, true);
                jc.putClientProperty(WizardDescriptor.PROP_CONTENT_NUMBERED, true);
            }
        }
        WizardDescriptor wiz = new WizardDescriptor(new WizardDescriptor.ArrayIterator<>(panels));
        for (Map.Entry<String, Object> entry : defaultProperties.entrySet()) {
            wiz.putProperty(entry.getKey(), entry.getValue());
        }

        // {0} will be replaced by WizardDesriptor.Panel.getComponent().getName()
        wiz.setTitleFormat(new MessageFormat("{0}"));
        wiz.setTitle(Bundle.ACTION_Import());
        if (DialogDisplayer.getDefault().notify(wiz) == WizardDescriptor.FINISH_OPTION) {
            final ImportProperties properties = new ImportPropertiesBuilder()
                .collection((String) wiz.getProperty(PROP_COLLECTION))
                .drop((Boolean) wiz.getProperty(PROP_DROP))
                .file((File) wiz.getProperty(PROP_FILE))
                .encoding((Charset) wiz.getProperty(PROP_ENCODING))
                .build();
            new ImportTask(
                new Importer(lookup.lookup(DB.class), properties, onDone))
                .run();
        }
    }

}
