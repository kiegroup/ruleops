package org.drools.ruleops;

import java.util.List;

import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.inject.Singleton;

import io.quarkus.runtime.ShutdownEvent;
import org.drools.ruleops.model.Advice;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.kie.api.runtime.KieRuntimeBuilder;
import org.kie.api.runtime.StatelessKieSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class DroolsSingleton {

    private static final Logger LOG = LoggerFactory.getLogger(DroolsSingleton.class);

    @Inject
    KieRuntimeBuilder runtimeBuilder;

    @ConfigProperty(name = "ruleops.kiebase")
    String kieBase;

    @Inject
    LevelTrigger levelTrigger;

    public StatelessKieSession createKieSession() {
        LOG.info("@Singleton construction initiated with kieBase {}...", kieBase);
        StatelessKieSession ksession = runtimeBuilder.getKieBase(kieBase).newStatelessKieSession();
        ksession.addEventListener(new TraceListener());
        return ksession;
    }

    public List<Advice> evaluateAllRulesStateless(String... args) {
        StatelessKieSession kieSession = createKieSession();
        return new RuleOps(levelTrigger, kieSession).evaluateAllRulesStateless(args);
    }

    void onStop(@Observes ShutdownEvent ev) {
        LOG.info("The application is stopping...");
        //evaluateAllRulesStateless();
    }
}
