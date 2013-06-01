EIGEN_RELEASE=release-2.0.74-stable
EPATH=/usr/pi/$EIGEN_RELEASE/bin
CONF=$1
BASECONF=`basename ${CONF}`
GENFILE=`mktemp /tmp/${BASECONF}.XXXXXX` || exit 1

echo "# Generating using:$CONF debug:${DEBUG_FILE}"
while read LINE
do
TEMPLATE=$(echo $LINE | cut -f1 -d:) 
VAR1=$(echo $LINE | cut -f2 -d:) 
VAR2=$(echo $LINE | cut -f3 -d:) 
VAR3=$(echo $LINE | cut -f4 -d:) 
echo "# Generating: $TEMPLATE  VAR1=$VAR1 VAR2=$VAR2 VAR3=$VAR3"
sed -e "s/%VAR1%/$VAR1/g" -e  "s/%VAR2%/$VAR2/g" -e "s/%VAR3%/$VAR3/g" < $TEMPLATE > $GENFILE
if [ -z "$DEBUG" ]; then
	$EPATH/bscript --verbose "<interpreter1>" $GENFILE
	sleep 1
else 
	cat $GENFILE
fi 
done < $CONF



