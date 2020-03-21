#!/usr/bin/env sh

cd /var
jar -xf Velociraptor-0.0.1.zip
mv Velociraptor-0.0.1 Velociraptor
rm Velociraptor-0.0.1.zip
chmod u+x Velociraptor/bin/Velociraptor
