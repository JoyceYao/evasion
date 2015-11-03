#!/bin/bash
#CLASSPATH=/Users/vmullachery/workspace/brooklyn-omniheurists/evasion/evasion/target/evasion-0.0.1-SNAPSHOT-jar-with-dependencies.jar
#CLASSPATH=${CLASSPATH}:/Users/vmullachery/workspace/brooklyn-omniheurists/javax.websocket-api-1.1.jar
CLASSPATH=.\lib\*
#java -Xbootclasspath/a:${CLASSPATH} -jar ./lib/hunter.jar
export PATH=$PATH:D:/HPS_HW/evasion/lib/
java -cp ${CLASSPATH} -jar hunter.jar

