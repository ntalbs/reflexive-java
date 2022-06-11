#!/usr/bin/env sh

cd /var
jar -xf reflexive-java-0.0.1.zip
mv reflexive-java-0.0.1 reflexive-java
rm reflexive-java-0.0.1.zip
chmod u+x reflexive-java/bin/reflexive-java
