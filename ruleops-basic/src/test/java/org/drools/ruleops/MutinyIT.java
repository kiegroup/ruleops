package org.drools.ruleops;

import javax.inject.Inject;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;

// not a: @QuarkusIntegrationTest, this is not to test the result of the build, this is IT with minikube/kind
@QuarkusTest
public class MutinyIT {
    @Inject
    DroolsSingleton drools;

    @Test
    public void smokeTestLevelTrigger() {
        drools.levelTrigger();
    }
}
