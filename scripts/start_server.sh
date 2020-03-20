#!/usr/bin/env sh

cd /var/velociraptor/Velociraptor-0.0.1
./bin/Velociraptor > /dev/null 2> /dev/null < /dev/null &

echo $! > /var/velociraptor/Velociraptor.pid
echo "server started"
