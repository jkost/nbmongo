/* 
 * Copyright (C) 2015 Thomas Werner
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
package org.netbeans.modules.mongodb;

import com.mongodb.DBObject;
import com.mongodb.MongoException;
import com.mongodb.util.JSON;

/**
 * Unwrapps a MongoException message.
 *
 * @author thomaswerner35
 */
public class MongoExceptionUnwrapper {

    private final String message;

    public MongoExceptionUnwrapper(Exception ex) {
        if(ex instanceof MongoException) {
            String msgValue = ex.getMessage();
            try {
                DBObject jsonMsg = (DBObject) JSON.parse(ex.getMessage());
                msgValue = (String) jsonMsg.get("err");
                if(msgValue == null || msgValue.isEmpty()) {
                    msgValue = (String) jsonMsg.get("errmsg");
                }
            } catch(Exception ignored) { }
            message = msgValue;
        } else {
            message = ex == null ? null : ex.getMessage();
        }
    }

    @Override
    public String toString() {
        return message;
    }

}
