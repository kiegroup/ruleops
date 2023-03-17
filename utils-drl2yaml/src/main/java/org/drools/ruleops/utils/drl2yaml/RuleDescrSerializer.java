package org.drools.ruleops.utils.drl2yaml;

import java.io.IOException;

import org.drools.drl.ast.descr.BaseDescr;
import org.drools.drl.ast.descr.RuleDescr;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

public class RuleDescrSerializer extends StdSerializer<RuleDescr> {
    protected RuleDescrSerializer(Class<RuleDescr> clazz) {
        super(clazz);
    }

    public void serialize(RuleDescr o, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();
        jsonGenerator.writeStringField("name", o.getName());
        jsonGenerator.writeArrayFieldStart("when");
        for (BaseDescr i : o.getLhs().getDescrs()) {                
            jsonGenerator.writeObject(i);
        }
        jsonGenerator.writeEndArray();
        jsonGenerator.writeStringField("then", o.getConsequence().toString());
        jsonGenerator.writeEndObject();
    }
}