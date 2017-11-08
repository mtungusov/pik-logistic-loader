#!/usr/bin/env bash

DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
JAR="$(basename $DIR)".jar

#lein uberjar && java -jar "target/$JAR" -f "2017-11-07 16:46:24"
lein uberjar && java -jar "target/$JAR"
