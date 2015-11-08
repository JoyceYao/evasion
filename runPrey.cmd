rem !/bin/bash
rem CLASSPATH=/Users/vmullachery/workspace/brooklyn-omniheurists/evasion/evasion/target/evasion-0.0.1-SNAPSHOT-jar-with-dependencies.jar
rem CLASSPATH=${CLASSPATH}:/Users/vmullachery/workspace/brooklyn-omniheurists/javax.websocket-api-1.1.jar
set CLASSPATH=.\lib\javax.websocket-api-1.1.jar;.\lib\evasion-0.0.1-SNAPSHOT-jar-with-dependencies.jar

java -Xbootclasspath/a;"%CLASSPATH%" -jar .\lib\prey.jar
