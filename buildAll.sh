#!/bin/bash

cd bin
jar -cvfe ../lib/hunter.jar evasion.HuntApp evasion/ 
jar -cvfe ../lib/prey.jar evasion.PreyApp  evasion/

