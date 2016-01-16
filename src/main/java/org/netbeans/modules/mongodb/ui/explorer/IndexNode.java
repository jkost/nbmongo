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

import com.mongodb.MongoException;
import org.netbeans.modules.mongodb.properties.LocalizedProperties;
import com.mongodb.client.MongoCollection;
import java.awt.event.ActionEvent;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.bson.Document;
import org.netbeans.modules.mongodb.indexes.Index;
import org.netbeans.modules.mongodb.resources.Images;
import org.netbeans.modules.mongodb.ui.util.DialogNotification;
import org.netbeans.modules.mongodb.util.Tasks;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Sheet;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.Task;
import org.openide.util.TaskListener;

/**
 *
 * @author Yann D'Isanto
 */
@NbBundle.Messages({
    "ACTION_dropIndex=Drop index",
    "# {0} - index name",
    "dropIndexConfirmText=Permanently drop ''{0}'' index?",
    "# {0} - index name",
    "TASK_dropIndex=dropping '{0}' index"
})
class IndexNode extends AbstractNode {

    private final IndexKeyNodesFactory childFactory;

    private final Index index;

    IndexNode(Index index, Lookup lookup) {
        this(index, new IndexKeyNodesFactory(lookup, index), lookup);
    }

    private IndexNode(Index index, IndexKeyNodesFactory childFactory, Lookup lookup) {
        super(Children.create(childFactory, true), lookup);
        this.index = index;
        this.childFactory = childFactory;
        setIconBaseWithExtension(Images.KEY_ICON_PATH);
    }

    @Override
    public String getName() {
        return index.getName();
    }

    @Override
    public Action[] getActions(boolean ignored) {
        final List<Action> actions = new LinkedList<>();
        if ("_id_".equals(index.getName()) == false) {
            actions.add(new DropIndexAction());
        }
        final Action[] orig = super.getActions(ignored);
        if (orig.length > 0) {
            actions.add(null);
        }
        actions.addAll(Arrays.asList(orig));
        return actions.toArray(new Action[actions.size()]);
    }

    @Override
    protected Sheet createSheet() {
        Sheet sheet = Sheet.createDefault();
        Sheet.Set set = Sheet.createPropertiesSet();
        
        Index.GlobalOptions globalOptions = index.getGlobalOptions();
        String storageEngine = globalOptions.getStorageEngine() != null 
                ? globalOptions.getStorageEngine().toJson() 
                : null;
        set.put(new LocalizedProperties(IndexNode.class, true)
                .stringProperty("name", index.getName())
                .stringProperty("nameSpace", index.getNameSpace())
                .booleanProperty("sparse", globalOptions.isSparse())
                .booleanProperty("unique", globalOptions.isUnique())
                .longProperty("expireAfterSeconds", globalOptions.getExpireAfterSeconds())
                .intProperty("indexVersion", globalOptions.getIndexVersion())
                .stringProperty("storageEngine", storageEngine)
                .toArray());
        sheet.put(set);

        Index.TextOptions textOptions = index.getTextOptions();
        String weights = textOptions.getWeights() != null 
                ? textOptions.getWeights().toJson() 
                : null;
        Property[] textProperties = new LocalizedProperties(IndexNode.class, true)
                .stringProperty("weights", weights)
                .stringProperty("defaultLanguage", textOptions.getDefaultLanguage())
                .stringProperty("languageOverride", textOptions.getLanguageOverride())
                .intProperty("textVersion", textOptions.getIndexVersion())
                .toArray();
        if(textProperties.length > 0) {
            set = new Sheet.Set();
            set.setName("textOptions");
            set.setDisplayName("Text Options");
            set.put(textProperties);
            sheet.put(set);
        }
        
        Index.Geo2DOptions geo2DOptions = index.getGeo2DOptions();
        Property[] geo2DProperties = new LocalizedProperties(IndexNode.class, true)
                .intProperty("bits", geo2DOptions.getBits())
                .doubleProperty("min", geo2DOptions.getMin())
                .doubleProperty("max", geo2DOptions.getMax())
                .toArray();
        if(geo2DProperties.length > 0) {
            set = new Sheet.Set();
            set.setName("2dOptions");
            set.setDisplayName("2D Options");
            set.put(geo2DProperties);
            sheet.put(set);
        }
        
        Index.Geo2DSphereOptions geo2DSphereOptions = index.getGeo2DSphereOptions();
        Property[] geo2DSphereProperties = new LocalizedProperties(IndexNode.class, true)
                .intProperty("2dsphereVersion", geo2DSphereOptions.getIndexVersion())
                .toArray();
        if(geo2DSphereProperties.length > 0) {
            set = new Sheet.Set();
            set.setName("2dsphereOptions");
            set.setDisplayName("2D Sphere Options");
            set.put(geo2DSphereProperties);
            sheet.put(set);
        }
        
        Index.GeoHaystackOptions geoHaystackOptions = index.getGeoHaystackOptions();
        Property[] geoHaystackProperties = new LocalizedProperties(IndexNode.class, true)
                .doubleProperty("bucketSize", geoHaystackOptions.getBucketSize())
                .toArray();
        if(geoHaystackProperties.length > 0) {
            set = new Sheet.Set();
            set.setName("geoHaystackOptions");
            set.setDisplayName("Geo haystack Options");
            set.put(geoHaystackProperties);
            sheet.put(set);
        }

        return sheet;
    }

    public void refreshChildren() {
        childFactory.refresh();
    }

    private class DropIndexAction extends AbstractAction {

        private static final long serialVersionUID = 1L;

        public DropIndexAction() {
            super(Bundle.ACTION_dropIndex());
        }

        @Override
        @SuppressWarnings("unchecked")
        public void actionPerformed(ActionEvent e) {
            final MongoCollection<Document> collection = getLookup().lookup(MongoCollection.class);
            if (DialogNotification.confirm(Bundle.dropIndexConfirmText(index.getName()))) {
                Tasks.create(Bundle.TASK_dropIndex(index.getName()), new Runnable() {

                    @Override
                    public void run() {
                        try {
                            collection.dropIndex(index.getName());
                        } catch (MongoException ex) {
                            DialogNotification.error(ex);
                        }
                    }
                }).execute().addTaskListener(new TaskListener() {

                    @Override
                    public void taskFinished(Task task) {
                        ((CollectionNode) getParentNode()).refreshChildren();
                    }
                });
            }
        }
    }

}
