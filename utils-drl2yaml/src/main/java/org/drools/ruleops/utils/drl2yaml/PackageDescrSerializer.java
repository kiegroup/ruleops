package org.drools.ruleops.utils.drl2yaml;

import java.io.IOException;

import org.drools.drl.ast.descr.FunctionDescr;
import org.drools.drl.ast.descr.ImportDescr;
import org.drools.drl.ast.descr.PackageDescr;
import org.drools.drl.ast.descr.RuleDescr;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

public class PackageDescrSerializer extends StdSerializer<PackageDescr> {
    protected PackageDescrSerializer(Class<PackageDescr> clazz) {
        super(clazz);
    }

    public void serialize(PackageDescr o, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();
        if (o.getName() != null && !(o.getName().isEmpty())) {
            jsonGenerator.writeStringField("package", o.getName());
        }
        if (!o.getImports().isEmpty()) {
            jsonGenerator.writeArrayFieldStart("imports");
            for (ImportDescr i : o.getImports()) {                
                jsonGenerator.writeObject(i);
            }
            jsonGenerator.writeEndArray();
        }
        jsonGenerator.writeArrayFieldStart("rules");
        for (RuleDescr i : o.getRules()) {                
            jsonGenerator.writeObject(i);
        }
        jsonGenerator.writeEndArray();
        if (!o.getFunctions().isEmpty()) {
            jsonGenerator.writeArrayFieldStart("functions");
            for (FunctionDescr i : o.getFunctions()) {                
                jsonGenerator.writeObject(i);
            }
            jsonGenerator.writeEndArray();
        }
        jsonGenerator.writeEndObject();
    }
}