package org.drools.ruleops.basic;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

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

    public static void cleanupWaitForEmptyK8s(KubernetesClient client) throws Exception {
        LOG.debug("checking for empty k8s cluster on test cleanup.");
        await()
        .pollDelay(2, TimeUnit.SECONDS)
        .atMost(30, TimeUnit.SECONDS)
        .pollInterval(2, TimeUnit.SECONDS)
        .ignoreExceptions()
        .untilAsserted(() -> {
            assertThat(client.resourceQuotas().list().getItems() ).as("Expecting empty resourceQuotas on cleanup").isEmpty();
            assertThat(client.pods().list().getItems()).as("Expecting empty pods on cleanup").isEmpty();
            assertThat(client.apps().deployments().list().getItems()).as("Expecting empty deployments on cleanup").isEmpty();
            assertThat(client.apps().statefulSets().list().getItems()).as("Expecting empty statefulSets on cleanup").isEmpty();
            assertThat(client.persistentVolumes().list().getItems()).as("Expecting empty persistentVolumes on cleanup").isEmpty();
            assertThat(client.persistentVolumeClaims().list().getItems()).as("Expecting empty persistentVolumeClaims on cleanup").isEmpty();
        });
    }
}
