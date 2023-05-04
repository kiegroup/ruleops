package org.drools.ruleops;

import java.util.List;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.Alternative;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.Produces;

import io.quarkus.runtime.ShutdownEvent;
import org.drools.ruleops.model.Advice;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.kie.api.runtime.KieRuntimeBuilder;
import org.kie.api.runtime.StatelessKieSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
@Alternative
public class DroolsSingleton {

    private static final Logger LOG = LoggerFactory.getLogger(DroolsSingleton.class);

    @Inject
    KieRuntimeBuilder runtimeBuilder;

    @Inject
    RuleOps ruleOps;

    @ConfigProperty(name = "ruleops.kiebase")
    String kieBase;

    @Produces
    public StatelessKieSession createKieSession() {
        LOG.info("@Singleton construction initiated with kieBase {}...", kieBase);
        StatelessKieSession ksession = runtimeBuilder.getKieBase(kieBase).newStatelessKieSession();
        ksession.addEventListener(new TraceListener());
        return ksession;
    }

    /***
     * Tests are still calling this. Use RuleOps.evaluateAllRulesStateless instead
     */
    @Deprecated()
    public List<Advice> evaluateAllRulesStateless(String... args) {
        return ruleOps.evaluateAllRulesStateless(args);
    }

    void onStop(@Observes ShutdownEvent ev) {
        LOG.info("The application is stopping...");
        //evaluateAllRulesStateless();
    }
}
