#!/bin/bash
<<<<<<< HEAD
#CLASSPATH=/Users/vmullachery/workspace/brooklyn-omniheurists/evasion/evasion/target/evasion-0.0.1-SNAPSHOT-jar-with-dependencies.jar
#CLASSPATH=${CLASSPATH}:/Users/vmullachery/workspace/brooklyn-omniheurists/javax.websocket-api-1.1.jar
CLASSPATH=.\lib\*
#java -Xbootclasspath/a:${CLASSPATH} -jar ./lib/hunter.jar
export PATH=$PATH:D:/HPS_HW/evasion/lib/
java -cp ${CLASSPATH} -jar hunter.jar
=======
CLASSPATH=/Users/vmullachery/workspace/brooklyn-omniheurists/evasion/evasion/target/evasion-0.0.1-SNAPSHOT-jar-with-dependencies.jar
CLASSPATH=${CLASSPATH}:/Users/vmullachery/workspace/brooklyn-omniheurists/javax.websocket-api-1.1.jar:/Users/vmullachery/workspace/JSON-java-master/json/json.jar:/Users/vmullachery/workspace/Evasion/lib/json-simple-1.1.1.jar:/Users/vmullachery/workspace/Evasion/lib/jetty-all-9.0.4.v20130625.jar:./bin/
java -Xbootclasspath/a:${CLASSPATH} -jar ./lib/hunter.jar
>>>>>>> 33acd883e505bb0c9a241143d974b29ba1e979af

