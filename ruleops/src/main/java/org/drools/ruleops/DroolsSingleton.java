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
                LOG.info("<<< DELETED: {}", event.getOldObject());
            }

            @Override
            public void objectInserted(ObjectInsertedEvent event) {
                LOG.info(">>> INSERTED: {}", event.getObject());
            }

            @Override
            public void objectUpdated(ObjectUpdatedEvent event) {
                LOG.info("><> UPDATED: {}", event.getObject());
            }
            
        });
    }

    void onStart(@Observes StartupEvent ev) {               
        LOG.info("The application is starting...");
        for ( var d : client.apps().statefulSets().list().getItems() ) {
            if (LOG.isDebugEnabled()) {
                Utils.debugYaml(d);
            }
        }
        evaluateAllRulesStateless();
    }

    private Multi<KubernetesResource> mutinyFabric8KubernetesClient(Function<KubernetesClient, List<? extends KubernetesResource>> blockingFn) {
        return Multi.createFrom().<KubernetesResource>items(() -> {
                LOG.debug("using fabric8");
                var res = blockingFn.apply(client);
                LOG.debug("resulted fabric8 ({} results)", res.size());
                return res.stream();})
            .runSubscriptionOn(Infrastructure.getDefaultWorkerPool());
    }

    public Collection<KubernetesResource> levelTrigger() {
        Multi<KubernetesResource> deployments = mutinyFabric8KubernetesClient(c -> c.apps().deployments().list().getItems());
        Multi<KubernetesResource> statefulSets = mutinyFabric8KubernetesClient(c -> c.apps().statefulSets().list().getItems());
        Multi<KubernetesResource> pods = mutinyFabric8KubernetesClient(c -> c.pods().list().getItems());
        Multi<KubernetesResource> persistentVolumeClaims = mutinyFabric8KubernetesClient(c -> c.persistentVolumeClaims().list().getItems());
        Multi<KubernetesResource> services = mutinyFabric8KubernetesClient(c -> c.services().list().getItems());
        List<KubernetesResource> mylist = Multi.createBy().merging().streams(deployments, statefulSets, pods, persistentVolumeClaims, services).collect().asList().await().atMost(Duration.ofSeconds(10));

        // var mylist = Uni.combine().all().unis(
        //     Uni.createFrom().item(() -> { LOG.info("deployments"); var x = client.apps().deployments().list().getItems(); LOG.info("deployments"); return x;}).runSubscriptionOn(Infrastructure.getDefaultWorkerPool()),  
        //     Uni.createFrom().item(() -> { LOG.info("statefulSets"); var x = client.apps().statefulSets().list().getItems(); LOG.info("statefulSets"); return x;}).runSubscriptionOn(Infrastructure.getDefaultWorkerPool()),
        //     Uni.createFrom().item(() -> { LOG.info("pods"); var x = client.pods().list().getItems(); LOG.info("pods"); return x;}).runSubscriptionOn(Infrastructure.getDefaultWorkerPool()),
        //     Uni.createFrom().item(() -> { LOG.info("persistentVolumeClaims"); var x = client.persistentVolumeClaims().list().getItems(); LOG.info("persistentVolumeClaims"); return x;}).runSubscriptionOn(Infrastructure.getDefaultWorkerPool()),
        //     Uni.createFrom().item(() -> { LOG.info("services"); var x = client.services().list().getItems(); LOG.info("services"); return x;}).runSubscriptionOn(Infrastructure.getDefaultWorkerPool())
        // ).usingConcurrencyOf(8).combinedWith(List.class, te -> (List<?>) te.stream().flatMap(List::stream).collect(Collectors.toList())).await().atMost(Duration.ofSeconds(10));
        // LOG.debug("{}", mylist);

        return mylist;
    }

    public List<Advice> evaluateAllRulesStateless() {
        List<Command<?>> cmds = new ArrayList<>();
        cmds.add(CommandFactory.newInsertElements(levelTrigger()));
        cmds.add(CommandFactory.newFireAllRules());
        final String ADVICES = "advices";
        cmds.add(CommandFactory.newGetObjects(Advice.class::isInstance, ADVICES));
        ExecutionResults results = ksession.execute(CommandFactory.newBatchExecution(cmds));
        LOG.debug("{}", results);
        @SuppressWarnings("unchecked")
        List<Advice> value = (List<Advice>) results.getValue(ADVICES);
        return value;
    }

    void onStop(@Observes ShutdownEvent ev) {               
        LOG.info("The application is stopping...");
        //evaluateAllRulesStateless();
    }

}
