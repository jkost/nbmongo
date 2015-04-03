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

import com.mongodb.DBObject;
import java.io.File;
import java.nio.charset.Charset;
import java.util.Objects;

/**
 *
 * @author Yann D'Isanto
 */
public final class ExportProperties {

    private final String collection;

    private final DBObject criteria;

    private final DBObject projection;

    private final DBObject sort;

    private final boolean jsonArray;
    
    private final File file;
    
    private final Charset encoding;

    public ExportProperties(String collection, DBObject criteria, DBObject projection, DBObject sort, boolean jsonArray, File file, Charset encoding) {
        this.collection = Objects.requireNonNull(collection);
        this.criteria = criteria;
        this.projection = projection;
        this.sort = sort;
        this.jsonArray = jsonArray;
        this.file = Objects.requireNonNull(file);
        this.encoding = Objects.requireNonNull(encoding);
    }

    public String getCollection() {
        return collection;
    }

    public DBObject getCriteria() {
        return criteria;
    }

    public DBObject getProjection() {
        return projection;
    }

    public DBObject getSort() {
        return sort;
    }

    public boolean isJsonArray() {
        return jsonArray;
    }

    public File getFile() {
        return file;
    }

    public Charset getEncoding() {
        return encoding;
    }
    
}
