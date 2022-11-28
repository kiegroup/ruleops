package org.drools.ruleops;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
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
public class ServiceTargetPortContainerPortIT {
    @Inject
    KubernetesClient client;

    @Inject
    DroolsSingleton drools;

    final String HELLO_PVDF_SERVICE_YML = "hello-pvdf-service.yml";
    final String HELLO_PVDF_WRONGPORTPOD_YML = "hello-pvdf-wrongPortPod.yml";
    
    @Test
    public void testRules() throws Exception {
        assertThat(drools.evaluateAllRulesStateless())
        .anySatisfy(a -> assertThat(a)
            .hasFieldOrPropertyWithValue("title", "Fix the Service targetPort and the containerPort")
            .extracting(Advice::description).asString()
            .startsWith("Service hello-pvdf targetPort: ")
            .endsWith("not found in any Container of related Pod: hello-pvdf")
        );
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
        client.load(k8sFile(HELLO_PVDF_WRONGPORTPOD_YML)).createOrReplace();
        await()
            .atMost(10, TimeUnit.SECONDS)
            .pollInterval(2, TimeUnit.SECONDS)
            .ignoreExceptions()
            .untilAsserted(() -> {
                KubernetesResource r1 = fromServer(client, HELLO_PVDF_WRONGPORTPOD_YML);
                assertThat(r1).extracting("status.phase").asString().isEqualTo("Running");
            });
    }

    @AfterEach
    public void cleanup() throws Exception {
        client.load(k8sFile(HELLO_PVDF_SERVICE_YML)).delete();
        client.load(k8sFile(HELLO_PVDF_WRONGPORTPOD_YML)).delete();
    }

}