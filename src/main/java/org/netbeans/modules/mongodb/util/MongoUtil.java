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
package org.netbeans.modules.mongodb.util;

import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author Yann D'Isanto
 */
public final class MongoUtil {

    private static final Set<String> SUPPORTED_OPTIONS = new HashSet<>();
    static {
        final Set<String> generalOptionsKeys = new HashSet<>();
        final Set<String> authKeys = new HashSet<>();
        final Set<String> readPreferenceKeys = new HashSet<>();
        final Set<String> writeConcernKeys = new HashSet<>();

        generalOptionsKeys.add("maxpoolsize");
        generalOptionsKeys.add("waitqueuemultiple");
        generalOptionsKeys.add("waitqueuetimeoutms");
        generalOptionsKeys.add("connecttimeoutms");
        generalOptionsKeys.add("sockettimeoutms");
        generalOptionsKeys.add("autoconnectretry");
        generalOptionsKeys.add("ssl");

        readPreferenceKeys.add("slaveok");
        readPreferenceKeys.add("readpreference");
        readPreferenceKeys.add("readpreferencetags");

        writeConcernKeys.add("safe");
        writeConcernKeys.add("w");
        writeConcernKeys.add("wtimeout");
        writeConcernKeys.add("fsync");
        writeConcernKeys.add("j");

        authKeys.add("authmechanism");
        authKeys.add("authsource");

        SUPPORTED_OPTIONS.addAll(generalOptionsKeys);
        SUPPORTED_OPTIONS.addAll(authKeys);
        SUPPORTED_OPTIONS.addAll(readPreferenceKeys);
        SUPPORTED_OPTIONS.addAll(writeConcernKeys);
    }

    public static boolean isSupportedOption(String option) {
        return SUPPORTED_OPTIONS.contains(option.toLowerCase());
    }

    private MongoUtil() {
    }

}
