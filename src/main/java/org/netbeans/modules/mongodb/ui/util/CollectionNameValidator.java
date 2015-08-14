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
package org.netbeans.modules.mongodb.ui.util;

import com.mongodb.MongoException;
import com.mongodb.client.MongoDatabase;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.netbeans.modules.mongodb.api.MongoErrorCode;
import org.openide.util.Lookup;

/**
 *
 * @author Yann D'Isanto
 */
public final class CollectionNameValidator implements ValidatingInputLine.InputValidator {

    private static final String[] forbiddenCharacters = {
        "$", "\u0000"
    };

    private static final String SYSTEM_PREFIX = "system.";

    private final Lookup lookup;
    
    private final Set<String> existingCollections = new HashSet<>();

    public CollectionNameValidator(Lookup lookup) {
        this.lookup = lookup;
        try {
            lookup.lookup(MongoDatabase.class).listCollectionNames().into(existingCollections);
        } catch (MongoException ex) {
            if (MongoErrorCode.of(ex) != MongoErrorCode.Unauthorized) {
                throw ex;
            }
        }
    }

    @Override
    public void validate(String inputText) throws IllegalArgumentException {
        final String value = inputText.trim();
        if (value.isEmpty()) {
            throw new IllegalArgumentException(
                Bundle.VALIDATION_empty());
        }
        if (value.startsWith(SYSTEM_PREFIX)) {
            throw new IllegalArgumentException(
                Bundle.VALIDATION_invalid_prefix(SYSTEM_PREFIX));
        }
        for (String character : forbiddenCharacters) {
            if (value.contains(character)) {
                throw new IllegalArgumentException(
                    Bundle.VALIDATION_forbidden_character(character));
            }
        }
//        if (lookup.lookup(DB.class).getCollectionNames().contains(value)) {
//            throw new IllegalArgumentException(
//                Bundle.VALIDATION_exists("collection", value));
//        }
        if (existingCollections.contains(value)) {
            throw new IllegalArgumentException(
                Bundle.VALIDATION_exists("collection", value));
        }
    }
}
