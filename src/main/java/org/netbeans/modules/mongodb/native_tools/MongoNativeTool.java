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

import java.nio.file.Path;
import java.nio.file.Paths;
import org.apache.commons.lang.SystemUtils;
import org.netbeans.modules.mongodb.options.MongoNativeToolsOptions;

/**
 *
 * @author Yann D'Isanto
 */
public enum MongoNativeTool {

    MONGO_SHELL("mongo"),
    MONGO_DUMP("mongodump"),
    MONGO_RESTORE("mongorestore"),
    MONGO_IMPORT("mongoimport"),
    MONGO_EXPORT("mongoexport"),
    MONGO_STAT("mongostat"),
    MONGO_TOP("mongotop"),
    MONGO_PERF("mongoperf"),
    MONGO_FILES("mongofiles"),
    MONGO_OPLOG("mongooplog");

    private final String execBaseName;

    private MongoNativeTool(String execBaseName) {
        this.execBaseName = execBaseName;
    }

    public String getExecBaseName() {
        return execBaseName;
    }

    public String getExecFileName() {
        final StringBuilder sb = new StringBuilder();
        sb.append(execBaseName);
        if (SystemUtils.IS_OS_WINDOWS) {
            sb.append(".exe");
        }
        return sb.toString();
    }

    public Path getExecFullPath() {
        final MongoNativeToolsOptions options = MongoNativeToolsOptions.INSTANCE;
        final String execFolderPath = options.getToolsFolder();
        if (execFolderPath == null) {
            throw new IllegalStateException("mongo tools executables folder not configured");
        }
        return Paths.get(execFolderPath, getExecFileName());
    }

}
