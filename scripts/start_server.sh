#!/usr/bin/env sh

cd /var/reflexive-java
./bin/reflexive-java > /dev/null 2> /dev/null < /dev/null &

echo $! > /var/reflexive-java/reflexive-java.pid
echo "Server started"
