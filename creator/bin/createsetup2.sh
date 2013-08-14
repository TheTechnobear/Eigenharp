#!/bin/bash

cd `dirname $0`/..
pwd

DIR=bin
DIR3=$DIR/thirdparty
CP=$DIR/com.technobear.jar:$DIR3/xmlrpc-client-3.1.3.jar:$DIR3/xmlrpc-common-3.1.3.jar:$DIR3/ws-commons-util-1.0.2.jar
echo java -cp $CP com.technobear.eigenharp.creator.CreateLayoutApp "$@" 
java -cp "$CP" com.technobear.eigenharp.creator.CreateSetupApp "$@" 



