Compile the RHOC SOP Jar with 

```shell
mvn install
```

Then you can run the only RHOC SOP defined using a managed connector ID $RHOC_CONNECTOR_ID

```shell
java -jar target/quarkus-app/quarkus-run.jar <$RHOC_CONNECTOR_ID>
```