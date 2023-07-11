package org.drools.ruleops.basic;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.drools.ruleops.basic.TestUtils.cleanupWaitForEmptyK8s;
import static org.drools.ruleops.basic.TestUtils.fromServer;
import static org.drools.ruleops.basic.TestUtils.k8sFile;

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
public class DeploymentQuotaIT {
    @Inject
    KubernetesClient client;

    @Inject
    DroolsSingleton drools;

    final String QUOTA_POD_YML = "quota-pod.yml";
    final String QUOTA_POD_DEPLOYMENT_YML = "quota-pod-deployment.yml";
    
    @Test
    public void testRules() throws Exception {
        assertThat(drools.evaluateAllRulesStateless()).contains(new Advice("Relax the ResourceQuota limits","Deployment PENDING: pod-quota-demo"));
    }

    @BeforeEach
    public void k8sClusterConditionsSetup() throws Exception {
        client.load(k8sFile(QUOTA_POD_YML)).createOrReplace();
        await()
            .atMost(10, TimeUnit.SECONDS)
            .pollInterval(2, TimeUnit.SECONDS)
            .ignoreExceptions()
            .untilAsserted(() -> {
                KubernetesResource r1 = fromServer(client, QUOTA_POD_YML);
                assertThat(r1).extracting("status.hard.pods").asString().isEqualTo("2");
            });
        client.load(k8sFile(QUOTA_POD_DEPLOYMENT_YML)).createOrReplace().get(0);
        await()
            .atMost(10, TimeUnit.SECONDS)
            .pollInterval(2, TimeUnit.SECONDS)
            .ignoreExceptions()
            .untilAsserted(() -> {
                KubernetesResource r1 = fromServer(client, QUOTA_POD_DEPLOYMENT_YML);
                assertThat(r1).extracting("status.conditions").asList().isNotEmpty();
            });
    }

    @AfterEach
    public void cleanup() throws Exception {
        client.load(k8sFile(QUOTA_POD_YML)).delete();
        client.load(k8sFile(QUOTA_POD_DEPLOYMENT_YML)).delete();
        cleanupWaitForEmptyK8s(client);
    }

}