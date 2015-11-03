#!/bin/bash
CLASSPATH=/Users/vmullachery/workspace/brooklyn-omniheurists/evasion/evasion/target/evasion-0.0.1-SNAPSHOT-jar-with-dependencies.jar
CLASSPATH=${CLASSPATH}:/Users/vmullachery/workspace/brooklyn-omniheurists/javax.websocket-api-1.1.jar:./bin/
java -Xbootclasspath/a:${CLASSPATH} -jar ./lib/hunter.jar

