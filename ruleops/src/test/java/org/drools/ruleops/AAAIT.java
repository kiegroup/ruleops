package org.drools.ruleops;

import static org.assertj.core.api.Assertions.assertThat;

import javax.inject.Inject;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestWatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.fabric8.kubernetes.client.KubernetesClient;
import io.quarkus.test.junit.QuarkusTest;

// not a: @QuarkusIntegrationTest, this is not to test the result of the build, this is IT with minikube/kind
@QuarkusTest
@ExtendWith(AAAIT.FailFast.class) // skip after failure count -> N for Junit5 in https://maven.apache.org/surefire/maven-surefire-plugin/featurematrix.html
public class AAAIT {

    private static final Logger LOG = LoggerFactory.getLogger(AAAIT.class);

    @Inject
    KubernetesClient client;
    
    @Test
    public void testRules() {
        assertThat(client.resourceQuotas().list().getItems() ).as("Expecting empty resourceQuotas k8s cluster for integration tests").isEmpty();
        assertThat(client.pods().list().getItems()).as("Expecting empty pods k8s cluster for integration tests").isEmpty();
        assertThat(client.apps().deployments().list().getItems()).as("Expecting empty deployments k8s cluster for integration tests").isEmpty();
        assertThat(client.apps().statefulSets().list().getItems()).as("Expecting empty statefulSets k8s cluster for integration tests").isEmpty();
        assertThat(client.persistentVolumes().list().getItems()).as("Expecting empty persistentVolumes k8s cluster for integration tests").isEmpty();
        assertThat(client.persistentVolumeClaims().list().getItems()).as("Expecting empty persistentVolumeClaims k8s cluster for integration tests").isEmpty();
    }

    public static class FailFast implements TestWatcher {
        @Override
        public void testFailed(ExtensionContext context, Throwable cause) {
            LOG.error("Expecting empty k8s cluster for integration tests, FAILING FAST BRUTALLY.", cause);
            System.err.println("Expecting empty k8s cluster for integration tests, FAILING FAST BRUTALLY.");
            System.exit(-1);
        }
    }
}