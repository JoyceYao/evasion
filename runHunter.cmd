<<<<<<< HEAD
rem !/bin/bash
rem CLASSPATH=/Users/vmullachery/workspace/brooklyn-omniheurists/evasion/evasion/target/evasion-0.0.1-SNAPSHOT-jar-with-dependencies.jar
rem CLASSPATH=${CLASSPATH}:/Users/vmullachery/workspace/brooklyn-omniheurists/javax.websocket-api-1.1.jar
setenv CLASSPATH=${CLASSPATH};.\lib\javax.websocket-api-1.1.jar;.\lib\evasion-0.0.1-SNAPSHOT-jar-with-dependencies.jar
export CLASSPATH
=======
set BOOTPATH=.\lib\javax.websocket-api-1.1.jar;.\lib\json-simple-1.1.1.jar:.\lib\json.jar:.\lib\jetty-all-9.0.4.v20130625.jar:.\lib\jackson-core-2.5.0.jar:.\lib\jackson-databind-2.5.4.jar:.\lib\jackson-annotations-2.5.0.jar
>>>>>>> 4018a20d8197f8b18df25aed0b4bdaf7217a555b

java -Xbootclasspath/a:"%BOOTPATH%" -jar .\lib\hunter.jar
