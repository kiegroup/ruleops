package org.drools.ruleops.utils.drl2yaml;

import java.io.IOException;

import org.drools.drl.ast.descr.MVELExprDescr;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

public class MVELExprDescrSerializer extends StdSerializer<MVELExprDescr> {
    protected MVELExprDescrSerializer(Class<MVELExprDescr> clazz) {
        super(clazz);
    }

    public void serialize(MVELExprDescr o, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeObject(o.getExpression());
    }
}