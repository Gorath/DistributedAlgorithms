#!/bin/bash

for f in $( find  . -iname "*.class" ); do  rm $f ; done
