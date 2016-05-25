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
package org.netbeans.modules.mongodb.ui.explorer;

import java.util.ArrayList;
import java.util.List;
import org.netbeans.api.db.explorer.node.NodeProvider;
import org.netbeans.api.db.explorer.node.NodeProviderFactory;
import org.openide.nodes.Node;
import org.openide.util.Lookup;

/**
 *
 * @author Yann D'Isanto
 */
public class MongoDBNodeProvider extends NodeProvider {

    // lazy initialization holder class idiom for static fields is used
    // for retrieving the factory
    public static NodeProviderFactory getFactory() {
        return FactoryHolder.FACTORY;
    }

    private static class FactoryHolder {

        static final NodeProviderFactory FACTORY = new NodeProviderFactory() {
            @Override
            public MongoDBNodeProvider createInstance(Lookup lookup) {
                MongoDBNodeProvider provider = new MongoDBNodeProvider(lookup);
                return provider;
            }
        };

    }

    private MongoDBNodeProvider(Lookup lookup) {
        super(lookup);
    }

    @Override
    protected synchronized void initialize() {
        List<Node> newList = new ArrayList<Node>();
        newList.add(MongoServicesNode.getDefault());
        setNodes(newList);
    }

}
