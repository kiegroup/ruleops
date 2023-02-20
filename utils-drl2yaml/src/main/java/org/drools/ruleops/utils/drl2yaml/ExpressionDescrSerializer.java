package org.drools.ruleops.utils.drl2yaml;

import java.io.IOException;

import org.drools.drl.ast.descr.ExpressionDescr;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

public class ExpressionDescrSerializer extends StdSerializer<ExpressionDescr> {
    protected ExpressionDescrSerializer(Class<ExpressionDescr> clazz) {
        super(clazz);
    }

    public void serialize(ExpressionDescr o, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeString(o.getExpression());
    }
}