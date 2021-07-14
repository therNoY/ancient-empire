cd ~/runtime
PATH=/usr/jdk1.8.0_211/bin:/root/workspace/tools/apache-maven-3.0.5/bin:/root/workspace/runtime/apache-tomcat-8.5.43/:/usr/jdk1.8.0_211/bin:/root/workspace/tools/apache-maven-3.0.5/bin:/root/workspace/apache-tomcat-8.5.43/:/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/root/bin
TOMCAT_PATH=/root/workspace/runtime/apache-tomcat-8.5.43/

dos2unix ./scripts/server_stratup.sh
/bin/bash ./scripts/server_stratup.sh

