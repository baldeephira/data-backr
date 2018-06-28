# data-backr

This project provides a client program that can be used to back up files to Amazon Glacier storage.  It uses the multi-part REST API and can takes a file list as input so that you can bootstrap this program to run overnight and upload all your large files into AWS Glacier storage overnight.  It creates a log file and outputs the start and end times for each file upload.  It outputs the archive IDs returned by the REST API so that they can be uses for subsequent fetch operations.

### BUILD INSTRUCTIONS
* clone data-backr repository
* ensure java 8 is installed
* cd data-backr
* ./build.sh

### CONFIGURATION
* configure aws environment details in ~/.aws/config file
* configure aws credentials in ~/.aws/credentials file
* configure file upload details in data-backr/archive.properties file

### AWS CONFIG FILE
```
[default]
region = us-west-2
output = json
```

### AWS CREDENTIALS FILE
```
[default]
aws_access_key_id = ABCDEFGHIXYZ
aws_secret_access_key = fd03ri2jrfw0r3nvn032r23rj239r23ns32trlknvkl
```

### ARCHIVE.PROPERTIES FILE
```
awsEndpoint=https://glacier.us-west-2.amazonaws.com/
vaultName=photos
uploadFiles= file1,file2,file3
```

### EXECUTION INSTRUCTIONS
* build the project
* ensure java 8 is installed
* cd data-backr
* vi archive.properties
* ./run.sh