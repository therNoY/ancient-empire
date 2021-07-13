#!bin/bash
cd ~/workspace/ancient-empire
strat_time=$(date "+%Y-%m-%d %H:%M:%S")
echo "=========从远程仓库拉区最新代码开始:"$time_time >>~/runtime/log/ae.server.start.log &2>>1
git pull >>~/runtime/log/ae.server.start.log &2>>1
strat_time=$(date "+%Y-%m-%d %H:%M:%S")
echo "=========从远程仓库拉区最新代码结束:"$time_time >>~/runtime/log/ae.server.start.log &2>>1

cd ./startup
strat_time=$(date "+%Y-%m-%d %H:%M:%S")
echo "=========maven构建tar包开始"$time_time >>~/runtime/log/ae.server.start.log &2>>1
mvn clean >>~/runtime/log/ae.server.start.log &2>>1
mvn package >>~/runtime/log/ae.server.start.log &2>>1
strat_time=$(date "+%Y-%m-%d %H:%M:%S")
echo "=========maven构建tar包结束"$time_time >>~/runtime/log/ae.server.start.log &2>>1

