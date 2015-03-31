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
package org.netbeans.modules.mongodb.ui.explorer;

import com.mongodb.BasicDBObject;
import org.netbeans.modules.mongodb.resources.Images;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoException;
import de.bfg9000.mongonb.core.CollectionStats;
import de.bfg9000.mongonb.ui.core.actions.OpenMapReduceWindowAction;
import de.bfg9000.mongonb.ui.core.windows.MapReduceTopComponent;
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
import org.netbeans.modules.mongodb.indexes.CreateIndexPanel;
import org.netbeans.modules.mongodb.indexes.Index;
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
    "ACTION_RefreshIndexes=Refresh Indexes",
    "ACTION_CreateIndex=Create Index",
    "# {0} - collection name",
    "dropCollectionConfirmText=Permanently drop ''{0}'' collection?",
    "# {0} - collection name",
    "renameCollectionText=Rename ''{0}'' to:",
    "# {0} - collection name",
    "clearCollectionConfirmText=Remove all documents of ''{0}'' collection?"})
final class CollectionNode extends AbstractNode {

    private final CollectionChildFactory childFactory;

    private final CollectionInfo collection;

    CollectionNode(CollectionInfo connection) {
        this(connection, new InstanceContent());
    }

    CollectionNode(CollectionInfo connection, InstanceContent content) {
        this(connection, content, new ProxyLookup(new AbstractLookup(content), Lookups.fixed(connection), connection.getLookup()));
    }

    CollectionNode(final CollectionInfo collection, final InstanceContent content, final Lookup lookup) {
        this(collection, new CollectionChildFactory(lookup), content, lookup);
    }

    CollectionNode(final CollectionInfo collection, CollectionChildFactory childFactory, final InstanceContent content, final Lookup lookup) {
        super(Children.create(childFactory, true), lookup);
        this.collection = collection;
        this.childFactory = childFactory;
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
        setIconBaseWithExtension(SystemCollectionPredicate.get().eval(collection.getName())
            ? Images.SYSTEM_COLLECTION_ICON_PATH
            : Images.COLLECTION_ICON_PATH);
    }

    @Override
    public String getName() {
        return collection.getName();
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
        actions.add(new RefreshChildrenAction(Bundle.ACTION_RefreshIndexes(), childFactory));
        actions.add(new CreateIndexAction());
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

    public void refreshChildren() {
        childFactory.refresh();
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

    public class CreateIndexAction extends AbstractAction {

        public CreateIndexAction() {
            super(Bundle.ACTION_CreateIndex());
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            Index index = CreateIndexPanel.showDialog();
            if (index != null) {
                DBCollection collection = getLookup().lookup(DBCollection.class);
                final BasicDBObject keys = new BasicDBObject();
                for (Index.Key key : index.getKeys()) {
                    keys.append(key.getField(), key.getSort().getValue());
                }
                final BasicDBObject options = new BasicDBObject();
                options.append("name", index.getName());
                if (index.isSparse()) {
                    options.append("sparse", true);
                }
                if (index.isUnique()) {
                    options.append("unique", true);
                }
                if (index.isDropDuplicates()) {
                    options.append("dropDups", true);
                }
                collection.createIndex(keys, options);
                refreshChildren();
            }
        }
    }

}
