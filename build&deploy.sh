#!/bin/bash

mvn clean package -DskipTests


if [ $? -ne 0 ]; then
  echo "Build failed"
  exit 1
fi

echo 'Removing old api deploy'
rm -rf /home/kerty/tomcat9/webapps/api.war /home/kerty/tomcat9/webapps/api

echo 'Deploying new api'
cp webapp/target/webapp.war  /home/kerty/tomcat9/webapps/api.war
