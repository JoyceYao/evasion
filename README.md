# evasion
jar cvfe evasionServer.jar  Server/ServerMain  *.class
jar cvfe evasionClient.jar  evasion/Main  *.class

Server:
java -jar evasionServer.jar -rH -sR -tcly -p1337 -N3 -M10

Client:
java -jar evasionClient.jar -p1337 -N3 -M10 -Hcly -Pmv
