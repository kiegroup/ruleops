package org.drools.ruleops.utils.drl2yaml;

import java.io.IOException;

import org.drools.drl.ast.descr.ImportDescr;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

public class ImportDescrSerializer extends StdSerializer<ImportDescr> {
    protected ImportDescrSerializer(Class<ImportDescr> clazz) {
        super(clazz);
    }

    public void serialize(ImportDescr o, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeString(o.getTarget());
    }
}