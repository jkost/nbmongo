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
package org.netbeans.modules.mongodb.native_tools;

/**
 *
 * @author Yann D'Isanto
 */
public interface MongoRestoreOptions {

    String HOST = "--host";

    String PORT = "--port";

    String USERNAME = "--username";

    String PASSWORD = "--password";

    String AUTH_DATABASE = "--authenticationDatabase";

    String AUTH_MECHANISM = "--authenticationMechanism";

    String DB = "--db";

    String COLLECTION = "--collection";

    String FILTER = "--filter";

    String DB_PATH = "--dbpath";

    String IPV6 = "--ipv6";

    String SSL = "--ssl";

    String DIRECTORY_PER_DB = "--directoryperdb";

    String JOURNAL = "--journal";

    String OBJCHECK = "--objcheck";

    String NO_OBJCHECK = "--noobjcheck";

    String DROP = "--drop";

    String OPLOG_REPLAY = "--oplogReplay";

    String KEEP_INDEX_VERSION = "--keepIndexVersion";

    String NO_OPTIONS_RESTORE = "--noOptionsRestore";

    String NO_INDEX_RESTORE = "--noIndexRestore";
}
