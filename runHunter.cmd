rem !/bin/bash
rem CLASSPATH=/Users/vmullachery/workspace/brooklyn-omniheurists/evasion/evasion/target/evasion-0.0.1-SNAPSHOT-jar-with-dependencies.jar
rem CLASSPATH=${CLASSPATH}:/Users/vmullachery/workspace/brooklyn-omniheurists/javax.websocket-api-1.1.jar
setenv CLASSPATH=${CLASSPATH};.\lib\javax.websocket-api-1.1.jar;.\lib\evasion-0.0.1-SNAPSHOT-jar-with-dependencies.jar
export CLASSPATH

java -Xbootclasspath/a:"%CLASSPATH%" -jar .\lib\hunter.jar