package org.drools.rhoc;

import java.util.List;

import javax.inject.Inject;

import io.quarkus.runtime.QuarkusApplication;
import io.quarkus.runtime.annotations.QuarkusMain;
import org.drools.ruleops.LevelTrigger;
import org.drools.ruleops.RuleOps;
import org.drools.ruleops.TraceListener;
import org.drools.ruleops.model.Advice;
import org.kie.api.runtime.KieRuntimeBuilder;
import org.kie.api.runtime.StatelessKieSession;

@QuarkusMain(name = "rhocMain")
public class RhocMain implements QuarkusApplication {

    @Inject
    LevelTrigger levelTrigger;

    @Inject
    KieRuntimeBuilder runtimeBuilder;

    RuleOps createRuleOps() {
        StatelessKieSession ksession = runtimeBuilder.getKieBase("rhoc").newStatelessKieSession();
        ksession.addEventListener(new TraceListener());
        return new RuleOps(levelTrigger, ksession);
    }

    @Override
    public int run(String... args) throws Exception {
        // This could be a different main with specific RHOC use cases (such as --help with PicoCli)
        List<Advice> advices = createRuleOps().evaluateAllRulesStateless(args);

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