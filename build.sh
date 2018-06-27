#!/bin/bash

CLASSPATH="../lib/aws-java-sdk-1.11.245.jar"

echo ""
echo "building data-backr..."

# clean up old files
echo "removing class files"
rm *.class

# compile java file
cd src
# javac -cp $CLASSPATH ArchiveUpload.java
javac -cp $CLASSPATH *.java

# move class file to base dir
echo "move class files to base folder"
mv *.class ..

# return to base dir
cd ..
echo "build finished!!!"
echo ""
