# Installs Stardog API and SNARL libs to local maven repository
# Expects Stardog 2.0.2
# Run this in Stardog home directory

DEPENDENCY_FILE=/tmp/dependencies.txt
STARDOG_HOME=/opt/stardog-2.0.2

rm $DEPENDENCY_FILE

#cd $STARDOG_HOME/client/api/
cd $STARDOG_HOME/client/snarl/

for file in *
do
    if [[ -f $file ]]; then
	if [[ $file =~ (.+)-([0-9].+)(\.jar) ]]; then
		GROUP_ID=${BASH_REMATCH[1]}
		ARTIFACT_ID=${BASH_REMATCH[1]}
		VERSION=${BASH_REMATCH[2]}
		FILEPATH=$STARDOG_HOME/client/api/$file

		mvn install:install-file -DgroupId=$GROUP_ID -DartifactId=$ARTIFACT_ID -Dversion=$VERSION -Dfile=$FILEPATH -Dpackaging=jar -DgeneratePom=true
		
		echo "    <dependency>" >> $DEPENDENCY_FILE
	  	echo "        <groupId>$GROUP_ID</groupId>" >> $DEPENDENCY_FILE
  		echo "        <artifactId>$ARTIFACT_ID</artifactId>" >> $DEPENDENCY_FILE
  		echo "        <version>$VERSION</version>" >> $DEPENDENCY_FILE
  		echo "    </dependency>" >> $DEPENDENCY_FILE
	fi
    fi
done

