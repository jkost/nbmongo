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
public final class CollectionInfo implements Comparable<CollectionInfo> {

    @Getter
    private final String name;

    @Getter
    private final Lookup lookup;

    public CollectionInfo(@NonNull String name, @NonNull Lookup lookup) {
        this.name = name;
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
        if (object instanceof CollectionInfo) {
            final CollectionInfo other = (CollectionInfo) object;
            return name.equals(other.name)
                && Objects.equals(
                    lookup.lookup(DbInfo.class),
                    other.lookup.lookup(DbInfo.class));
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 71 * hash + Objects.hashCode(this.name);
        hash = 71 * hash + Objects.hashCode(this.lookup.lookup(DbInfo.class));
        return hash;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public int compareTo(CollectionInfo o) {
        return name.compareToIgnoreCase(o.name);
    }
}
