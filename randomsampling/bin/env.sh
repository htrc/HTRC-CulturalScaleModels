#!/bin/sh

# set env
this=$0
bin=`dirname "$this"`
bin=`cd "$bin"; pwd`
RS_HOME=$bin/..

# java
#JAVA=$JAVA_HOME/bin/java
JAVA=java
JAVA_HEAP_MAX=-Xmx1024m 

# RS_CLASSPATH
RS_CLASSPATH=.
for f in ${RS_HOME}/lib/*.jar; do
  RS_CLASSPATH=${RS_CLASSPATH}:$f;
done
for f in ${RS_HOME}/*.jar; do
  RS_CLASSPATH=${RS_CLASSPATH}:$f;
done
RS_CLASSPATH=${RS_CLASSPATH}:${RS_HOME}/conf
#echo $KV_RS_CLASSPATH