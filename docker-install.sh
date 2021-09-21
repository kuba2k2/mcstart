#!/bin/bash

wget https://github.com/kuba2k2/mcstart/releases/download/v1.0.2/mcstart-1.0.2.jar -O /mcstart.jar
sed -i 's/exec /mcstart /g' /start-finalExec
echo "function mcstart(){ exec java -Xmx128M -jar /mcstart.jar \$* ; }" >> /start-utils
