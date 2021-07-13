#!bin/bash

cd ~/workspace/ancient-empire
strat_time=$(date "+%Y-%m-%d %H:%M:%S")
echo "=========更新后端代码开始:"${strat_time} >>~/runtime/log/ae.server.start.log &2>>1
git pull >>~/runtime/log/ae.server.start.log &2>>1
# 检测是否还在拉取代码中
i=60
while [[ $i -gt 0 ]];do
    sleep 2
    check_start=`ps -ef|grep pull|grep -v grep| awk '{printf $2}'`
    if [ -z "$check_start" ];
    then
        break
    fi
    ((i = i - 1))
done
strat_time=$(date "+%Y-%m-%d %H:%M:%S")
echo "=========更新后端代码开始结束:"${strat_time} >>~/runtime/log/ae.server.start.log &2>>1

strat_time=$(date "+%Y-%m-%d %H:%M:%S")
echo "=========maven构建tar包开始"${strat_time} >>~/runtime/log/ae.server.start.log &2>>1
mvn clean package >>~/runtime/log/ae.server.start.log &2>>1

i=30
while [[ $i -gt 0 ]];do
    sleep 2
    check_start=`ps -ef|grep maven|grep -v grep| awk '{printf $2}'`
    if [ -z "$check_start" ];
    then
        break
    fi
    ((i = i - 1))
done
strat_time=$(date "+%Y-%m-%d %H:%M:%S")
echo "=========maven构建tar包结束"${strat_time} >>~/runtime/log/ae.server.start.log &2>>1

fileName=`ls ./startup/target | grep tar.gz`
if [ -f ./startup/target/${fileName} ]; then
    echo "=========打包成功文件"${fileName}"开始移动文件" >>~/runtime/log/ae.server.start.log &2>>1
    rm -rf ~/runtime/${fileName} ~/runtime/ancient-empire-server
    mv ./startup/target/${fileName} ~/runtime/
    sleep 2
    cd ~/runtime
    tar -xzf ./${fileName}
    sleep 5
    dos2unix ./scripts/server_stratup.sh
    /bin/bash ./scripts/server_stratup.sh
else
    echo "=========打包失败文件不存在" >>~/runtime/log/ae.server.start.log &2>>1
fi

