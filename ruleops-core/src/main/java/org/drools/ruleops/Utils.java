package org.drools.ruleops;

import java.io.IOException;
import java.io.InputStream;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;

import io.fabric8.kubernetes.client.utils.Serialization;

public class Utils {
    private Utils() {
        // only static
    }

    private static final ObjectMapper MAPPER = new ObjectMapper(new YAMLFactory().disable(YAMLGenerator.Feature.USE_NATIVE_TYPE_ID));

    public static <T> T loadYaml(Class<T> clazz, String yaml) {
        try (InputStream is = Utils.class.getResourceAsStream(yaml)) {
            return Serialization.unmarshal(is, clazz);
        } catch (IOException ex) {
            throw new IllegalStateException("Cannot find yaml on classpath: " + yaml);
        }
    }

    public static void debugYaml(Object in) {
        try {
            System.out.println(MAPPER.writeValueAsString(in));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }
}
