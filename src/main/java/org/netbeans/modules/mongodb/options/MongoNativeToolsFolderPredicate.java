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
package org.netbeans.modules.mongodb.options;

import com.mytdev.predicates.AbstractPredicate;
import java.nio.file.Files;
import java.nio.file.Path;
import org.netbeans.modules.mongodb.native_tools.MongoNativeTool;

/**
 *
 * @author Yann D'Isanto
 */
public final class MongoNativeToolsFolderPredicate extends AbstractPredicate<Path> {

    @Override
    public boolean eval(Path path) {
        for (MongoNativeTool tool : MongoNativeTool.values()) {
            final Path toolPath = path.resolve(tool.getExecFileName());
            if (Files.exists(toolPath) == false) {
                return false;
            }
        }
        return true;
    }

}
