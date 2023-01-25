# RuleOps Project

The project expects empty minikube/kind for integration tests purposes.
Skip it with `mvn clean install -DskipITs` if needed.

## Configuration defaults

By default RuleOps observes the Kubernetes `default` namespace.
This is reflected in the configuration of the Quarkus' `quarkus-kubernetes-client` extension, ([ref.](https://quarkus.io/guides/kubernetes-client#quarkus-kubernetes-client_quarkus.kubernetes-client.namespace)).
You might override this setting to enable the use of RuleOps in other and different scenarios.

## Scenarios

Deployment quota
- with reference to this guide: https://kubernetes.io/docs/tasks/administer-cluster/manage-resources/quota-pod-namespace/
  the guide contains Deployment with replica=3 while quota=2
- kubectl apply -f k8s/quota-pod.yml
- kubectl apply -f k8s/quota-pod-deployment.yml

Statefulset quota
- with reference to this guide:  https://kubernetes.io/docs/tasks/administer-cluster/manage-resources/quota-memory-cpu-namespace/
- kubectl apply -f k8s/quota-mem-cpu.yml
- kubectl apply -f k8s/quota-mem-cpu-pod.yml
- kubectl apply -f k8s/hello-pvdf-statefulset.yml

Pod Pending for PVC Pending
- with reference to this guide: https://kubernetes.io/docs/tasks/configure-pod-container/configure-persistent-volume-storage/
- kubectl apply -f k8s/pv-claim.yml (this creates the pvc without the pv, so the pvc will hold in Pending state, as needed for test)
- kubectl apply -f k8s/pvc-deployment.yml

Fix the Service selector No Pod found for selector
- kubectl apply -f k8s/hello-pvdf-service.yml

Fix the Service selector matches Pod name, but other selectors don't
- kubectl apply -f k8s/hello-pvdf-service.yml
- kubectl apply -f k8s/hello-pvdf-diffVersionPod.yml

Fix the Service selector sounds like Pod name but not an exact match
- kubectl apply -f k8s/hello-pvdf-service.yml
- kubectl apply -f k8s/hello-pvdf-similarNamePod.yml

Fix the Service targetPort and the containerPort
- kubectl apply -f k8s/hello-pvdf-service.yml
- kubectl apply -f k8s/hello-pvdf-wrongPortPod.yml 

## DEV NOTES

### enabling CLI use-case

```sh
mvn clean install -DskipTests -Dquarkus.profile=cli
java -jar target/quarkus-app/quarkus-run.jar -Dquarkus.profile=cli
alias ruleops="java -jar target/quarkus-app/quarkus-run.jar -Dquarkus.profile=cli"
ruleops
```

<!--

This project uses Quarkus, the Supersonic Subatomic Java Framework.

If you want to learn more about Quarkus, please visit its website: https://quarkus.io/ .

## Running the application in dev mode

You can run your application in dev mode that enables live coding using:
```shell script
./mvnw compile quarkus:dev
```

> **_NOTE:_**  Quarkus now ships with a Dev UI, which is available in dev mode only at http://localhost:8080/q/dev/.

## Packaging and running the application

The application can be packaged using:
```shell script
./mvnw package
```
It produces the `quarkus-run.jar` file in the `target/quarkus-app/` directory.
Be aware that it’s not an _über-jar_ as the dependencies are copied into the `target/quarkus-app/lib/` directory.

The application is now runnable using `java -jar target/quarkus-app/quarkus-run.jar`.

If you want to build an _über-jar_, execute the following command:
```shell script
./mvnw package -Dquarkus.package.type=uber-jar
```

The application, packaged as an _über-jar_, is now runnable using `java -jar target/*-runner.jar`.

## Creating a native executable

You can create a native executable using: 
```shell script
./mvnw package -Pnative
```

Or, if you don't have GraalVM installed, you can run the native executable build in a container using: 
```shell script
./mvnw package -Pnative -Dquarkus.native.container-build=true
```

You can then execute your native executable with: `./target/demo20220701ruleops-1.0.0-SNAPSHOT-runner`

If you want to learn more about building native executables, please consult https://quarkus.io/guides/maven-tooling.

## Related Guides

- SmallRye OpenAPI ([guide](https://quarkus.io/guides/openapi-swaggerui)): Document your REST APIs with OpenAPI - comes with Swagger UI
- Kubernetes Client ([guide](https://quarkus.io/guides/kubernetes-client)): Interact with Kubernetes and develop Kubernetes Operators

## Provided Code

### RESTEasy JAX-RS

Easily start your RESTful Web Services

[Related guide section...](https://quarkus.io/guides/getting-started#the-jax-rs-resources)

-->