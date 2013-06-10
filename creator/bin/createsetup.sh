#!/bin/bash

cd `dirname $0`/..
pwd

EIGEN_RELEASE=release-2.0.74-stable
EPATH=/usr/pi/$EIGEN_RELEASE/bin
DEVICE=$1
CONF=$2

if [[ -z $DEVICE ]]; then
echo "Enter device pico|tau|alpha"
read DEVICE
fi

if [[ -z $CONF ]]; then
echo "Enter config"
read CONF
fi

BASECONF=`basename ${CONF}`
GENFILE=`mktemp /tmp/${BASECONF}.XXXXXX` || exit 1

echo "# Generating for:$DEVICE using:$CONF debug:${DEBUG_FILE}"
while read LINE
do

if [[ "$LINE" =~ "#" ]]; then 
	echo $LINE
	continue
fi

if [[ -z "$LINE" ]]; then 
	continue
fi

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

done < $CONF

rm $GENFILE


