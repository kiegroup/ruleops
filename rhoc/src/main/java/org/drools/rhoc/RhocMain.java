package org.drools.rhoc;

import java.util.List;

import javax.inject.Inject;

import org.drools.ruleops.DroolsSingleton;
import org.drools.ruleops.model.Advice;

import io.quarkus.runtime.QuarkusApplication;
import io.quarkus.runtime.annotations.QuarkusMain;

@QuarkusMain(name = "rhocMain")
public class RhocMain implements QuarkusApplication {

    @Inject
    DroolsSingleton drools;

    @Override
    public int run(String... args) throws Exception {
        // This could be a different main with specific RHOC use cases (such as --help with PicoCli)
        List<Advice> advices = drools.evaluateAllRulesStateless(args);

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