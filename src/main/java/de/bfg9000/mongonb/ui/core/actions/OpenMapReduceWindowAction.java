package de.bfg9000.mongonb.ui.core.actions;

import de.bfg9000.mongonb.ui.core.windows.MapReduceTopComponent;
import java.awt.event.ActionEvent;
import java.util.ResourceBundle;
import javax.swing.AbstractAction;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.NbBundle.Messages;

/**
 * Opens a {@code MapReduceTopComponent}.
 *
 * @author thomaswerner35
 */
@Messages({
    "ACTION_OpenMapReduceWindow_name=Map/Reduce..."
})
public class OpenMapReduceWindowAction extends AbstractAction {

    private static final long serialVersionUID = 1L;
    
    private static final ResourceBundle bundle = NbBundle.getBundle(OpenMapReduceWindowAction.class);

    private final Lookup lookup;
    
    public OpenMapReduceWindowAction(Lookup lookup) {
        super(Bundle.ACTION_OpenMapReduceWindow_name());
        this.lookup = lookup;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        final MapReduceTopComponent tc  = new MapReduceTopComponent(lookup);
        tc.open();
        tc.requestActive();
    }

}
