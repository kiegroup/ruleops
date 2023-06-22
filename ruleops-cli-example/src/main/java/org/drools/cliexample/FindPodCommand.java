package org.drools.cliexample;

import java.util.List;

import javax.inject.Inject;

import org.drools.ruleops.LevelTrigger;
import org.drools.ruleops.RuleOps;
import org.drools.ruleops.TraceListener;
import org.drools.ruleops.model.Advice;
import org.kie.api.runtime.KieRuntimeBuilder;
import org.kie.api.runtime.StatelessKieSession;
import picocli.CommandLine;
@CommandLine.Command(name = "pod", description = "Find a pod with a certain name")
public class FindPodCommand implements Runnable {

    @CommandLine.Parameters(index = "0", description = "The Pod name to search for")
    String name;

    @Inject
    LevelTrigger levelTrigger;

    @Inject
    KieRuntimeBuilder runtimeBuilder;

    RuleOps createRuleOps() {
        StatelessKieSession ksession = runtimeBuilder.getKieBase("findpod").newStatelessKieSession();
        ksession.addEventListener(new TraceListener());
        return new RuleOps(levelTrigger, ksession);
    }

    @Override
    public void run() {
        List<Advice> advices = createRuleOps().evaluateAllRulesStateless(name);

        for (Advice a : advices) {
            System.out.println("");
            System.out.printf("  💡 %s%n", a.title());
            System.out.println("");
            System.out.println(a.description());
        }

        System.out.println("");
    }
}