#!/bin/bash

cd `dirname $0`/..
pwd

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

bin/createsetup.sh "$DEVICE" "$CONF"
