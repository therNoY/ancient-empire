# ancient-empire 说明

ancient-empire 是项目远古帝国网页版 的后端项目(现在该项目在持续开发中)

# 技术框架

后端采用spring boot作为技术支持框架

# 持久层框架

使用的到的其他技术有mybatis-plus 作为持久层框架

https://mp.baomidou.com

# 安全验证

使用spring-security 和 jwt 结合作为用户身份验证的框架

https://spring.io/projects/spring-security

# MQ

使用到消息中间件rabbitmq 来解决异步消息日志处理

https://www.rabbitmq.com/

# 数据库/缓存

数据库使用了mysql mongoDB 缓存使用了reids
(都是有具体的使用场景, 不为单纯的凑出所有的主流技术)


# 其他技术

使用了spring-mail来处理邮件， 使用spring-boot-websocket 处理基于stomp的ws请求连接
