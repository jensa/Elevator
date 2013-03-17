#!/bin/bash

BASEPATH="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
java -classpath $BASEPATH/lib/elevator.jar -Djava.security.policy=$BASEPATH/lib/rmi.policy -Djava.rmi.server.codebase=file:$BASEPATH/lib/elevator.jar elevator.Elevators -top 5 -number 5 -rmi
