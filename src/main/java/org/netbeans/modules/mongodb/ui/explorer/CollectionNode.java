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

import org.netbeans.modules.mongodb.properties.LocalizedProperties;
import com.mongodb.BasicDBObject;
import org.netbeans.modules.mongodb.resources.Images;
import com.mongodb.MongoException;
import com.mongodb.MongoNamespace;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.netbeans.modules.mongodb.ui.windows.MapReduceTopComponent;
import org.netbeans.modules.mongodb.ui.util.TopComponentUtils;
import java.awt.event.ActionEvent;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.swing.AbstractAction;
import javax.swing.Action;
import lombok.AllArgsConstructor;
import org.bson.BsonDocument;
import org.bson.BsonString;
import org.bson.Document;
import org.netbeans.modules.mongodb.CollectionInfo;
import org.netbeans.modules.mongodb.api.MongoErrorCode;
import org.netbeans.modules.mongodb.indexes.CreateIndexPanel;
import org.netbeans.modules.mongodb.indexes.Index;
import org.netbeans.modules.mongodb.native_tools.MongoNativeToolsAction;
import org.netbeans.modules.mongodb.ui.actions.OpenMapReduceWindowAction;
import org.netbeans.modules.mongodb.ui.util.CollectionNameValidator;
import org.netbeans.modules.mongodb.ui.util.DialogNotification;
import org.netbeans.modules.mongodb.ui.windows.CollectionView;
import org.netbeans.modules.mongodb.ui.wizards.ExportWizardAction;
import org.netbeans.modules.mongodb.ui.wizards.ImportWizardAction;
import org.netbeans.modules.mongodb.util.SystemCollectionPredicate;
import org.netbeans.modules.mongodb.util.Tasks;
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
    "ACTION_OpenCollectionInNewTab=Open in new tab",
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
    "clearCollectionConfirmText=Remove all documents of ''{0}'' collection?",
    "# {0} - collection name",
    "TASK_clearCollection=removing all documents from ''{0}'' collection",
    "# {0} - index name",
    "TASK_createIndex=creating index ''{0}''"})
final class CollectionNode extends AbstractNode {

    private final IndexNodesFactory childFactory;

    private final CollectionInfo collection;

    CollectionNode(CollectionInfo connection) {
        this(connection, new InstanceContent());
    }

    CollectionNode(CollectionInfo connection, InstanceContent content) {
        this(connection, content, new ProxyLookup(new AbstractLookup(content), Lookups.fixed(connection), connection.getLookup()));
    }

    CollectionNode(final CollectionInfo collection, final InstanceContent content, final Lookup lookup) {
        this(collection, new IndexNodesFactory(lookup), content, lookup);
    }

    CollectionNode(final CollectionInfo collection, IndexNodesFactory childFactory, final InstanceContent content, final Lookup lookup) {
        super(Children.create(childFactory, true), lookup);
        this.collection = collection;
        this.childFactory = childFactory;
        content.add(collection);
        content.add(collection, new CollectionConverter());
        content.add((OpenCookie) () -> {
            if (TopComponentUtils.isNotActivated(CollectionView.class, collection)) {
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
    @SuppressWarnings("unchecked")
    protected Sheet createSheet() {
        Sheet sheet = Sheet.createDefault();
        Sheet.Set set = Sheet.createPropertiesSet();
        MongoDatabase db = getLookup().lookup(MongoDatabase.class);
        BsonDocument commandDocument = new BsonDocument("collStats", new BsonString(collection.getName()));
        try {
            Document result = db.runCommand(commandDocument);
            set.put(new LocalizedProperties(CollectionNode.class).fromDocument(result).toArray());
            sheet.put(set);
        } catch (MongoException ex) {
            if (MongoErrorCode.of(ex) != MongoErrorCode.Unauthorized) {
                DialogNotification.error(ex);
            }
        }
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
        actions.add(new OpenCollectionInNewTabAction());
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
        return actions.toArray(new Action[0]);
    }

    @Override
    public Action getPreferredAction() {
        return SystemAction.get(OpenAction.class);
    }

    public void refreshChildren() {
        childFactory.refresh();
    }

    private class CollectionConverter implements InstanceContent.Convertor<CollectionInfo, MongoCollection> {

        @Override
        public MongoCollection<BsonDocument> convert(CollectionInfo t) {
            MongoDatabase db = getLookup().lookup(MongoDatabase.class);
            return db.getCollection(t.getName(), BsonDocument.class);
        }

        @Override
        public Class<? extends MongoCollection> type(CollectionInfo t) {
            return MongoCollection.class;
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

    public class OpenCollectionInNewTabAction extends AbstractAction {

        public OpenCollectionInNewTabAction() {
            super(Bundle.ACTION_OpenCollectionInNewTab());
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            Lookup lookup = getLookup();
            final CollectionInfo collection = lookup.lookup(CollectionInfo.class);
            CollectionView newTab = new CollectionView(collection, lookup);
            newTab.open();
            newTab.requestActive();
        }
    }

    public class DropCollectionAction extends AbstractAction {

        public DropCollectionAction() {
            super(Bundle.ACTION_DropCollection());
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            final CollectionInfo ci = getLookup().lookup(CollectionInfo.class);
            if (DialogNotification.confirm(Bundle.dropCollectionConfirmText(ci.getName()))) {
                try {
                    getLookup().lookup(MongoCollection.class).drop();
                    ((DBNode) getParentNode()).refreshChildren();
                    for (TopComponent topComponent : TopComponentUtils.findAll(ci, CollectionView.class, MapReduceTopComponent.class)) {
                        topComponent.close();
                    }
                } catch (MongoException ex) {
                    DialogNotification.error(ex);
                }
            }
        }
    }

    public class RenameCollectionAction extends AbstractAction {

        public RenameCollectionAction() {
            super(Bundle.ACTION_RenameCollection());
        }

        @Override
        @SuppressWarnings("unchecked")
        public void actionPerformed(ActionEvent e) {
            try {
                String result = DialogNotification.validatingInput(
                        Bundle.renameCollectionText(collection.getName()),
                        Bundle.ACTION_RenameCollection(),
                        new CollectionNameValidator(getLookup()));
                if (result != null) {
                    String name = result.trim();
                    MongoCollection<BsonDocument> collection = getLookup().lookup(MongoCollection.class);
                    collection.renameCollection(new MongoNamespace(collection.getNamespace().getDatabaseName(), name));
                    final DBNode parentNode = (DBNode) getParentNode();
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
                }
            } catch (MongoException ex) {
                DialogNotification.error(ex);
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
            if (DialogNotification.confirm(Bundle.clearCollectionConfirmText(ci.getName()))) {
                Tasks.create(Bundle.TASK_clearCollection(ci.getName()), new Runnable() {

                    @Override
                    public void run() {
                        try {
                            getLookup().lookup(MongoCollection.class).deleteMany(new BasicDBObject());
                        } catch (MongoException ex) {
                            DialogNotification.error(ex);
                        }
                    }
                }).execute();
            }
        }
    }

    public class CreateIndexAction extends AbstractAction {

        public CreateIndexAction() {
            super(Bundle.ACTION_CreateIndex());
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            Index index = null;
            index = CreateIndexPanel.showDialog(index);
            if (index != null) {
                Tasks.create(
                        Bundle.TASK_createIndex(index.getName()), 
                        new IndexCreation(index)
                ).execute();
            }
        }
    }

    @AllArgsConstructor
    private class IndexCreation implements Runnable {

        Index index;

        @Override
        @SuppressWarnings("unchecked")
        public void run() {
            Document keys = new Document();
            for (Index.Key key : index.getKeys()) {
                keys.append(key.getField(), key.getType().getValue());
            }
            MongoCollection<BsonDocument> collection = getLookup().lookup(MongoCollection.class);
            try {
                collection.createIndex(keys, index.getOptions());
                refreshChildren();
            } catch (MongoException ex) {
                DialogNotification.error(ex);
            }
        }
    };

}
