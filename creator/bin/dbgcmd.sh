#!/bin/bash

cd `dirname $0`/..

EIGEN_RELEASE=release-2.0.74-stable
EPATH=/usr/pi/$EIGEN_RELEASE/bin
DEVICE=$1
LINE=$2

N=`basename $0`
GENFILE=`mktemp /tmp/${N}.XXXXXX` || exit 1

echo "executing for:$DEVICE cmd:$LINE"


TEMPLATE=$(echo $LINE | cut -f1 -d:) 
VAR1=$(echo $LINE | cut -f2 -d:) 
VAR2=$(echo $LINE | cut -f3 -d:) 
VAR3=$(echo $LINE | cut -f4 -d:) 
VAR4=$(echo $LINE | cut -f5 -d:) 
VAR5=$(echo $LINE | cut -f6 -d:) 
VAR6=$(echo $LINE | cut -f7 -d:) 
VAR7=$(echo $LINE | cut -f8 -d:) 
VAR8=$(echo $LINE | cut -f9 -d:) 
VAR9=$(echo $LINE | cut -f10 -d:) 
echo "# Generating: $TEMPLATE  VAR1=$VAR1 VAR2=$VAR2 VAR3=$VAR3 VAR4=$VAR4 VAR5=$VAR5 VAR6=$VAR6 VAR7=$VAR7 VAR8=$VAR8 VAR9=$VAR9      "

if [ -f $TEMPLATE ]; then
	sed -e "s/%VAR1%/$VAR1/g 
		s/%VAR2%/$VAR2/g 
		s/%VAR3%/$VAR3/g
		s/%VAR4%/$VAR4/g
		s/%VAR5%/$VAR5/g
		s/%VAR6%/$VAR6/g
		s/%VAR7%/$VAR7/g
		s/%VAR8%/$VAR8/g
		s/%VAR9%/$VAR9/g
	" < $TEMPLATE > $GENFILE
	if [ -z "$DEBUG" ]; then
		$EPATH/bscript --verbose "<interpreter1>" $GENFILE
		sleep 1
	else 
		cat $GENFILE
	fi 
fi

if [ -f $DEVICE/$TEMPLATE ]; then
	sed -e "s/%VAR1%/$VAR1/g 
		s/%VAR2%/$VAR2/g 
		s/%VAR3%/$VAR3/g
		s/%VAR4%/$VAR4/g
		s/%VAR5%/$VAR5/g
		s/%VAR6%/$VAR6/g
		s/%VAR7%/$VAR7/g
		s/%VAR8%/$VAR8/g
		s/%VAR9%/$VAR9/g
	" < $DEVICE/$TEMPLATE > $GENFILE
	if [ -z "$DEBUG" ]; then
		$EPATH/bscript --verbose "<interpreter1>" $GENFILE
		sleep 1
	else 
		cat $GENFILE
	fi 
fi

rm $GENFILE


