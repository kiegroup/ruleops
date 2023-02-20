package org.drools.ruleops.utils.drl2yaml;

import java.io.IOException;

import org.drools.drl.ast.descr.NotDescr;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

public class NotDescrSerializer extends StdSerializer<NotDescr> {
    protected NotDescrSerializer(Class<NotDescr> clazz) {
        super(clazz);
    }

    public void serialize(NotDescr o, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();
        jsonGenerator.writeObjectField("not", o.getDescrs());
        jsonGenerator.writeEndObject();
    }
}