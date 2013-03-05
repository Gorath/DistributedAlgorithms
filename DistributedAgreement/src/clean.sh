#!/bin/bash

for f in $( find  . -iname "*.class" ); do  rm $f ; done
for f in $( find  . -iname "*~" ); do  rm $f ; done
for f in $( find  . -iname "\#*\#" ); do  rm $f ; done