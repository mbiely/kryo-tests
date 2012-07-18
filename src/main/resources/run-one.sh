#!/bin/sh
if [ $# -lt 1 ]; then
        echo "not enough arguments" 
        exit 1
fi

OPTS="-server"

case $1 in 
    server)
	time java $OPTS -cp '@@DIR@@:@@DIR@@/@@JAR@@:@@DIR@@/deps/*:' ch.epfl.lsr.testing.netty.ServerMain ;;
    actor)
	time java $OPTS -cp '@@DIR@@:@@DIR@@/@@JAR@@:@@DIR@@/deps/*:' ch.epfl.lsr.testing.netty.akkabridge.ServerMain ;;
    client) 
	time java $OPTS -cp '@@DIR@@:@@DIR@@/@@JAR@@:@@DIR@@/deps/*:' ch.epfl.lsr.testing.netty.ClientMain $2 $3 ;;
    akkaclient)
	time java $OPTS -cp '@@DIR@@:@@DIR@@/@@JAR@@:@@DIR@@/deps/*:' ch.epfl.lsr.testing.akkatest.ClientMain $2 $3 ;;
    akkaserver)
	time java $OPTS -cp '@@DIR@@:@@DIR@@/@@JAR@@:@@DIR@@/deps/*:' ch.epfl.lsr.testing.akkatest.ServerMain ;;
    kryonetserver)
	time java $OPTS -cp '@@DIR@@:@@DIR@@/@@JAR@@:@@DIR@@/deps/*:' ch.epfl.lsr.testing.kryonet.ServerMain ;;
    kryonetclient)
	time java $OPTS -cp '@@DIR@@:@@DIR@@/@@JAR@@:@@DIR@@/deps/*:' ch.epfl.lsr.testing.kryonet.ClientMain $2 $3 ;;
esac
