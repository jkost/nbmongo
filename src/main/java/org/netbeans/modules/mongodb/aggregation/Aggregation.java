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
package org.netbeans.modules.mongodb.aggregation;

import java.util.List;
import lombok.Builder;
import lombok.Value;
import org.netbeans.modules.mongodb.util.Repository.RepositoryItem;

/**
 *
 * @author Yann D'Isanto
 */
@Value
@Builder
public class Aggregation implements RepositoryItem {
    
    String name;
    
    List<PipelineStage> pipeline;
    
    @Override
    public String toString() {
        return name;
    }

    @Override
    public String getKey() {
        return name;
    }
    
}
