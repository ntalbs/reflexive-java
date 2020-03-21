#!/usr/bin/env sh

cd /var/Velociraptor
./bin/Velociraptor > /dev/null 2> /dev/null < /dev/null &

echo $! > /var/Velociraptor/Velociraptor.pid
echo "Server started"
