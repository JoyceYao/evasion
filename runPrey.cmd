<<<<<<< HEAD
set CLASSPATH=.\lib\javax.websocket-api-1.1.jar;.\lib\evasion-0.0.1-SNAPSHOT-jar-with-dependencies.jar;.\lib\json.jar;.\lib\json-simple-1.1.1.jar;.\lib\jetty-all-9.0.4.v20130625.jar

java -Xbootclasspath/a:"%CLASSPATH%" -jar .\lib\prey.jar
=======
set BOOTPATH=.\lib\javax.websocket-api-1.1.jar;.\lib\json-simple-1.1.1.jar:.\lib\json.jar:.\lib\jetty-all-9.0.4.v20130625.jar:.\lib\jackson-core-2.5.0.jar:.\lib\jackson-databind-2.5.4.jar:.\lib\jackson-annotations-2.5.0.jar

java -Xbootclasspath/a:"%BOOTPATH%" -jar .\lib\prey.jar
>>>>>>> 4018a20d8197f8b18df25aed0b4bdaf7217a555b
