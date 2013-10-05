#!/bin/bash

cd `dirname $0`/..

DIR=bin
DIR3=$DIR/thirdparty
CP=$DIR/com.technobear.jar:$DIR3/xmlrpc-client-3.1.3.jar:$DIR3/xmlrpc-common-3.1.3.jar:$DIR3/ws-commons-util-1.0.2.jar
java -cp "$CP" com.technobear.eigenharp.creator.CreatorApp "$@" 



