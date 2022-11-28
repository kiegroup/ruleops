package org.drools.ruleops;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.drools.ruleops.TestUtils.fromServer;
import static org.drools.ruleops.TestUtils.k8sFile;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import org.assertj.core.api.InstanceOfAssertFactories;
import org.drools.ruleops.model.Advice;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.fabric8.kubernetes.api.model.KubernetesResource;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.quarkus.test.junit.QuarkusTest;

// not a: @QuarkusIntegrationTest, this is not to test the result of the build, this is IT with minikube/kind
@QuarkusTest
public class StatefulSetQuotaIT {
    @Inject
    KubernetesClient client;

    @Inject
    DroolsSingleton drools;
    
    final String QUOTA_MEM_CPU = "quota-mem-cpu.yml";
    final String QUOTA_MEM_CPU_POD = "quota-mem-cpu-pod.yml";
    final String QUOTA_POD_DEPLOYMENT_YML = "hello-pvdf-statefulset.yml";

    @Test
    public void testRules() throws Exception {
        assertThat(drools.evaluateAllRulesStateless()).contains(new Advice("Relax the ResourceQuota limits","StatefulSet PENDING: hello-pvdf"));
    }

    @BeforeEach
    public void k8sClusterConditionsSetup() throws Exception {
        client.load(k8sFile(QUOTA_MEM_CPU)).createOrReplace();
        await()
            .atMost(10, TimeUnit.SECONDS)
            .pollInterval(2, TimeUnit.SECONDS)
            .ignoreExceptions()
            .untilAsserted(() -> {
                KubernetesResource r1 = fromServer(client, QUOTA_MEM_CPU);
                assertThat(r1).extracting("status.hard")
                .asInstanceOf(InstanceOfAssertFactories.map(String.class, Object.class))
                .extractingByKey("limits.cpu").asString().isEqualTo("2");
            });
        client.load(k8sFile(QUOTA_MEM_CPU_POD)).createOrReplace();
        await()
            .atMost(10, TimeUnit.SECONDS)
            .pollInterval(2, TimeUnit.SECONDS)
            .ignoreExceptions()
            .untilAsserted(() -> {
                KubernetesResource r1 = fromServer(client, QUOTA_MEM_CPU_POD);
                assertThat(r1).extracting("status.phase").asString().isEqualTo("Running");
            });
        client.load(k8sFile(QUOTA_POD_DEPLOYMENT_YML)).createOrReplace().get(0);
        await()
            .atMost(10, TimeUnit.SECONDS)
            .pollInterval(2, TimeUnit.SECONDS)
            .ignoreExceptions()
            .untilAsserted(() -> {
                KubernetesResource r1 = fromServer(client, QUOTA_POD_DEPLOYMENT_YML);
                assertThat(r1).extracting("status").isNotNull();
            });
    }

    @AfterEach
    public void cleanup() throws Exception {
        client.load(k8sFile(QUOTA_MEM_CPU)).delete();
        client.load(k8sFile(QUOTA_MEM_CPU_POD)).delete();
        client.load(k8sFile(QUOTA_POD_DEPLOYMENT_YML)).delete();
        // NOTE the PVC created by the volumeClaimTemplates is not deleted automatically on statefulset deletion from the cluster; ref https://github.com/kubernetes/kubernetes/issues/55045
        client.persistentVolumeClaims().withName("my-pvc-claim-hello-pvdf-0").delete();
    }
}