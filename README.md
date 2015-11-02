# evasion
jar cvfe evasionServer.jar  server/ServerMain  *.class

jar cvfe evasionClient.jar  evasion/Main  *.class

Server:
java -jar evasionServer.jar -rH -sR -tcly -p1337 -N3 -M10 -U1991

Client:
java -jar evasionClient.jar -p1337 -N3 -M10 -Hcly -Pmv


# for windows cd under bin
jar -cp ../lib/* cvfe evasionServer.jar server/ServerMain -C . *
jar cvfe evasionClient.jar  evasion/Main -C . *

Server:
java -jar evasionServer.jar -p1337 -N3 -M10 -Hcly -Pmv


Client:
java -jar evasionClient.jar -rH -sR -tcly -p1337 -N3 -M10 -U1991
java -cp evasionClient.jar:../lib/* evasion.Main


