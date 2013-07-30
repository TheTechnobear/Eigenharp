#!/bin/bash


cd `dirname $0`/..
pwd

EIGEN_RELEASE=release-2.0.74-stable
EPATH=/usr/pi/$EIGEN_RELEASE/bin

GENFILE=`mktemp /tmp/${BASECONF}.XXXXXX` || exit 1
MODULES=`pwd`/modules

java -cp `dirname $0` CreateLayout "$@" > $GENFILE

$EPATH/bscript --verbose "<interpreter1>" $GENFILE

rm $GENFILE


