package org.drools.ruleops.utils.drl2yaml;

import java.io.IOException;

import org.drools.drl.ast.descr.BaseDescr;
import org.drools.drl.ast.descr.PatternDescr;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

public class PatternDescrSerializer extends StdSerializer<PatternDescr> {
    protected PatternDescrSerializer(Class<PatternDescr> clazz) {
        super(clazz);
    }

    public void serialize(PatternDescr o, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();
        jsonGenerator.writeStringField("given", o.getObjectType());
        if (o.getAllBoundIdentifiers().isEmpty()) {
            // do nothing, as expected.
        } else if (o.getAllBoundIdentifiers().size() == 1) {
            jsonGenerator.writeStringField("as", o.getAllBoundIdentifiers().get(0));
        } else {
            jsonGenerator.writeStringField("as", o.getAllBoundIdentifiers().get(0)); // TODO check with Mario the index=0 is always the pattern one
        }
        if (!o.getConstraint().getDescrs().isEmpty()) {
            jsonGenerator.writeArrayFieldStart("having");
            for (BaseDescr i : o.getConstraint().getDescrs()) {                
                jsonGenerator.writeObject(i);
            }
            jsonGenerator.writeEndArray();
        }
        if (o.getSource() != null) {
            jsonGenerator.writeObjectField("from", o.getSource());
        }
        jsonGenerator.writeEndObject();
    }
}