#!/bin/sh
if [ $# -lt 1 ]; then
        echo "not enough arguments" 
        exit 1
fi

OPTS="-server"

case $1 in 
    server)
	time java $OPTS -cp '@@DIR@@:@@DIR@@/@@JAR@@:@@DIR@@/deps/*:' ch.epfl.lsr.netty.ServerMain ;;
    actor)
	time java $OPTS -cp '@@DIR@@:@@DIR@@/@@JAR@@:@@DIR@@/deps/*:' ch.epfl.lsr.netty.akkabridge.ServerMain ;;
    client) 
	time java $OPTS -cp '@@DIR@@:@@DIR@@/@@JAR@@:@@DIR@@/deps/*:' ch.epfl.lsr.netty.ClientMain $2 $3 ;;
    akkaclient)
	time java $OPTS -cp '@@DIR@@:@@DIR@@/@@JAR@@:@@DIR@@/deps/*:' ch.epfl.lsr.akkatest.ClientMain $2 $3 ;;
    akkaserver)
	time java $OPTS -cp '@@DIR@@:@@DIR@@/@@JAR@@:@@DIR@@/deps/*:' ch.epfl.lsr.akkatest.ServerMain ;;
esac
