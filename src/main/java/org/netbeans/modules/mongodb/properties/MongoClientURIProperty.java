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
package org.netbeans.modules.mongodb.properties;

import com.mongodb.MongoClientURI;
import java.beans.PropertyEditor;
import java.lang.reflect.InvocationTargetException;
import org.netbeans.modules.mongodb.api.connections.ConnectionInfo;
import org.openide.nodes.PropertySupport;
import org.openide.util.Lookup;
import org.openide.util.NbBundle.Messages;

/**
 *
 * @author Yann D'Isanto
 */
@Messages("LABEL_mongoURI=Mongo URI")
public final class MongoClientURIProperty extends PropertySupport.ReadOnly<MongoClientURI> {

    public static final String KEY = "mongoURI";
    
    private final Lookup lkp;

    public MongoClientURIProperty(Lookup lkp) {
        super(KEY, MongoClientURI.class, displayName(), null);
        this.lkp = lkp;
    }

    @Override
    public MongoClientURI getValue() throws IllegalAccessException, InvocationTargetException {
        return lkp.lookup(ConnectionInfo.class).getMongoURI();
    }
    
    public static String displayName() {
        return Bundle.LABEL_mongoURI();
    } 

    @Override
    public PropertyEditor getPropertyEditor() {
        return new MongoClientURIPropertyEditor(false);
    }
    
    
}
