package org.drools.ruleops;

import java.util.ArrayList;
import java.util.List;

import org.drools.ruleops.model.Advice;
import org.kie.api.command.Command;
import org.kie.api.runtime.ExecutionResults;
import org.kie.api.runtime.StatelessKieSession;
import org.kie.internal.command.CommandFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RuleOps {

    private static final Logger LOG = LoggerFactory.getLogger(RuleOps.class);

    private LevelTrigger levelTrigger;
    private StatelessKieSession ksession;

    public RuleOps(LevelTrigger levelTrigger, StatelessKieSession kieSession) {
        this.levelTrigger = levelTrigger;
        this.ksession = kieSession;
    }

    public List<Advice> evaluateAllRulesStateless(String... args) {
        List<Command<?>> cmds = new ArrayList<>();

        if (args.length > 0) {
            cmds.add(CommandFactory.newSetGlobal("arg0", args[0]));
        }

        cmds.add(CommandFactory.newInsertElements(levelTrigger.fetchFromKubernetes()));
        cmds.add(CommandFactory.newFireAllRules());

        final String ADVICES = "advices";
        cmds.add(CommandFactory.newGetObjects(Advice.class::isInstance, ADVICES));
        ExecutionResults results = ksession.execute(CommandFactory.newBatchExecution(cmds));
        LOG.debug("Results ids: {}", results.getIdentifiers());
        @SuppressWarnings("unchecked")
        List<Advice> value = (List<Advice>) results.getValue(ADVICES);
        return value;
    }
}
