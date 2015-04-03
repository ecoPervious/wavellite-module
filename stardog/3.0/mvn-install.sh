STARDOG_HOME=/opt/stardog-3.0

mvn install:install-file -DgroupId=com.complexible -DartifactId=cp-common-protobuf -Dversion=1.2 -Dfile=$STARDOG_HOME/client/api/cp-common-protobuf-1.2.jar -Dpackaging=jar -DgeneratePom=true
mvn install:install-file -DgroupId=com.complexible -DartifactId=cp-common-utils -Dversion=4.0 -Dfile=$STARDOG_HOME/client/api/cp-common-utils-4.0.jar -Dpackaging=jar -DgeneratePom=true
mvn install:install-file -DgroupId=com.complexible -DartifactId=cp-openrdf-utils -Dversion=3.0.1 -Dfile=$STARDOG_HOME/client/api/cp-openrdf-utils-3.0.1.jar -Dpackaging=jar -DgeneratePom=true
mvn install:install-file -DgroupId=org.mindrot -DartifactId=jbcrypt -Dversion=0.3.1 -Dfile=$STARDOG_HOME/client/api/jbcrypt-0.3.1.jar -Dpackaging=jar -DgeneratePom=true
mvn install:install-file -DgroupId=javax.annotation -DartifactId=jsr305 -Dversion=3.0.0 -Dfile=$STARDOG_HOME/client/api/jsr305-3.0.0.jar -Dpackaging=jar -DgeneratePom=true
mvn install:install-file -DgroupId=com.complexible -DartifactId=license-core -Dversion=3.0 -Dfile=$STARDOG_HOME/client/api/license-core-3.0.jar -Dpackaging=jar -DgeneratePom=true
mvn install:install-file -DgroupId=com.complexible -DartifactId=stardog -Dversion=3.0 -Dfile=$STARDOG_HOME/client/api/stardog-3.0.jar -Dpackaging=jar -DgeneratePom=true
mvn install:install-file -DgroupId=com.complexible -DartifactId=stardog-api -Dversion=3.0 -Dfile=$STARDOG_HOME/client/api/stardog-api-3.0.jar -Dpackaging=jar -DgeneratePom=true
mvn install:install-file -DgroupId=com.complexible -DartifactId=stardog-cli -Dversion=3.0 -Dfile=$STARDOG_HOME/client/api/stardog-cli-3.0.jar -Dpackaging=jar -DgeneratePom=true
mvn install:install-file -DgroupId=com.complexible -DartifactId=stardog-core-security -Dversion=3.0 -Dfile=$STARDOG_HOME/client/api/stardog-core-security-3.0.jar -Dpackaging=jar -DgeneratePom=true
mvn install:install-file -DgroupId=com.complexible -DartifactId=stardog-core-shared -Dversion=3.0 -Dfile=$STARDOG_HOME/client/api/stardog-core-shared-3.0.jar -Dpackaging=jar -DgeneratePom=true
mvn install:install-file -DgroupId=com.complexible -DartifactId=stardog-icv-api -Dversion=3.0 -Dfile=$STARDOG_HOME/client/api/stardog-icv-api-3.0.jar -Dpackaging=jar -DgeneratePom=true
mvn install:install-file -DgroupId=com.complexible -DartifactId=stardog-icv-api_snarl -Dversion=3.0 -Dfile=$STARDOG_HOME/client/api/stardog-icv-api_snarl-3.0.jar -Dpackaging=jar -DgeneratePom=true
mvn install:install-file -DgroupId=com.complexible -DartifactId=stardog-icv-shared -Dversion=3.0 -Dfile=$STARDOG_HOME/client/api/stardog-icv-shared-3.0.jar -Dpackaging=jar -DgeneratePom=true
mvn install:install-file -DgroupId=com.complexible -DartifactId=stardog-protocols-api-client -Dversion=3.0 -Dfile=$STARDOG_HOME/client/api/stardog-protocols-api-client-3.0.jar -Dpackaging=jar -DgeneratePom=true
mvn install:install-file -DgroupId=com.complexible -DartifactId=stardog-protocols-api-server -Dversion=3.0 -Dfile=$STARDOG_HOME/client/api/stardog-protocols-api-server-3.0.jar -Dpackaging=jar -DgeneratePom=true
mvn install:install-file -DgroupId=com.complexible -DartifactId=stardog-protocols-api-shared -Dversion=3.0 -Dfile=$STARDOG_HOME/client/api/stardog-protocols-api-shared-3.0.jar -Dpackaging=jar -DgeneratePom=true
mvn install:install-file -DgroupId=com.complexible -DartifactId=stardog-protocols-barc-client -Dversion=3.0 -Dfile=$STARDOG_HOME/client/api/stardog-protocols-barc-client-3.0.jar -Dpackaging=jar -DgeneratePom=true
mvn install:install-file -DgroupId=com.complexible -DartifactId=stardog-protocols-barc-server -Dversion=3.0 -Dfile=$STARDOG_HOME/client/api/stardog-protocols-barc-server-3.0.jar -Dpackaging=jar -DgeneratePom=true
mvn install:install-file -DgroupId=com.complexible -DartifactId=stardog-protocols-barc-shared -Dversion=3.0 -Dfile=$STARDOG_HOME/client/api/stardog-protocols-barc-shared-3.0.jar -Dpackaging=jar -DgeneratePom=true
mvn install:install-file -DgroupId=com.complexible -DartifactId=stardog-protocols-http-client -Dversion=3.0 -Dfile=$STARDOG_HOME/client/api/stardog-protocols-http-client-3.0.jar -Dpackaging=jar -DgeneratePom=true
mvn install:install-file -DgroupId=com.complexible -DartifactId=stardog-protocols-http-server -Dversion=3.0 -Dfile=$STARDOG_HOME/client/api/stardog-protocols-http-server-3.0.jar -Dpackaging=jar -DgeneratePom=true
mvn install:install-file -DgroupId=com.complexible -DartifactId=stardog-protocols-http-shared -Dversion=3.0 -Dfile=$STARDOG_HOME/client/api/stardog-protocols-http-shared-3.0.jar -Dpackaging=jar -DgeneratePom=true
mvn install:install-file -DgroupId=com.complexible -DartifactId=stardog-protocols-snarl-client -Dversion=3.0 -Dfile=$STARDOG_HOME/client/api/stardog-protocols-snarl-client-3.0.jar -Dpackaging=jar -DgeneratePom=true
mvn install:install-file -DgroupId=com.complexible -DartifactId=stardog-protocols-snarl-server -Dversion=3.0 -Dfile=$STARDOG_HOME/client/api/stardog-protocols-snarl-server-3.0.jar -Dpackaging=jar -DgeneratePom=true
mvn install:install-file -DgroupId=com.complexible -DartifactId=stardog-protocols-snarl-shared -Dversion=3.0 -Dfile=$STARDOG_HOME/client/api/stardog-protocols-snarl-shared-3.0.jar -Dpackaging=jar -DgeneratePom=true
mvn install:install-file -DgroupId=com.complexible -DartifactId=stardog-protocols-spec-client -Dversion=3.0 -Dfile=$STARDOG_HOME/client/api/stardog-protocols-spec-client-3.0.jar -Dpackaging=jar -DgeneratePom=true
mvn install:install-file -DgroupId=com.complexible -DartifactId=stardog-protocols-spec-server -Dversion=3.0 -Dfile=$STARDOG_HOME/client/api/stardog-protocols-spec-server-3.0.jar -Dpackaging=jar -DgeneratePom=true
mvn install:install-file -DgroupId=com.complexible -DartifactId=stardog-protocols-spec-shared -Dversion=3.0 -Dfile=$STARDOG_HOME/client/api/stardog-protocols-spec-shared-3.0.jar -Dpackaging=jar -DgeneratePom=true
mvn install:install-file -DgroupId=com.complexible -DartifactId=stardog-prov -Dversion=3.0 -Dfile=$STARDOG_HOME/client/api/stardog-prov-3.0.jar -Dpackaging=jar -DgeneratePom=true
mvn install:install-file -DgroupId=com.complexible -DartifactId=stardog-reasoning-api -Dversion=3.0 -Dfile=$STARDOG_HOME/client/api/stardog-reasoning-api-3.0.jar -Dpackaging=jar -DgeneratePom=true
mvn install:install-file -DgroupId=com.complexible -DartifactId=stardog-reasoning-cli -Dversion=3.0 -Dfile=$STARDOG_HOME/client/api/stardog-reasoning-cli-3.0.jar -Dpackaging=jar -DgeneratePom=true
mvn install:install-file -DgroupId=com.complexible -DartifactId=stardog-reasoning-shared -Dversion=3.0 -Dfile=$STARDOG_HOME/client/api/stardog-reasoning-shared-3.0.jar -Dpackaging=jar -DgeneratePom=true
mvn install:install-file -DgroupId=com.complexible -DartifactId=stardog-repair -Dversion=3.0 -Dfile=$STARDOG_HOME/client/api/stardog-repair-3.0.jar -Dpackaging=jar -DgeneratePom=true
mvn install:install-file -DgroupId=com.complexible -DartifactId=stardog-search-api -Dversion=3.0 -Dfile=$STARDOG_HOME/client/api/stardog-search-api-3.0.jar -Dpackaging=jar -DgeneratePom=true
mvn install:install-file -DgroupId=com.complexible -DartifactId=stardog-search-shared -Dversion=3.0 -Dfile=$STARDOG_HOME/client/api/stardog-search-shared-3.0.jar -Dpackaging=jar -DgeneratePom=true
mvn install:install-file -DgroupId=com.complexible -DartifactId=stardog-utils-common -Dversion=3.0 -Dfile=$STARDOG_HOME/client/api/stardog-utils-common-3.0.jar -Dpackaging=jar -DgeneratePom=true
mvn install:install-file -DgroupId=com.complexible -DartifactId=stardog-utils-openrdf -Dversion=3.0 -Dfile=$STARDOG_HOME/client/api/stardog-utils-openrdf-3.0.jar -Dpackaging=jar -DgeneratePom=true
mvn install:install-file -DgroupId=com.complexible -DartifactId=stardog-utils-rdf -Dversion=3.0 -Dfile=$STARDOG_HOME/client/api/stardog-utils-rdf-3.0.jar -Dpackaging=jar -DgeneratePom=true
mvn install:install-file -DgroupId=com.complexible -DartifactId=stardog-versioning-api -Dversion=3.0 -Dfile=$STARDOG_HOME/client/api/stardog-versioning-api-3.0.jar -Dpackaging=jar -DgeneratePom=true
mvn install:install-file -DgroupId=com.complexible -DartifactId=stardog-versioning-protocols-spec-client -Dversion=3.0 -Dfile=$STARDOG_HOME/client/api/stardog-versioning-protocols-spec-client-3.0.jar -Dpackaging=jar -DgeneratePom=true
mvn install:install-file -DgroupId=com.complexible -DartifactId=stardog-versioning-shared -Dversion=3.0 -Dfile=$STARDOG_HOME/client/api/stardog-versioning-shared-3.0.jar -Dpackaging=jar -DgeneratePom=true
mvn install:install-file -DgroupId=com.complexible -DartifactId=stardog-icv-protocols-shared -Dversion=3.0 -Dfile=$STARDOG_HOME/client/snarl/stardog-icv-protocols-shared-3.0.jar -Dpackaging=jar -DgeneratePom=true
mvn install:install-file -DgroupId=com.complexible -DartifactId=stardog-icv-protocols-snarl-client -Dversion=3.0 -Dfile=$STARDOG_HOME/client/snarl/stardog-icv-protocols-snarl-client-3.0.jar -Dpackaging=jar -DgeneratePom=true
mvn install:install-file -DgroupId=com.complexible -DartifactId=stardog-icv-protocols-snarl-shared -Dversion=3.0 -Dfile=$STARDOG_HOME/client/snarl/stardog-icv-protocols-snarl-shared-3.0.jar -Dpackaging=jar -DgeneratePom=true
mvn install:install-file -DgroupId=com.complexible -DartifactId=stardog-reasoning-protocols-shared -Dversion=3.0 -Dfile=$STARDOG_HOME/client/snarl/stardog-reasoning-protocols-shared-3.0.jar -Dpackaging=jar -DgeneratePom=true
mvn install:install-file -DgroupId=com.complexible -DartifactId=stardog-reasoning-protocols-snarl-client -Dversion=3.0 -Dfile=$STARDOG_HOME/client/snarl/stardog-reasoning-protocols-snarl-client-3.0.jar -Dpackaging=jar -DgeneratePom=true
mvn install:install-file -DgroupId=com.complexible -DartifactId=stardog-reasoning-protocols-snarl-shared -Dversion=3.0 -Dfile=$STARDOG_HOME/client/snarl/stardog-reasoning-protocols-snarl-shared-3.0.jar -Dpackaging=jar -DgeneratePom=true
mvn install:install-file -DgroupId=com.complexible -DartifactId=stardog-search-protocols-snarl-client -Dversion=3.0 -Dfile=$STARDOG_HOME/client/snarl/stardog-search-protocols-snarl-client-3.0.jar -Dpackaging=jar -DgeneratePom=true
mvn install:install-file -DgroupId=com.complexible -DartifactId=stardog-versioning-protocols-shared -Dversion=3.0 -Dfile=$STARDOG_HOME/client/snarl/stardog-versioning-protocols-shared-3.0.jar -Dpackaging=jar -DgeneratePom=true
mvn install:install-file -DgroupId=com.complexible -DartifactId=stardog-versioning-protocols-snarl-client -Dversion=3.0 -Dfile=$STARDOG_HOME/client/snarl/stardog-versioning-protocols-snarl-client-3.0.jar -Dpackaging=jar -DgeneratePom=true
mvn install:install-file -DgroupId=com.complexible -DartifactId=stardog-versioning-protocols-snarl-shared -Dversion=3.0 -Dfile=$STARDOG_HOME/client/snarl/stardog-versioning-protocols-snarl-shared-3.0.jar -Dpackaging=jar -DgeneratePom=true
mvn install:install-file -DgroupId=com.complexible -DartifactId=stardog-watchdog-protocols-shared -Dversion=3.0 -Dfile=$STARDOG_HOME/client/snarl/stardog-watchdog-protocols-shared-3.0.jar -Dpackaging=jar -DgeneratePom=true
mvn install:install-file -DgroupId=com.complexible -DartifactId=stardog-watchdog-protocols-snarl-client -Dversion=3.0 -Dfile=$STARDOG_HOME/client/snarl/stardog-watchdog-protocols-snarl-client-3.0.jar -Dpackaging=jar -DgeneratePom=true
mvn install:install-file -DgroupId=com.complexible -DartifactId=stardog-watchdog-protocols-snarl-shared -Dversion=3.0 -Dfile=$STARDOG_HOME/client/snarl/stardog-watchdog-protocols-snarl-shared-3.0.jar -Dpackaging=jar -DgeneratePom=true
mvn install:install-file -DgroupId=com.complexible -DartifactId=stardog-watchdog-shared -Dversion=3.0 -Dfile=$STARDOG_HOME/client/snarl/stardog-watchdog-shared-3.0.jar -Dpackaging=jar -DgeneratePom=true
