#!/usr/bin/env sh

PID=$(cat /var/reflexive-java/reflexive-java.pid)

if kill -9 $PID > /dev/null 2> /dev/null; then
    echo "The service process ${PID} is killed."
else
    killall java
    echo 'Killed all java process'
fi
