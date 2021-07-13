#!bin/bash

strat_time=$(date "+%Y-%m-%d %H:%M:%S")
echo "=========自动部署后端开始:"${time_time} >>~/runtime/log/ae.server.start.log &2>>1
cd ~/runtime/ancient-empire-server
if [ -f ./ancient-empire-server.jar ]; then
    active_app=`ps -ef|grep ancient-empire-server.jar|grep -v grep|awk '{print $2}'`
    if [ "$active_app" ];
    then
        kill -9 ${active_app}
    fi
    nohup java -jar -Dfile.encoding=UTF-8 ./ancient-empire-server.jar jfile=config/application.properties >/dev/null &2>~/runtime/log/ae.server.start.log
    strat_time=$(date "+%Y-%m-%d %H:%M:%S")
    echo "=========自动部署成功:"${time_time} >>~/runtime/log/ae.server.start.log &2>>1
else
    echo "打包失败文件不存在"
fi