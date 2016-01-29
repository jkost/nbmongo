/*
 * Copyright (C) 2016 Yann D'Isanto
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
package org.netbeans.modules.mongodb.api.aggregation;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import org.bson.BsonDocument;
import org.bson.BsonValue;
import org.netbeans.modules.mongodb.preferences.Prefs;
import static org.netbeans.modules.mongodb.preferences.Prefs.REPOSITORIES;
import org.netbeans.modules.mongodb.util.Repository.PrefsRepository;

/**
 *
 * @author Yann D'Isanto
 */
public final class AggregationsRepository extends PrefsRepository<Aggregation>{

    public static final String NAME = "aggregations";
    
    public AggregationsRepository() {
        super(Prefs.of(REPOSITORIES).node(NAME));
    }

    
    @Override
    protected Aggregation loadItem(Preferences node) throws BackingStoreException {
        int stagesCount = node.getInt("stagesCount", 0);
        List<PipelineStage> pipeline = new ArrayList<>();
        for(int index = 0; index < stagesCount; index ++) {
            BsonDocument stageDocument = BsonDocument.parse(node.get(String.valueOf(index), null));
            Entry<String, BsonValue> stage = stageDocument.entrySet().iterator().next();
            pipeline.add(new PipelineStage(PipelineStage.Stage.valueOf(stage.getKey()), stage.getValue()));
        }
        return Aggregation.builder().name(node.get("name", null)).pipeline(pipeline).build();
    }

    @Override
    protected void storeItem(Aggregation aggregation, Preferences node) {
        node.put("name", aggregation.getName());
        node.putInt("stagesCount", aggregation.getPipeline().size());
        int index = 0;
        for (PipelineStage stage : aggregation.getPipeline()) {
            node.put(String.valueOf(index ++), stage.toBson().toJson());
        }
    }
}
