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

import com.github.jsonj.JsonElement;
import com.github.jsonj.tools.JsonParser;
import com.github.jsonj.tools.JsonSerializer;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;

/**
 *
 * @author Yann D'Isanto
 */
public final class Json {

    private static final JsonParser PARSER = new JsonParser();
    
    
    public static String prettify(DBObject dbObject) {
        return prettify(JSON.serialize(dbObject));
    }
    
    public static String prettify(String json) {
        final JsonElement element = PARSER.parse(json);
        return JsonSerializer.serialize(element, true).replace("\t", "  ").trim();
    }

    private Json() {
    }
}
