package org.drools.ruleops.utils.drl2yaml;

import org.junit.jupiter.api.Test;

public class GeneratorTest {
    @Test
    public void testSmoke1() throws Exception {
        final var input = """
            package ciao;
            import java.lang.Number;
            import java.lang.Long;
            
            rule R1
            when
              $p : Person( age >= 18 )
            then
                System.out.println("hello");
                System.out.println("world");
                System.out.println("again");
            end
            """;
        System.out.println(input.replaceAll(" ", "_"));
        var output = Generator.convert(input); // noted that RHS for the RuleDescr trims the initial WS
        System.out.println(output);
    }

    @Test
    public void testSmoke2() throws Exception {
        final var input = """
            rule "Relax the ResourceQuota limits Deployment PENDING"
            when
              $d : Deployment()
              DeploymentCondition(type == "Available", status == "False") from $d.status.conditions
              DeploymentCondition(message contains "exceeded quota") from $d.status.conditions
            then
              insert(new Advice("Relax the ResourceQuota limits","Deployment PENDING: "+$d.getMetadata().getName()));
            end
            """;
        var output = Generator.convert(input);
        System.out.println(output);
    }

    @Test
    public void testSmoke3() throws Exception {
        final var input = """
            rule "Relax the ResourceQuota limits StatefulSet PENDING"
            when
              $s : StatefulSet( spec.replicas != status.replicas )
              Event(message contains "exceeded quota") from DroolsK8sClient.eventsFor($s)
            then
              insert(new Advice("Relax the ResourceQuota limits","StatefulSet PENDING: "+$s.getMetadata().getName()));
            end
            """;
        var output = Generator.convert(input);
        System.out.println(output);
    }

    @Test
    public void testSmoke4() throws Exception {
        final var input = """
            rule "Fix the PersistentVolume Claim Pod PENDING"
            when
              $pvc : PersistentVolumeClaim( status.phase == "Pending" )
              $pod : Pod( status.phase == "Pending" )
              Volume( persistentVolumeClaim!.claimName == $pvc.metadata.name ) from $pod.spec.volumes
            then
              insert(new Advice("Fix the PersistentVolume","Pod PENDING: "+$pod.getMetadata().getName() + " pvc PENDING: "+$pvc.getMetadata().getName()));
            end
            """;
        var output = Generator.convert(input);
        System.out.println(output);
    }

    @Test
    public void testSmoke5() throws Exception {
        final var input = """
            function boolean mapContains(Map left, Map right) {
                if (left == null) {
                  return right == null;
                }
                if (right == null) {
                  return false;
                }
                return left.entrySet().containsAll(right.entrySet());
              }
            """;
        var output = Generator.convert(input);
        System.out.println(output);
    }

    @Test
    public void testSmoke6() throws Exception {
        final var input = """
            rule "Fix the Service selector No Pod found for selector"
            when
              $svc : Service( metadata.name != "kubernetes" )
              not( Pod( mapContains(metadata.labels, $svc.spec.selector) ) )
            then
              insert(new Advice("Fix the Service selector","Service "+$svc.getMetadata().getName()+" no Pod found for selector: "+$svc.getSpec().getSelector()));
            end
            """;
        var output = Generator.convert(input);
        System.out.println(output);
    }

    @Test
    public void testSmoke7() throws Exception {
        final var input = """
            rule "Fix the Service selector matches Pod name, but other selectors don't"
            when
              $svc : Service( metadata.name != "kubernetes", $selectorName: spec!.selector["app.kubernetes.io/name"] )
              $pod : Pod( metadata.labels["app.kubernetes.io/name"] == $selectorName, !mapContains(metadata.labels, $svc.spec.selector) )
            then
              insert(new Advice("Fix the Service selector","Service "+$svc.getMetadata().getName()+" selector matches Pod name, but other selectors: '"+DroolsUtils.notFoundMap($svc.getSpec().getSelector(), $pod.getMetadata().getLabels())+"' did not match in the candidate Pod: "+$pod.getMetadata().getLabels()));
            end
            """;
        var output = Generator.convert(input);
        System.out.println(output);
    }

    @Test
    public void testSmoke8() throws Exception {
        final var input = """
            rule "Fix the Service selector sounds like Pod name but not an exact match"
            when
              $svc : Service( metadata.name != "kubernetes", $selectorName: spec!.selector["app.kubernetes.io/name"] )
              $pod : Pod( metadata.labels["app.kubernetes.io/name"] != $selectorName, metadata.labels["app.kubernetes.io/name"] soundslike $selectorName )
            then
              insert(new Advice("Service selector hint","Service "+$svc.getMetadata().getName()+" selector sounds-like Pod "+$pod.getMetadata().getName()+", but not an exact match: "+$pod.getMetadata().getLabels()));
            end
            """;
        var output = Generator.convert(input);
        System.out.println(output);
    }


    @Test
    public void testSmoke9() throws Exception {
        final var input = """
            rule "Fix the Service targetPort and the containerPort"
            when
              $svc : Service( metadata.name != "kubernetes" )
              $sPort : ServicePort() from $svc.spec.ports
              $pod : Pod( mapContains(metadata.labels, $svc.spec.selector) )
              not (
                $c : Container() from $pod.spec.containers and
                ContainerPort(containerPort == $sPort.targetPort.intVal) from $c.ports
              )
            then
              insert(new Advice("Fix the Service targetPort and the containerPort","Service " + 
                  $svc.getMetadata().getName() +
                  " targetPort: " +
                  $sPort.getTargetPort() +
                  " not found in any Container of related Pod: " +
                  $pod.getMetadata().getName()
              ));
            end
            """;
        var output = Generator.convert(input);
        System.out.println(output);
    }
}
