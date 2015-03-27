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
package org.netbeans.modules.mongodb.ui.native_tools;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import javax.swing.JPanel;
import org.netbeans.modules.mongodb.native_tools.MongoNativeTool;
import org.netbeans.modules.mongodb.options.MongoNativeToolsOptions;
import org.netbeans.modules.mongodb.util.Version;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.Lookup;
import org.openide.util.NbBundle.Messages;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author Yann D'Isanto
 */
@Messages({
    "# {0} - required version",
    "requiresVersion=requires version {0}",
    "# {0} - tool",
    "# {1} - version",
    "dialogTitle=mongodump {0} {1}"
})
public final class NativeToolOptionsDialog {

    private static final Map<MongoNativeTool, OptionsAndArgsPanel> OPTIONS_PANELS = new EnumMap<>(MongoNativeTool.class);

    static {
        final Lookup lookup = Lookups.metaInfServices(Thread.currentThread().getContextClassLoader());
        for (OptionsAndArgsPanel optionsPanel : lookup.lookupAll(NativeToolOptionsDialog.OptionsAndArgsPanel.class)) {
            OPTIONS_PANELS.put(optionsPanel.getNativeTool(), optionsPanel);
        }
        
    }

    public static interface OptionsAndArgsPanel {

        MongoNativeTool getNativeTool();
        
        List<String> getArgs();

        Map<String, String> getOptions();

        JPanel getPanel();

        void setOptions(Map<String, String> options);

        void setArgs(List<String> args);
    }

    private final MongoNativeTool tool;

    private final OptionsAndArgsPanel optionsPanel;

    private NativeToolOptionsDialog(MongoNativeTool tool, OptionsAndArgsPanel optionsPanel) {
        this.tool = tool;
        this.optionsPanel = optionsPanel;
    }

    public List<String> getArgs() {
        return optionsPanel.getArgs();
    }

    public Map<String, String> getOptions() {
        return optionsPanel.getOptions();
    }

    public static NativeToolOptionsDialog get(MongoNativeTool tool) {
        final OptionsAndArgsPanel panel = OPTIONS_PANELS.get(tool);
        return new NativeToolOptionsDialog(tool, panel);
    }

    public boolean show(Map<String, String> options, String... args) {
        return show(options, Arrays.asList(args));
    }

    public boolean show(List<String> args) {
        return show(null, args);
    }

    public boolean show(String... args) {
        return show(null, Arrays.asList(args));
    }

    public boolean show(Map<String, String> options, List<String> args) {
        if (options != null) {
            optionsPanel.setOptions(options);
        }
        if (args != null) {
            optionsPanel.setArgs(args);
        }
        final Version version = MongoNativeToolsOptions.INSTANCE.getToolsVersion();
        final DialogDescriptor desc = new DialogDescriptor(optionsPanel.getPanel(), Bundle.dialogTitle(tool.getExecBaseName(), version));
        return NotifyDescriptor.OK_OPTION.equals(DialogDisplayer.getDefault().notify(desc));
    }
}
