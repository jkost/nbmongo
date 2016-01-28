/* 
 * Copyright (C) 2015 Thomas Werner
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
package org.netbeans.modules.mongodb.ui.actions;

import java.awt.event.ActionEvent;
import java.util.List;
import javax.swing.AbstractAction;
import org.bson.BsonDocument;
import org.netbeans.modules.mongodb.ui.components.aggregation.AggregationTopComponent;
import org.netbeans.modules.mongodb.ui.components.aggregation.PipelinePanel;
import org.openide.util.Lookup;
import org.openide.util.NbBundle.Messages;

/**
 * Opens a {@code MapReduceTopComponent}.
 *
 * @author thomaswerner35
 */
@Messages({
    "ACTION_OpenAggregationPipeline=Aggregation Pipeline"
})
public class OpenAggregationPipelineAction extends AbstractAction {

    private static final long serialVersionUID = 1L;
    
    private final Lookup lookup;
    
    public OpenAggregationPipelineAction(Lookup lookup) {
        super(Bundle.ACTION_OpenAggregationPipeline());
        this.lookup = lookup;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void actionPerformed(ActionEvent e) {
        List<BsonDocument> pipeline = PipelinePanel.showPanel();
        if(pipeline == null) {
            return;
        }
        AggregationTopComponent tc  = new AggregationTopComponent(lookup, pipeline);
        tc.open();
        tc.requestActive();
        
    }

}
