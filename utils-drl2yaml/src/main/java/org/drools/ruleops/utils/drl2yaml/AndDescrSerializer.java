package org.drools.ruleops.utils.drl2yaml;

import java.io.IOException;

import org.drools.drl.ast.descr.AndDescr;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

public class AndDescrSerializer extends StdSerializer<AndDescr> {
    protected AndDescrSerializer(Class<AndDescr> clazz) {
        super(clazz);
    }

    public void serialize(AndDescr o, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();
        jsonGenerator.writeObjectField("all", o.getDescrs()); // TODO 'and' revisited as 'all'
        jsonGenerator.writeEndObject();
    }
}