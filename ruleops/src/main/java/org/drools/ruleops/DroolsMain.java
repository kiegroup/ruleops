package org.drools.ruleops;

import java.util.List;

import javax.inject.Inject;

import org.drools.ruleops.model.Advice;

import io.quarkus.runtime.QuarkusApplication;
import io.quarkus.runtime.annotations.QuarkusMain;

@QuarkusMain(name = "cli")
public class DroolsMain implements QuarkusApplication {

    @Inject
    DroolsSingleton drools;

    @Override
    public int run(String... args) throws Exception {
        List<Advice> advices = drools.evaluateAllRulesStateless(args);

        System.out.println("");
        System.out.printf("  🤔 found %d advices by looking into the k8s cluster:", advices.size());
        System.out.println("");

        for (Advice a : advices) {
            System.out.println("");
            System.out.printf("  💡 %s%n", a.title());
            System.out.println("");
            System.out.println(a.description());
        }

        System.out.println("");

        return 0;
    }
}