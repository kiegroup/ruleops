package org.drools.ruleops.utils.drl2yaml;

import java.io.IOException;

import org.drools.drl.ast.descr.FunctionDescr;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

public class FunctionDescrSerializer extends StdSerializer<FunctionDescr> {
    protected FunctionDescrSerializer(Class<FunctionDescr> clazz) {
        super(clazz);
    }

    public void serialize(FunctionDescr o, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();
        jsonGenerator.writeStringField("name", o.getName());
        jsonGenerator.writeStringField("returnType", o.getReturnType());
        jsonGenerator.writeArrayFieldStart("parameters");
        for (int i = 0; i < o.getParameterNames().size(); i++) {
            jsonGenerator.writeStartObject();
            jsonGenerator.writeStringField("name", o.getParameterNames().get(i));
            jsonGenerator.writeStringField("type", o.getParameterTypes().get(i));
            jsonGenerator.writeEndObject();
        }
        jsonGenerator.writeEndArray();
        jsonGenerator.writeStringField("body", o.getBody());
        jsonGenerator.writeEndObject();
    }
}