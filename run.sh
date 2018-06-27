#!/bin/bash

# setup classpath var to include dependent libs
CLASSPATH="."
CLASSPATH="$CLASSPATH:./lib/aws-java-sdk-1.11.245.jar"
CLASSPATH="$CLASSPATH:./lib/commons-logging-1.2.jar"
CLASSPATH="$CLASSPATH:./lib/jackson-databind.jar"
CLASSPATH="$CLASSPATH:./lib/jackson-core-2.2.3.jar"
CLASSPATH="$CLASSPATH:./lib/jackson-annotations-2.1.2.jar"
CLASSPATH="$CLASSPATH:./lib/httpclient-4.5.4.jar"
CLASSPATH="$CLASSPATH:./lib/httpcore-4.4.7.jar"
CLASSPATH="$CLASSPATH:./lib/joda-time-2.9.9.jar"

CLASSNAME="ArchiveUpload"

# run main method in ArchiveUpload class
echo ""
java -cp $CLASSPATH $CLASSNAME $@
echo ""
