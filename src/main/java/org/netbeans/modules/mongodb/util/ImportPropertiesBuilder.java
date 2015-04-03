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

import java.io.File;
import java.nio.charset.Charset;

/**
 *
 * @author Yann D'Isanto
 */
public final class ImportPropertiesBuilder {

    private String collection;

    private boolean drop;

    private File file;

    private Charset encoding;

    public ImportPropertiesBuilder() {
        this(null);
    }

    public ImportPropertiesBuilder(String collection) {
        this.collection = collection;
    }

    public ImportPropertiesBuilder collection(String collection) {
        this.collection = collection;
        return this;
    }

    public ImportPropertiesBuilder drop(boolean drop) {
        this.drop = drop;
        return this;
    }

    public ImportPropertiesBuilder file(File file) {
        this.file = file;
        return this;
    }

    public ImportPropertiesBuilder encoding(Charset encoding) {
        this.encoding = encoding;
        return this;
    }

    public ImportProperties build() {
        return new ImportProperties(collection, drop, file, encoding);
    }
}
