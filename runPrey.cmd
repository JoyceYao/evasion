set BOOTPATH=.\lib\javax.websocket-api-1.1.jar;.\lib\json-simple-1.1.1.jar:.\lib\json.jar:.\lib\jetty-all-9.0.4.v20130625.jar:.\lib\jackson-core-2.5.0.jar:.\lib\jackson-databind-2.5.4.jar:.\lib\jackson-annotations-2.5.0.jar

java -Xbootclasspath/a:"%BOOTPATH%" -jar .\lib\prey.jar
