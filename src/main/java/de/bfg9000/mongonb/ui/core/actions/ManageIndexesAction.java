package de.bfg9000.mongonb.ui.core.actions;

import com.mongodb.DBCollection;
import de.bfg9000.mongonb.ui.core.dialogs.IndexManagerDialog;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import org.openide.util.Lookup;
import org.openide.util.NbBundle.Messages;

/**
 * Opens the IndexManagerDialog for a given {@code Collection}.
 *
 * @author thomaswerner35
 */
@Messages({
    "ACTION_ManageIndexes_name=Manages Indexes..."
})
public class ManageIndexesAction extends AbstractAction {

    private final Lookup lookup;
    
    public ManageIndexesAction(Lookup lookup) {
        super(Bundle.ACTION_ManageIndexes_name());
        this.lookup = lookup;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        DBCollection collection = lookup.lookup(DBCollection.class);
        final IndexManagerDialog dialog = new IndexManagerDialog(collection);
        dialog.execute();
        dialog.dispose();
    }

}
