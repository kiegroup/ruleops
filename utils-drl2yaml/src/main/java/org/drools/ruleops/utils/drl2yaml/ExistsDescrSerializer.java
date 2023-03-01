package org.drools.ruleops.utils.drl2yaml;

import java.io.IOException;

import org.drools.drl.ast.descr.ExistsDescr;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

public class ExistsDescrSerializer extends StdSerializer<ExistsDescr> {
    protected ExistsDescrSerializer(Class<ExistsDescr> clazz) {
        super(clazz);
    }

    public void serialize(ExistsDescr o, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();
        jsonGenerator.writeObjectField("exists", o.getDescrs());
        jsonGenerator.writeEndObject();
    }
}