Compile the Example CLI Jar with 

```shell
mvn install
```


```shell
java -jar target/quarkus-app/quarkus-run.jar <$POD_NAME>
```

otherwise compile the native executable with

```shell
mvn install -Dnative
```

then run it with

```shell
./target/ruleops-cli-example-1.0.0-SNAPSHOT-runner
```