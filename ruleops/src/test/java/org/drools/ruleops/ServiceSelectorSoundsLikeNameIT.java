package org.drools.ruleops;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.drools.ruleops.TestUtils.cleanupWaitForEmptyK8s;
import static org.drools.ruleops.TestUtils.fromServer;
import static org.drools.ruleops.TestUtils.k8sFile;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import org.drools.ruleops.model.Advice;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.fabric8.kubernetes.api.model.KubernetesResource;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.quarkus.test.junit.QuarkusTest;

// not a: @QuarkusIntegrationTest, this is not to test the result of the build, this is IT with minikube/kind
@QuarkusTest
public class ServiceSelectorSoundsLikeNameIT {
    @Inject
    KubernetesClient client;

    @Inject
    DroolsSingleton drools;

    final String HELLO_PVDF_SERVICE_YML = "hello-pvdf-service.yml";
    final String HELLO_PVDF_SIMILARNAMEPOD_YML = "hello-pvdf-similarNamePod.yml";
    
    @Test
    public void testRules() throws Exception {
        assertThat(drools.evaluateAllRulesStateless())
        .contains(
            new Advice("Service selector hint","Service hello-pvdf selector sounds-like Pod hallo-pvdf, but not an exact match: {app.kubernetes.io/name=hallo-pvdf, app.kubernetes.io/version=1.0.0-SNAPSHOT}"),
            new Advice("Fix the Service selector","Service hello-pvdf no Pod found for selector: {app.kubernetes.io/name=hello-pvdf, app.kubernetes.io/version=1.0.0-SNAPSHOT}"));
    }

    @BeforeEach
    public void k8sClusterConditionsSetup() throws Exception {
        client.load(k8sFile(HELLO_PVDF_SERVICE_YML)).createOrReplace();
        await()
            .atMost(10, TimeUnit.SECONDS)
            .pollInterval(2, TimeUnit.SECONDS)
            .ignoreExceptions()
            .untilAsserted(() -> {
                KubernetesResource r1 = fromServer(client, HELLO_PVDF_SERVICE_YML);
                assertThat(r1).extracting("status").isNotNull();
            });
        client.load(k8sFile(HELLO_PVDF_SIMILARNAMEPOD_YML)).createOrReplace();
        await()
            .atMost(10, TimeUnit.SECONDS)
            .pollInterval(2, TimeUnit.SECONDS)
            .ignoreExceptions()
            .untilAsserted(() -> {
                KubernetesResource r1 = fromServer(client, HELLO_PVDF_SIMILARNAMEPOD_YML);
                assertThat(r1).extracting("status.phase").asString().isEqualTo("Running");
            });
    }

    @AfterEach
    public void cleanup() throws Exception {
        client.load(k8sFile(HELLO_PVDF_SERVICE_YML)).delete();
        client.load(k8sFile(HELLO_PVDF_SIMILARNAMEPOD_YML)).delete();
        cleanupWaitForEmptyK8s(client);
    }

}