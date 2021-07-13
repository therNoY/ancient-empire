echo "自动部署ae前端开始.........."
cd ./ancient-empire-web
git pull
npm install
rm -rf ./dist
npm run build

if [ -f ./dist/index.html ]; then
    echo "停止服务"
    ${TOMCAT_PATH}/bin/shutdown.sh
    rm -rf ${TOMCAT_PATH}/webapps/ROOT
    cp ./dist ${TOMCAT_PATH}/webapps/ROOT/
    nohup ${TOMCAT_PATH}/bin/startup.sh >../log/ae-web.log 2>../log/ae-web.log &
    echo "前端部署成功"
else
    echo "打包失败文件不存在 前端部署失败"
fi