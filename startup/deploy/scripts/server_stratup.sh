#!bin/bash

strat_time=$(date "+%Y-%m-%d %H:%M:%S")
echo "=========自动部署后端开始:"${time_time} >>~/runtime/log/ae.server.start.log &2>>1
cd ~/runtime
if [ -f ./ancient-empire-server/ancient-empire-server.jar ]; then
    ps -ef|grep ancient-empire-server.jar|grep -v grep|awk '{print $2}'|xargs kill -9
    nohup java -jar -Dfile.encoding=UTF-8 ./ancient-empire-server/ancient-empire-server.jar jfile=config/application.properties >/dev/null &2>1
    strat_time=$(date "+%Y-%m-%d %H:%M:%S")
    echo "=========自动部署成功:"${time_time} >>~/runtime/log/ae.server.start.log &2>>1
else
    echo "打包失败文件不存在"
fi