#!/bin/bash
THE_CLASSPATH=:../target/stormcapture-0.2.jar:
#java 1.8 
#export JAVA_HOME=$(/usr/libexec/java_home)
#adjust java home
export JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk1.8.0_212.jdk/Contents/Home
# change port and dir where  is wabac installed
$JAVA_HOME/bin/java  -classpath $THE_CLASSPATH  gov.lanl.crawler.input.InputMain -port=8067 -sdir=/data/web/tracer_demo/capture/wabac/
#jar -xf ./target/stormcapture-0.2.jar

