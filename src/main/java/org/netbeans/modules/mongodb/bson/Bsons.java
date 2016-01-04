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
package org.netbeans.modules.mongodb.bson;

import java.io.StringWriter;
import java.io.Writer;
import java.util.EnumMap;
import java.util.Map;
import org.bson.BsonBinary;
import org.bson.BsonDbPointer;
import org.bson.BsonRegularExpression;
import org.bson.BsonTimestamp;
import org.bson.BsonType;
import org.bson.BsonValue;
import org.bson.codecs.BsonArrayCodec;
import org.bson.codecs.BsonBinaryCodec;
import org.bson.codecs.BsonBooleanCodec;
import org.bson.codecs.BsonDBPointerCodec;
import org.bson.codecs.BsonDateTimeCodec;
import org.bson.codecs.BsonDocumentCodec;
import org.bson.codecs.BsonDoubleCodec;
import org.bson.codecs.BsonInt32Codec;
import org.bson.codecs.BsonInt64Codec;
import org.bson.codecs.BsonJavaScriptCodec;
import org.bson.codecs.BsonJavaScriptWithScopeCodec;
import org.bson.codecs.BsonMaxKeyCodec;
import org.bson.codecs.BsonMinKeyCodec;
import org.bson.codecs.BsonNullCodec;
import org.bson.codecs.BsonObjectIdCodec;
import org.bson.codecs.BsonRegularExpressionCodec;
import org.bson.codecs.BsonStringCodec;
import org.bson.codecs.BsonSymbolCodec;
import org.bson.codecs.BsonTimestampCodec;
import org.bson.codecs.BsonUndefinedCodec;
import org.bson.codecs.BsonValueCodec;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.bson.json.JsonMode;
import org.bson.json.JsonReader;
import org.bson.json.JsonWriter;
import org.bson.json.JsonWriterSettings;
import org.bson.types.ObjectId;

/**
 *
 * @author Yann D'Isanto
 */
public final class Bsons {

    private static final JsonWriterSettings shell = new JsonWriterSettings(JsonMode.SHELL, false);
    private static final JsonWriterSettings shellAndPretty = new JsonWriterSettings(JsonMode.SHELL, "  ", "\n");
    private static final JsonWriterSettings strict = new JsonWriterSettings(JsonMode.STRICT, false);
    private static final JsonWriterSettings strictAndPretty = new JsonWriterSettings(JsonMode.STRICT, "  ", "\n");

    public static String shell(BsonValue value) {
        return toJson(value, shell);
    }

    public static String shellAndPretty(BsonValue value) {
        return toJson(value, shellAndPretty);
    }

    public static String strict(BsonValue value) {
        return toJson(value, strict);
    }

    public static String strictAndPretty(BsonValue value) {
        return toJson(value, strictAndPretty);
    }

    public static String toJson(BsonValue value, final JsonWriterSettings settings) {
        if(value.isDocument()) {
            return value.asDocument().toJson(settings);
        }
        StringWriter writer = new StringWriter();
        getCodec(value).encode(new BsonValueJsonWriter(writer, settings), value, EncoderContext.builder().build());
        return writer.toString();
    }

    @SuppressWarnings("unchecked")    
    public static <T extends BsonValue> T fromJson(String json, BsonType type) {
        return (T) getCodec(type).decode(new JsonReader(json), DecoderContext.builder().build());
    }

    @SuppressWarnings("unchecked")
    private static Codec<BsonValue> getCodec(BsonValue value) {
        Codec<BsonValue> codec = getCodec(value.getBsonType());
        return codec != null ? codec : defaultCodec;
    }

    @SuppressWarnings("unchecked")
    public static <T extends BsonValue> Codec<T> getCodec(BsonType type) {
        return (Codec<T>) codecs.get(type);
    }
    
    private static final Codec<BsonValue> defaultCodec = new BsonValueCodec();
    private static final Map<BsonType, Codec<? extends BsonValue>> codecs;

    static {
        BsonDocumentCodec documentCodec = new BsonDocumentCodec();
        codecs = new EnumMap<>(BsonType.class);
        codecs.put(BsonType.ARRAY, new BsonArrayCodec(documentCodec.getCodecRegistry()));
        codecs.put(BsonType.BINARY, new BsonBinaryCodec());
        codecs.put(BsonType.BOOLEAN, new BsonBooleanCodec());
        codecs.put(BsonType.DATE_TIME, new BsonDateTimeCodec());
        codecs.put(BsonType.DB_POINTER, new BsonDBPointerCodec());
        codecs.put(BsonType.DOCUMENT, documentCodec);
        codecs.put(BsonType.DOUBLE, new BsonDoubleCodec());
        codecs.put(BsonType.INT32, new BsonInt32Codec());
        codecs.put(BsonType.INT64, new BsonInt64Codec());
        codecs.put(BsonType.JAVASCRIPT, new BsonJavaScriptCodec());
        codecs.put(BsonType.JAVASCRIPT_WITH_SCOPE, new BsonJavaScriptWithScopeCodec(documentCodec));
        codecs.put(BsonType.MAX_KEY, new BsonMaxKeyCodec());
        codecs.put(BsonType.MIN_KEY, new BsonMinKeyCodec());
        codecs.put(BsonType.NULL, new BsonNullCodec());
        codecs.put(BsonType.OBJECT_ID, new BsonObjectIdCodec());
        codecs.put(BsonType.REGULAR_EXPRESSION, new BsonRegularExpressionCodec());
        codecs.put(BsonType.STRING, new BsonStringCodec());
        codecs.put(BsonType.SYMBOL, new BsonSymbolCodec());
        codecs.put(BsonType.TIMESTAMP, new BsonTimestampCodec());
        codecs.put(BsonType.UNDEFINED, new BsonUndefinedCodec());

    }

    private Bsons() {
    }

    static class BsonValueJsonWriter extends JsonWriter {

        public BsonValueJsonWriter(Writer writer, JsonWriterSettings settings) {
            super(writer, settings);
        }

        @Override
        public void writeBinaryData(BsonBinary binary) {
            doWriteBinaryData(binary);
        }

        @Override
        public void writeBoolean(boolean value) {
            doWriteBoolean(value);
        }

        @Override
        public void writeDBPointer(BsonDbPointer value) {
            doWriteDBPointer(value);
        }

        @Override
        public void writeDateTime(long value) {
            doWriteDateTime(value);
        }

        @Override
        public void writeDouble(double value) {
            doWriteDouble(value);
        }

        @Override
        public void writeEndArray() {
            doWriteEndArray();
        }

        @Override
        public void writeEndDocument() {
            doWriteEndDocument();
        }

        @Override
        public void writeInt32(int value) {
            doWriteInt32(value);
        }

        @Override
        public void writeInt64(long value) {
            doWriteInt64(value);
        }

        @Override
        public void writeJavaScript(String code) {
            doWriteJavaScript(code);
        }

        @Override
        public void writeJavaScriptWithScope(String code) {
            doWriteJavaScriptWithScope(code);
        }

        @Override
        public void writeMaxKey() {
            doWriteMaxKey();
        }

        @Override
        public void writeMinKey() {
            doWriteMinKey();
        }

        @Override
        public void writeNull() {
            doWriteNull();
        }

        @Override
        public void writeObjectId(ObjectId objectId) {
            doWriteObjectId(objectId);
        }

        @Override
        public void writeRegularExpression(BsonRegularExpression regularExpression) {
            doWriteRegularExpression(regularExpression);
        }

        @Override
        public void writeStartArray() {
            doWriteStartArray();
        }

        @Override
        public void writeStartDocument() {
            doWriteStartDocument();
        }

        @Override
        public void writeString(String value) {
            doWriteString(value);
        }

        @Override
        public void writeSymbol(String value) {
            doWriteSymbol(value);
        }

        @Override
        public void writeTimestamp(BsonTimestamp value) {
            doWriteTimestamp(value);
        }

        @Override
        public void writeUndefined() {
            doWriteUndefined();
        }

    }
}
