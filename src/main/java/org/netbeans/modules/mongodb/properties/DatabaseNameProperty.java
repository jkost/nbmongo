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

import java.lang.reflect.InvocationTargetException;
import org.netbeans.modules.mongodb.DbInfo;
import org.openide.nodes.PropertySupport;
import org.openide.util.Lookup;
import org.openide.util.NbBundle.Messages;

/**
 *
 * @author Tim Boudreau
 * @author Yann D'Isanto
 */
@Messages({
    "LABEL_databaseName=Database Name",
    "DESC_databaseName=The name of the database",
    "EMPTY_databaseName=[no name]"
})
public final class DatabaseNameProperty extends PropertySupport.ReadOnly<String> {

    public static final String KEY = "databaseName";
    
    private final Lookup lkp;

    public DatabaseNameProperty(Lookup lkp) {
        super(KEY, String.class, Bundle.LABEL_databaseName(), Bundle.DESC_databaseName());
        this.lkp = lkp;
    }

    @Override
    public String getValue() throws IllegalAccessException, InvocationTargetException {
        DbInfo info = lkp.lookup(DbInfo.class);
        return info == null ? Bundle.EMPTY_databaseName() : info.getDbName();
    }
}
