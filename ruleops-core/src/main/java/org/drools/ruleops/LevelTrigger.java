package org.drools.ruleops;

import java.time.Duration;
import java.util.Collection;
import java.util.function.Function;

import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.inject.Singleton;

import io.fabric8.kubernetes.api.model.KubernetesResource;
import io.fabric8.kubernetes.api.model.KubernetesResourceList;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.quarkus.runtime.StartupEvent;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.infrastructure.Infrastructure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class LevelTrigger {

    private static final Logger LOG = LoggerFactory.getLogger(LevelTrigger.class);

    @Inject
    KubernetesClient client;

    public LevelTrigger(KubernetesClient client) {
        this.client = client;
    }

    void onStart(@Observes StartupEvent ev) {
        LOG.info("The application is starting...");
        for (var d : client.apps().statefulSets().list().getItems()) {
            if (LOG.isDebugEnabled()) {
                Utils.debugYaml(d);
            }
        }
    }

    private Multi<KubernetesResource> mutinyFabric8KubernetesClient(Function<KubernetesClient, KubernetesResourceList<? extends KubernetesResource>> blockingFn) {
        return Multi.createFrom().<KubernetesResource>items(() -> {
                    var get = blockingFn.apply(client);
                    var res = get.getItems();
                    LOG.debug("Fetched {} items from {}", res.size(), get.getClass().getSimpleName());
                    return res.stream();
                })
                .runSubscriptionOn(Infrastructure.getDefaultWorkerPool());
    }

    public Collection<KubernetesResource> fetchFromKubernetes() {
        Multi<KubernetesResource> deployments = mutinyFabric8KubernetesClient(c -> c.apps().deployments().list());
        Multi<KubernetesResource> statefulSets = mutinyFabric8KubernetesClient(c -> c.apps().statefulSets().list());
        Multi<KubernetesResource> pods = mutinyFabric8KubernetesClient(c -> c.pods().inAnyNamespace().list());
        Multi<KubernetesResource> persistentVolumeClaims = mutinyFabric8KubernetesClient(c -> c.persistentVolumeClaims().list());
        Multi<KubernetesResource> services = mutinyFabric8KubernetesClient(c -> c.services().list());
        Multi<KubernetesResource> configMaps = mutinyFabric8KubernetesClient(c -> c.configMaps().inAnyNamespace().list());

        return Multi.createBy().merging().streams(deployments, statefulSets, pods, persistentVolumeClaims, services, configMaps)
                .collect()
                .asList()
                .await()
                .atMost(Duration.ofSeconds(10));
    }
}
