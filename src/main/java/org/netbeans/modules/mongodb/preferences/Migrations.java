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
import lombok.AllArgsConstructor;
import org.netbeans.modules.mongodb.NBMongo;
import org.openide.util.Exceptions;

/**
 *
 * @author Yann D'Isanto
 */
final class Migrations {

    private static final Map<String, Prefs.Version> VERSIONS;

    static {
        VERSIONS = new HashMap<>();
        VERSIONS.put("8.1.0", Prefs.Version.V1);
        VERSIONS.put("8.1.1", Prefs.Version.V1);
        VERSIONS.put("8.1.2", Prefs.Version.V1);
        VERSIONS.put("8.1.3", Prefs.Version.V1);
        VERSIONS.put("8.1.4", Prefs.Version.V1);
        VERSIONS.put("8.2.0", Prefs.Version.V1);
        VERSIONS.put("8.3.0", Prefs.Version.V2);
    }

    static final Prefs.Version PREFS_EXPECTED_VERSION = VERSIONS.get(NBMongo.moduleInfo().getImplementationVersion());

    static final Migration V1_TO_V2 = new Migration(Prefs.Version.V2, null) {

        @Override
        protected void performMigration(Preferences prefs) throws Exception {
            // TODO: update connections prefs (when repository is used)
        }

    };

    static final Migration DEFAULT = new Migration(PREFS_EXPECTED_VERSION, null) {
        @Override
        protected void performMigration(Preferences prefs) throws Exception {
            // DO NOTHING
            // It will simply update the preferences 'version' property.
        }
    };

    private static final Map<Prefs.Version, Migration> MIGRATIONS;

    static {
        MIGRATIONS = new HashMap<>();
        MIGRATIONS.put(Prefs.Version.UNDEFINED, DEFAULT);
        MIGRATIONS.put(Prefs.Version.V1, V1_TO_V2);
        MIGRATIONS.put(Prefs.Version.V2, DEFAULT);
    }

    public static void migrateIfNecessary() {
        if (PREFS_EXPECTED_VERSION == null) {
            return;
        };
        Prefs.Version actualVersion = NBMongo.prefs().version();
        if (actualVersion == PREFS_EXPECTED_VERSION) {
            return;
        }
        MIGRATIONS.get(actualVersion).run();
    }

    @AllArgsConstructor
    static abstract class Migration implements Runnable {

        private final Prefs.Version version;

        private final Migration next;

        @Override
        public void run() {
            try {
                Preferences prefs = NBMongo.prefs().root();
                performMigration(prefs);
                prefs.put("version", version.name());
                if (next != null) {
                    next.run();
                }
            } catch (Exception ex) {
                Exceptions.printStackTrace(ex);
            }
        }

        protected abstract void performMigration(Preferences prefs) throws Exception;

    }

}
