package org.drools.ruleops;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;

import javax.annotation.PostConstruct;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.inject.Singleton;

import io.fabric8.kubernetes.api.model.PersistentVolumeClaim;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.Service;
import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.api.model.apps.StatefulSet;
import org.drools.ruleops.model.Advice;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.kie.api.command.Command;
import org.kie.api.event.rule.DefaultRuleRuntimeEventListener;
import org.kie.api.event.rule.ObjectDeletedEvent;
import org.kie.api.event.rule.ObjectInsertedEvent;
import org.kie.api.event.rule.ObjectUpdatedEvent;
import org.kie.api.runtime.ExecutionResults;
import org.kie.api.runtime.KieRuntimeBuilder;
import org.kie.api.runtime.StatelessKieSession;
import org.kie.internal.command.CommandFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.fabric8.kubernetes.api.model.KubernetesResource;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.StartupEvent;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.infrastructure.Infrastructure;

@Singleton
public class DroolsSingleton {

    private static final Logger LOG = LoggerFactory.getLogger(DroolsSingleton.class);

    @Inject
    KubernetesClient client;

    @Inject
    KieRuntimeBuilder runtimeBuilder;

    private StatelessKieSession ksession;

    @ConfigProperty(name = "ruleops.kiebase")
    String kieBase;

    @PostConstruct
    void onConstructed() {
        LOG.info("@Singleton construction initiated with kieBase {}...", kieBase);
        ksession = runtimeBuilder.getKieBase(kieBase).newStatelessKieSession();
        ksession.addEventListener(new DefaultRuleRuntimeEventListener() {

            @Override
            public void objectDeleted(ObjectDeletedEvent event) {
                LOG.trace("<<< DELETED: {}", event.getOldObject());
            }

            @Override
            public void objectInserted(ObjectInsertedEvent event) {
                LOG.trace(">>> INSERTED: {}", event.getObject());
            }

            @Override
            public void objectUpdated(ObjectUpdatedEvent event) {
                LOG.trace("><> UPDATED: {}", event.getObject());
            }

        });
    }

    void onStart(@Observes StartupEvent ev) {
        LOG.info("The application is starting...");
        for (var d : client.apps().statefulSets().list().getItems()) {
            if (LOG.isDebugEnabled()) {
                Utils.debugYaml(d);
            }
        }
    }

    private Multi<KubernetesResource> mutinyFabric8KubernetesClient(Class<? extends KubernetesResource> resourceType, Function<KubernetesClient,
            List<? extends KubernetesResource>> blockingFn) {

        return Multi.createFrom().<KubernetesResource>items(() -> {
                    var res = blockingFn.apply(client);
                    LOG.debug("Fetched {} {}s", res.size(), resourceType.getName());
                    return res.stream();
                })
                .runSubscriptionOn(Infrastructure.getDefaultWorkerPool());
    }

    public Collection<KubernetesResource> levelTrigger() {
        Multi<KubernetesResource> deployments = mutinyFabric8KubernetesClient(Deployment.class, c -> c.apps().deployments().list().getItems());
        Multi<KubernetesResource> statefulSets = mutinyFabric8KubernetesClient(StatefulSet.class, c -> c.apps().statefulSets().list().getItems());
        Multi<KubernetesResource> pods = mutinyFabric8KubernetesClient(Pod.class, c -> c.pods().inAnyNamespace().list().getItems());
        Multi<KubernetesResource> persistentVolumeClaims = mutinyFabric8KubernetesClient(PersistentVolumeClaim.class, c -> c.persistentVolumeClaims().list().getItems());
        Multi<KubernetesResource> services = mutinyFabric8KubernetesClient(Service.class, c -> c.services().list().getItems());

        return Multi.createBy().merging().streams(deployments, statefulSets, pods, persistentVolumeClaims, services)
                .collect()
                .asList()
                .await()
                .atMost(Duration.ofSeconds(10));
    }

    public List<Advice> evaluateAllRulesStateless(String... args) {
        List<Command<?>> cmds = new ArrayList<>();

        if (args.length > 0) {
            cmds.add(CommandFactory.newSetGlobal("arg0", args[0]));
        }

        cmds.add(CommandFactory.newInsertElements(levelTrigger()));
        cmds.add(CommandFactory.newFireAllRules());

        final String ADVICES = "advices";
        cmds.add(CommandFactory.newGetObjects(Advice.class::isInstance, ADVICES));
        ExecutionResults results = ksession.execute(CommandFactory.newBatchExecution(cmds));
        LOG.debug("Results id: {}", results.getIdentifiers());
        @SuppressWarnings("unchecked")
        List<Advice> value = (List<Advice>) results.getValue(ADVICES);
        return value;
    }

    void onStop(@Observes ShutdownEvent ev) {
        LOG.info("The application is stopping...");
        //evaluateAllRulesStateless();
    }

}
