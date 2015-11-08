# !/bin/bash
CLASSPATH=.\lib\javax.websocket-api-1.1.jar;.\lib\evasion-0.0.1-SNAPSHOT-jar-with-dependencies.jar
CLASSPATH=${CLASSPATH};.\lib\json-simple-1.1.1.jar;.\lib\jetty-all-9.0.4.v20130625.jar;.\bin\
java -Xbootclasspath/a;"%CLASSPATH%" -jar .\lib\prey.jar

