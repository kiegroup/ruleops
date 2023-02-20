package org.drools.ruleops.utils.drl2yaml;

import java.io.IOException;

import org.drools.drl.ast.descr.FromDescr;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

public class FromDescrSerializer extends StdSerializer<FromDescr> {
    protected FromDescrSerializer(Class<FromDescr> clazz) {
        super(clazz);
    }

    public void serialize(FromDescr o, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeObject(o.getDataSource());
    }
}