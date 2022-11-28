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
public class PodPendingForPVCPendingIT {
    @Inject
    KubernetesClient client;

    @Inject
    DroolsSingleton drools;

    final String PV_CLAIM_YML = "pv-claim.yml";
    final String PVC_DEPLOYMENT_YML = "pvc-deployment.yml";
    
    @Test
    public void testRules() throws Exception {
        assertThat(drools.evaluateAllRulesStateless())
            .filteredOn(Advice::title, "Fix the PersistentVolume")
            .map(Advice::description)
            .anyMatch(m -> m.matches("Pod PENDING: pod-quota-demo-(.*) pvc PENDING: task-pv-claim"));
    }

    @BeforeEach
    public void k8sClusterConditionsSetup() throws Exception {
        // this creates the pvc without the pv, so the pvc will hold in Pending state, as needed for test
        client.load(k8sFile(PV_CLAIM_YML)).createOrReplace();
        await()
            .atMost(10, TimeUnit.SECONDS)
            .pollInterval(2, TimeUnit.SECONDS)
            .ignoreExceptions()
            .untilAsserted(() -> {
                KubernetesResource r1 = fromServer(client, PV_CLAIM_YML);
                assertThat(r1).extracting("status.phase").asString().isEqualTo("Pending");
            });
        client.load(k8sFile(PVC_DEPLOYMENT_YML)).createOrReplace().get(0);
        await()
            .atMost(10, TimeUnit.SECONDS)
            .pollInterval(2, TimeUnit.SECONDS)
            .ignoreExceptions()
            .untilAsserted(() -> {
                KubernetesResource r1 = fromServer(client, PVC_DEPLOYMENT_YML);
                assertThat(r1).extracting("status.conditions").asList().isNotEmpty();
            });
    }

    @AfterEach
    public void cleanup() throws Exception {
        client.load(k8sFile(PV_CLAIM_YML)).delete();
        client.load(k8sFile(PVC_DEPLOYMENT_YML)).delete();
    }

}