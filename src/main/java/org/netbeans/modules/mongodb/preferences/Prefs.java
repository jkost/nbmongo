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
package org.netbeans.modules.mongodb.preferences;

import java.util.HashMap;
import java.util.Map;
import java.util.prefs.Preferences;
import org.openide.util.NbPreferences;

/**
 *
 * @author Yann D'Isanto
 */
public enum Prefs {

    INSTANCE;
    
    public static final String REPOSITORIES = "repositories";
    
    private Prefs() {
        Migrations.migrateIfNecessary();
    }
    
    public Preferences root() {
        return NbPreferences.forModule(Prefs.class); //NOI18N
    }
    
    public Version version() {
        String v = root().get("version", Version.UNDEFINED.name());
        return Version.valueOf(v);
    }
    
    public static Preferences of(String node) {
        return INSTANCE.root().node(node);
    }
    
    public static enum Version {
        UNDEFINED,
        V1,
        V2
    }
}
