#!/bin/bash

args=`find dataset -type f | xargs`

echo "Compilando o código java Serial"
bash src/java/serial/build.sh

echo "Compilando o código java Concurrent"
bash src/java/concurrent/build.sh

echo "Executanto o código Serial"
time bash src/java/serial/run.sh $args

echo "Executanto o código Concurrent"
time bash src/java/concurrent/run.sh $args
