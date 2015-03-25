/* 
 * The MIT License
 *
 * Copyright 2013 Tim Boudreau.
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
package org.netbeans.modules.mongodb.ui.explorer;

import com.mongodb.BasicDBObject;
import org.netbeans.modules.mongodb.resources.Images;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoException;
import de.bfg9000.mongonb.core.CollectionStats;
import de.bfg9000.mongonb.ui.core.actions.ManageIndexesAction;
import de.bfg9000.mongonb.ui.core.actions.OpenMapReduceWindowAction;
import de.bfg9000.mongonb.ui.core.windows.MapReduceTopComponent;
import java.awt.Image;
import org.netbeans.modules.mongodb.ui.util.TopComponentUtils;
import java.awt.event.ActionEvent;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.netbeans.modules.mongodb.CollectionInfo;
import org.netbeans.modules.mongodb.native_tools.MongoNativeToolsAction;
import org.netbeans.modules.mongodb.ui.util.CollectionNameValidator;
import org.netbeans.modules.mongodb.ui.util.ValidatingInputLine;
import org.netbeans.modules.mongodb.ui.windows.CollectionView;
import org.netbeans.modules.mongodb.ui.windows.QueryResultPanelContainer;
import org.netbeans.modules.mongodb.ui.wizards.ExportWizardAction;
import org.netbeans.modules.mongodb.ui.wizards.ImportWizardAction;
import org.netbeans.modules.mongodb.util.SystemCollectionPredicate;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.actions.OpenAction;
import org.openide.cookies.OpenCookie;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Sheet;
import org.openide.util.Lookup;
import org.openide.util.NbBundle.Messages;
import org.openide.util.actions.SystemAction;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;
import org.openide.util.lookup.Lookups;
import org.openide.util.lookup.ProxyLookup;
import org.openide.windows.TopComponent;

/**
 *
 * @author Tim Boudreau
 * @author Yann D'Isanto
 */
@Messages({
    "ACTION_DropCollection=Drop Collection",
    "ACTION_RenameCollection=Rename Collection",
    "ACTION_ClearCollection=Clear Collection",
    "# {0} - collection name",
    "dropCollectionConfirmText=Permanently drop ''{0}'' collection?",
    "# {0} - collection name",
    "renameCollectionText=Rename ''{0}'' to:",
    "# {0} - collection name",
    "clearCollectionConfirmText=Remove all documents of ''{0}'' collection?"})
final class CollectionNode extends AbstractNode {

    private final CollectionInfo collection;

    CollectionNode(CollectionInfo connection) {
        this(connection, new InstanceContent());
    }

    CollectionNode(CollectionInfo connection, InstanceContent content) {
        this(connection, content, new ProxyLookup(new AbstractLookup(content), Lookups.fixed(connection), connection.getLookup()));
    }

    CollectionNode(final CollectionInfo collection, final InstanceContent content, final Lookup lookup) {
        super(Children.LEAF, lookup);
        this.collection = collection;
        content.add(collection);
        content.add(collection, new CollectionConverter());
        content.add(new OpenCookie() {
            @Override
            public void open() {
                TopComponent tc = TopComponentUtils.find(CollectionView.class, collection);
                if (tc == null) {
                    tc = new CollectionView(collection, lookup);
                    tc.open();
                }
                tc.requestActive();
            }
        });
    }

    @Override
    public String getName() {
        return collection.getName();
    }

    @Override
    public Image getIcon(int ignored) {
        if (SystemCollectionPredicate.get().eval(collection.getName())) {
            return Images.SYSTEM_COLLECTION_ICON;
        } else {
            return Images.COLLECTION_ICON;
        }
    }

    @Override
    protected Sheet createSheet() {
        Sheet sheet = Sheet.createDefault();
        Sheet.Set set = Sheet.createPropertiesSet();
        DBCollection col = getLookup().lookup(DBCollection.class);
        final CollectionStats stats = new CollectionStats(col.isCapped(), col.getStats());

        set.put(new CollectionStatsProperty("serverUsed", stats.getServerUsed()));
        set.put(new CollectionStatsProperty("ns", stats.getNs()));
        set.put(new CollectionStatsProperty("capped", stats.getCapped()));
        set.put(new CollectionStatsProperty("count", stats.getCount()));
        set.put(new CollectionStatsProperty("size", stats.getSize()));
        set.put(new CollectionStatsProperty("storageSize", stats.getStorageSize()));
        set.put(new CollectionStatsProperty("numExtents", stats.getNumExtents()));
        set.put(new CollectionStatsProperty("nindexes", stats.getNindexes()));
        set.put(new CollectionStatsProperty("lastExtentSize", stats.getLastExtentSize()));
        set.put(new CollectionStatsProperty("paddingFactor", stats.getPaddingFactor()));
        set.put(new CollectionStatsProperty("systemFlags", stats.getSystemFlags()));
        set.put(new CollectionStatsProperty("userFlags", stats.getUserFlags()));
        set.put(new CollectionStatsProperty("totalIndexSize", stats.getTotalIndexSize()));
        set.put(new CollectionStatsProperty("ok", stats.getOk()));

        sheet.put(set);
        return sheet;
    }

    @Override
    public Action[] getActions(boolean ignored) {
        final Map<String, Object> properties = new HashMap<>();
        properties.put(ExportWizardAction.PROP_COLLECTION, collection.getName());
        properties.put(ImportWizardAction.PROP_COLLECTION, collection.getName());
        final Action importAction = new ImportWizardAction(getLookup(), properties);
        final Action renameAction = new RenameCollectionAction();
        final Action dropAction = new DropCollectionAction();
        final Action clearAction = new ClearCollectionAction();
        if (SystemCollectionPredicate.get().eval(collection.getName())) {
            importAction.setEnabled(false);
            renameAction.setEnabled(false);
            dropAction.setEnabled(false);
            clearAction.setEnabled(false);
        }
        final List<Action> actions = new LinkedList<>();
        actions.add(SystemAction.get(OpenAction.class));
        actions.add(new OpenMapReduceWindowAction(getLookup()));
        actions.add(null);
        actions.add(new ManageIndexesAction(getLookup()));
        actions.add(null);
        actions.add(clearAction);
        actions.add(dropAction);
        actions.add(renameAction);
        actions.add(null);
        actions.add(new MongoNativeToolsAction(getLookup()));
        actions.add(null);
        actions.add(new ExportWizardAction(getLookup(), properties));
        actions.add(importAction);
        final Action[] orig = super.getActions(ignored);
        if (orig.length > 0) {
            actions.add(null);
        }
        actions.addAll(Arrays.asList(orig));
        return actions.toArray(new Action[actions.size()]);
    }

    @Override
    public Action getPreferredAction() {
        return SystemAction.get(OpenAction.class);
    }

    private class CollectionConverter implements InstanceContent.Convertor<CollectionInfo, DBCollection> {

        @Override
        public DBCollection convert(CollectionInfo t) {
            DB db = getLookup().lookup(DB.class);
            return db.getCollection(t.getName());
        }

        @Override
        public Class<? extends DBCollection> type(CollectionInfo t) {
            return DBCollection.class;
        }

        @Override
        public String id(CollectionInfo t) {
            return t.getName();
        }

        @Override
        public String displayName(CollectionInfo t) {
            return id(t);
        }
    }

    public class DropCollectionAction extends AbstractAction {

        public DropCollectionAction() {
            super(Bundle.ACTION_DropCollection());
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            final CollectionInfo ci = getLookup().lookup(CollectionInfo.class);
            final Object dlgResult = DialogDisplayer.getDefault().notify(new NotifyDescriptor.Confirmation(
                Bundle.dropCollectionConfirmText(ci.getName()),
                NotifyDescriptor.YES_NO_OPTION));
            if (dlgResult.equals(NotifyDescriptor.OK_OPTION)) {
                try {
                    getLookup().lookup(DBCollection.class).drop();
                    ((OneDbNode) getParentNode()).refreshChildren();
                    for (TopComponent topComponent : TopComponentUtils.findAll(ci, CollectionView.class, MapReduceTopComponent.class)) {
                        topComponent.close();
                    }
                } catch (MongoException ex) {
                    DialogDisplayer.getDefault().notify(
                        new NotifyDescriptor.Message(ex.getLocalizedMessage(), NotifyDescriptor.ERROR_MESSAGE));
                }
            }
        }
    }

    public class RenameCollectionAction extends AbstractAction {

        public RenameCollectionAction() {
            super(Bundle.ACTION_RenameCollection());
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            final NotifyDescriptor.InputLine input = new ValidatingInputLine(
                Bundle.renameCollectionText(collection.getName()),
                Bundle.ACTION_RenameCollection(),
                new CollectionNameValidator(getLookup()));
            input.setInputText(collection.getName());
            final Object dlgResult = DialogDisplayer.getDefault().notify(input);
            if (dlgResult.equals(NotifyDescriptor.OK_OPTION)) {
                try {
                    final String name = input.getInputText().trim();
                    getLookup().lookup(DBCollection.class).rename(name);
                    final OneDbNode parentNode = (OneDbNode) getParentNode();
                    parentNode.refreshChildren();

                    CollectionNode node = (CollectionNode) parentNode.getChildren().findChild(name);
                    Lookup lookup = node != null ? node.getLookup() : null;
                    final CollectionView view = TopComponentUtils.find(CollectionView.class, collection);
                    if (view != null) {
                        if (lookup != null) {
                            view.setLookup(lookup);
                            view.updateTitle();
                        }
                    }
                    for (MapReduceTopComponent mapReduceComponent : TopComponentUtils.findAll(MapReduceTopComponent.class, collection)) {
                        if (lookup != null) {
                            mapReduceComponent.setLookup(lookup);
                            mapReduceComponent.updateCollectionLabel();
                        }
                    }
                } catch (MongoException ex) {
                    DialogDisplayer.getDefault().notify(
                        new NotifyDescriptor.Message(ex.getLocalizedMessage(), NotifyDescriptor.ERROR_MESSAGE));
                }
            }
        }
    }
    
    public class ClearCollectionAction extends AbstractAction {

        public ClearCollectionAction() {
            super(Bundle.ACTION_ClearCollection());
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            final CollectionInfo ci = getLookup().lookup(CollectionInfo.class);
            final Object dlgResult = DialogDisplayer.getDefault().notify(new NotifyDescriptor.Confirmation(
                Bundle.clearCollectionConfirmText(ci.getName()),
                NotifyDescriptor.YES_NO_OPTION));
            if (dlgResult.equals(NotifyDescriptor.OK_OPTION)) {
                try {
                    getLookup().lookup(DBCollection.class).remove(new BasicDBObject());
                    for (TopComponent topComponent : TopComponentUtils.findAll(ci, CollectionView.class, MapReduceTopComponent.class)) {
                        ((QueryResultPanelContainer) topComponent).getResultPanel().refreshResults();
                        
                    }
                } catch (MongoException ex) {
                    DialogDisplayer.getDefault().notify(
                        new NotifyDescriptor.Message(ex.getLocalizedMessage(), NotifyDescriptor.ERROR_MESSAGE));
                }
            }
        }
    }

}
