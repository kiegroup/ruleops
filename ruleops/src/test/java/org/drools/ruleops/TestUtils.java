package org.drools.ruleops;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.api.model.KubernetesResource;
import io.fabric8.kubernetes.client.KubernetesClient;

public class TestUtils {
    private static final Logger LOG = LoggerFactory.getLogger(TestUtils.class);
    private TestUtils() {
        // only static methods.
    }
    public static InputStream k8sFile(String filename) throws FileNotFoundException {
        final String k8sFilename = "k8s/"+filename;
        LOG.debug("Looking up for file: {}", k8sFilename);
        return new FileInputStream(k8sFilename);
    }

    public static KubernetesResource fromServer(KubernetesClient client, String filename) throws Exception {
        HasMetadata r1 = client.load(k8sFile(filename)).fromServer().get().get(0);
        LOG.debug("fromServer() {}", r1);
        return r1;
    }
}
