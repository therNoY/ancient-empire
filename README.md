

# ancient-empire 说明

ancient-empire 是项目远古帝国网页版 的后端项目(现在该项目在持续开发中.......)

# 设计

## 1.用户管理

## 2.地图管理

## 3.游戏管理

### 1.游戏的核心控制

+ 游戏的核心控制是基于事件指令的;

+ 客户端（web前端，移动前端...）根据用户的操作发出事件(事件集)，后端处理事件，生成指令(指令集)，
前端再根据指令做出相应的渲染;

+ 其中后端维护一局游戏的生命周期，即GameContext 维护在GameContextManger中;

+ 客户端对操作做出校验，校验成功通过simp(WS)发出事件，后端接受事件，封装成事件对象，分发到相应的GameContext，
GameContext获取对应事件的处理器，处理事件，返回结果，生成相应指令；

### 2.数据库选择（为什么用mongo+mysql+redis）游戏单位
+ 使用redis作为数据缓存服务器是目前项目中基本都会有的的，mysql作为开源免费的开源数据库也是大家常见的

+ 为什么要使用mongoDB呢?
    + 从数据存储上来说游戏中每个单位有自己的信息 有单位基础信息(UnitMes)，单位等级信息(UnitLevelMes), 单位能力信息(UnitAbility)三个信息组成；
    这些数据适合保存再关系型数据库中；
    + 这三个信息组成的只是单位的客观信息, 只是一个抽象的单位，如果要建立一个活跃的单位，需要单位的地图位置，
    单位的阵营(颜色), 单位的等级，单位的血量，单位的状态（buff），这些是一局游戏中单位的信息，是“活着的”单位,目前这些是由Unit类描述的，
    一局游戏中这些信息是时刻都在变化的，关键是除了这些信息，还有军队的信息（军队除了是单位的集合，还有自己的信息，比如金钱，颜色，人口等），
    军队的信息有Army类描述；这些在与“存活的”单位关联，然后上层才是一个游戏快照的信息，有多个军队组成，当然也有自己的信息；说了这么多，可以想想这个关联有多麻烦
    保存信息有多复杂，对于这样一个典型的树状json型的数据，有mongoDB来保存自然是最好的。





### 2.事件指令模型的好处
+ 可以不关心客户端，只需要接受事件，处理事件，方便多个平台

+ 便于联机，很多时候一个指令是需要发送到多个客户端显示的

## 4.联机管理

暂无

## 5.人机管理

### 1.人机操作
+ 当GameContextManger判断当前回合是人机回合的时候，会新生成一个GameRobot 对象
这是一个线程，从创建到结束取决于自身的逻辑


## 5.客户端管理

+ 客户端抽离出公共部分，包括对命令的解析等

+ 网页web使用VUE


## 6.扩展

+ 因为一个应用维护的都是有状态的（维护了不同的GameContext） 如果无状态需要频繁操作数据库，
且前端也每次都要传大Map过来，暂时不扩展

+ 模块化
    + common 通用模块
    + auth 人员管理及验证模块
    + base 基础模块，维护地图信息等
    + core 维护核心处理模块
        + 网络请求
    + robot 机器人模块
    + startup 启动模块
    

## 7.其他

#### 事件集




#### 2020/9/1-2020/10/28 历时一个半月项目重构计划
1.HTML (9/1-9/6) √ 
  标签 √
  属性 √
  dom √
2.项目代码模块化(9/7-9/9) (√)

3.定义好事件，指令，状态(这些肯定不能目前全部定义好，但是要能定个大概，要能够支持方便扩展)(9/10-9/11)
	状态管理() GameContextManger 状态 GameContext 主状态，子状态 Robot 状态

4.JS 
  高级函数编程  (9/12-9/15)
	ES6
	线程，基于阻塞队列做一个取指令的东西
  Node.js (9/16-9/20) 
	node.js和那npm关系 https://blog.csdn.net/hong10086/article/details/85062678
	事件循环？
	npm 包管理
5.CSS (9/21-9/24) (重写CSS)
6.Vue 特性复习 (9/25-9/27)

7.思考可扩展性,以及高可用性 （9/28）
8.理解stomp协议 用到了(tcp/ip, udp, http, websocket 都理解清楚) （9/29-10/8）
	计算机网络
	网络安全（对称加密，非对称加密）
	--tag 整理至百度脑图

9. mongoDB 要用就要好好用 （10/9-10/16）
	--tag 整理至百度脑图 （10/17）

10.后续整理，至此全部重构完成（10/18)



# ancient-empire 前端项目地址
https://github.com/therNoY/ancient-empire-web/

现在还没有开发完成, 只有部分截图 不能体验截图在前端项目可看




### 其他技术
项目使用mybatis-plus 作为持久层框架
使用spring-security 和 jwt 结合作为用户身份验证的框架
使用了spring-mail来处理邮件， 使用spring-boot-websocket 处理基于stomp的ws请求连接
