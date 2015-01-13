package de.bfg9000.mongonb.core;

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
