# Installs Stardog libs in local maven repository

DEPENDENCY_FILE=/tmp/dependencies.txt
STARDOG_HOME=/opt/stardog-2.0.2

#LIB_DIR=$STARDOG_HOME/client/api
LIB_DIR=$STARDOG_HOME/client/snarl

rm $DEPENDENCY_FILE

cd $LIB_DIR

for file in *
do
    if [[ -f $file ]]; then
	if [[ $file =~ (.+)-([0-9].+)(\.jar) ]]; then
		GROUP_ID=${BASH_REMATCH[1]}
		ARTIFACT_ID=${BASH_REMATCH[1]}
		VERSION=${BASH_REMATCH[2]}
		FILEPATH=$LIB_DIR/$file

		mvn install:install-file -DgroupId=$GROUP_ID -DartifactId=$ARTIFACT_ID -Dversion=$VERSION -Dfile=$FILEPATH -Dpackaging=jar -DgeneratePom=true
		
		echo "    <dependency>" >> $DEPENDENCY_FILE
	  	echo "        <groupId>$GROUP_ID</groupId>" >> $DEPENDENCY_FILE
  		echo "        <artifactId>$ARTIFACT_ID</artifactId>" >> $DEPENDENCY_FILE
  		echo "        <version>$VERSION</version>" >> $DEPENDENCY_FILE
  		echo "    </dependency>" >> $DEPENDENCY_FILE
	fi
    fi
done

