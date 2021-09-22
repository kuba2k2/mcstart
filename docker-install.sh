#!/bin/bash

wget https://github.com/kuba2k2/mcstart/releases/download/v1.1.3/mcstart-1.1.3.jar -O /mcstart.jar
sed -i 's/exec /mcstart /g' /start-finalExec
echo "function mcstart(){ exec java -Xmx256M -jar /mcstart.jar \$* ; }" >> /start-utils
