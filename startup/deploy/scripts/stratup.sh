#!bin/bash
echo "自动部署ae后端开始"
if [ -f ./startup/target/startup-0.0.1-SNAPSHOT.jar ]; then
    echo "停止后端服务"
    ps -ef|grep startup.jar|awk '{print $2}'|xargs kill -9
    rm -rf ../runtime/startup.jar
    cp ./startup/target/startup-0.0.1-SNAPSHOT.jar ../runtime/startup.jar
    cd ..
    nohup java -jar ./runtime/startup.jar >ancient-empire.log 2>ancient-empire.log &
    echo "后端服务重新部署成功"
else
    echo "打包失败文件不存在"
fi