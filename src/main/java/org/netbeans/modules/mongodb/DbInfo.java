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
package org.netbeans.modules.mongodb;

import java.util.Objects;
import lombok.Getter;
import lombok.NonNull;
import org.openide.util.Lookup;

/**
 *
 * @author Tim Boudreau
 * @author Yann D'Isanto
 */
public final class DbInfo implements Comparable<DbInfo> {

    @Getter
    private final Lookup lookup;

    @Getter
    private final String dbName;

    public DbInfo(@NonNull String dbName, @NonNull Lookup lookup) {
        this.dbName = dbName;
        this.lookup = lookup;
    }

    @Override
    public boolean equals(Object object) {
        if (object == null) {
            return false;
        }
        if (object == this) {
            return true;
        }
        if (object instanceof DbInfo) {
            final DbInfo other = (DbInfo) object;
            return dbName.equals(other.dbName)
                && Objects.equals(
                    lookup.lookup(ConnectionInfo.class),
                    other.lookup.lookup(ConnectionInfo.class));
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 71 * hash + Objects.hashCode(this.dbName);
        hash = 71 * hash + Objects.hashCode(this.lookup.lookup(ConnectionInfo.class));
        return hash;
    }

    @Override
    public String toString() {
        return dbName;
    }

    @Override
    public int compareTo(DbInfo o) {
        return toString().compareToIgnoreCase(o.toString());
    }
}
