#!/bin/sh

# set env
bin=`dirname "$0"`
bin=`cd "$bin"; pwd`
. "$bin"/env.sh

# start
$JAVA $JAVA_HEAP_MAX -classpath "$RS_CLASSPATH" \
	edu.indiana.d2i.htrc.randomsampling.ws.RSServer "$@" &