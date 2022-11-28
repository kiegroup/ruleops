package org.drools.ruleops;

import java.util.List;

import javax.enterprise.inject.spi.CDI;

import org.jboss.logging.Logger;

import io.fabric8.kubernetes.api.model.Container;
import io.fabric8.kubernetes.api.model.ContainerPort;
import io.fabric8.kubernetes.api.model.Event;
import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.api.model.ListOptions;
import io.fabric8.kubernetes.api.model.PersistentVolumeClaim;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.Service;
import io.fabric8.kubernetes.api.model.ServicePort;
import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.api.model.apps.StatefulSet;
import io.fabric8.kubernetes.client.KubernetesClient;

public class DroolsK8sClient {

    private static final Logger LOGGER = Logger.getLogger(DroolsK8sClient.class);

    private static KubernetesClient getK8sClient() {
        KubernetesClient client = CDI.current().select(KubernetesClient.class).iterator().next();
        return client;
    }

    public static List<Deployment> deployments() {
        LOGGER.debug("invoked deployments()");
        KubernetesClient client = getK8sClient();       
        return client.apps().deployments().list().getItems();
    }

    public static List<StatefulSet> statefulSets() {
        LOGGER.debug("invoked statefulSets()");
        KubernetesClient client = getK8sClient();       
        return client.apps().statefulSets().list().getItems();
    }

    public static List<Event> eventsFor(HasMetadata k8sResource) {
        LOGGER.info("invoked eventsFor()");
        KubernetesClient client = getK8sClient();
        ListOptions lo = new ListOptions();
        lo.setFieldSelector("involvedObject.uid="+k8sResource.getMetadata().getUid());
        return client.v1().events().list(lo).getItems();
    }

    public static List<Pod> pods() {
        LOGGER.debug("invoked pods()");
        KubernetesClient client = getK8sClient();
        return client.pods().list().getItems();
    }

    public static List<PersistentVolumeClaim> persistentVolumeClaims() {
        LOGGER.debug("invoked persistentVolumeClaims()");
        KubernetesClient client = getK8sClient();
        return client.persistentVolumeClaims().list().getItems();
    }

    public static List<Service> services() {
        LOGGER.debug("invoked services()");
        KubernetesClient client = getK8sClient();
        return client.services().list().getItems();
    }
    
    public static void x() {
        ServicePort p = services().get(0).getSpec().getPorts().get(0);
        p.getTargetPort().getIntVal();
        Container c = pods().get(0).getSpec().getContainers().get(0);
        List<ContainerPort> cp = c.getPorts();
        System.out.println(cp);
    }
}