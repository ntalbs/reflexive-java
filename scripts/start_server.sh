#!/usr/bin/env sh

/var/velociraptor/velociraptor-0.0.1/bin/velociraptor > /dev/null 2> /dev/null < /dev/null &

echo $! > /var/velociraptor/velociraptor.pid
echo "server started"
