

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

+ 客户端对操作做出校验，校验成功通过websocket长连接发出事件，后端接受事件，封装成事件对象，发送到GameContext，
GameContext获取对应事件的处理器，处理事件，返回结果，生成相应指令；

+ 玩家的websocket连接管理 玩家先连接到服务器,单机验证完信息直接创建一个GameContext,

### 2.使用模板管理

+ 游戏的内容都是可以配置的，用户可以选择自己的角色动画，角色描述，甚至攻击动画，使用能力的动画，等等
当然这要求用户有一定的像素绘画能力（高端玩家，甚至可以选择单元格的宽度和高度）
+ 用户创建单位 模板可以复用

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





### 4.事件指令模型的好处
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
    + abstractRobot 机器人模块
    + startup 启动模块
    

## 7.其他

#### 事件集




#### 2020/9/1-2020/10/28 历时一个半月项目重构计划
##### 之前之所以搁置了很久就是代码架构不清晰，导致后面做到机器人的时候导致前端有很多类似逻辑，但是代码还不能重用.. 所以花了大概三天时间思考
##### 但是代码还不能重用.. 为了让这个项目还能继续做下去，毕竟花了这么长时间.. 所以花了大概三天时间思考准备重构事宜，这次重构包括架构，交互逻辑，
##### 同时在重构的时候复习之前的知识，或者学习新的知识（还是太菜了hah）;

+ 1.HTML (9/1-9/6) (复习) √ 
  + 标签 √
  + 属性 √
  + dom √

+ 2.项目代码模块化(9/7-9/9) (√) (重构)

+ 3.定义好游戏内的事件，指令，状态 (9/10-9/11) (定义模型)
	(这些肯定不能目前全部定义好，但是要能定个大概，要能够支持方便扩展)(9/10-9/11)
	状态管理() GameContextManger 状态 GameContext 主状态，子状态 Robot 状态

+ 4.JS (复习，编写模型，重构)
   + 高级函数编程  (9/12-9/15)
   + ES6
   + 线程，基于阻塞队列做一个取指令的东西
   + Node.js (9/16-9/20) 
   + node.js和那npm关系 https://blog.csdn.net/hong10086/article/details/85062678
   + 事件循环？
   + npm 包管理

5.CSS (9/21-9/24) 
	cass(学习，重写CSS)

6.Vue 特性复习 (9/25-9/27) (复习， 重构)

7.思考可扩展性,以及高可用性 （9/28） (思考， 定义模型)

http://hao.caibaojian.com.cn/81294.html
https://segmentfault.com/a/1190000017307713

------------------------------------------------------------------------------------

8.理解stomp协议 用到了(tcp/ip, udp, http, websocket 都理解清楚) （9/29-10/8）(学习)
	计算机网络
	网络安全（对称加密，非对称加密）
	Http -> WebScoket -> socketJs -> Stomp
	https://blog.csdn.net/weixin_42052388/article/details/80313752

	--tag 整理至百度脑图

9. mongoDB (要用就要好好用) （10/9-10/16）
	--tag 整理至百度脑图 （10/17）

10.后续整理，至此全部重构完成（10/18)



# ancient-empire 前端项目地址
https://github.com/therNoY/ancient-empire-web/

现在还没有开发完成, 只有部分截图 不能体验截图在前端项目可看




### 其他技术
项目使用mybatis-plus 作为持久层框架
使用spring-security 和 jwt 结合作为用户身份验证的框架
使用了spring-mail来处理邮件， 使用spring-boot-websocket 处理基于stomp的ws请求连接



### 回去验证stomp

+ 考虑增加随机酒馆功能


### 按规定日期修改bug (20210207-20210227)
1. 机器人模块 只修复 不占领 √
2. 机器人模块 目标移动的路线 不合理 应该计算 移动的最短距离 而不是直线最短 ===》 无向有权图两点之间的最短距离 迪杰斯特拉算法 √
3. 机器人模块 购买单位优化 单位最低需要的占领城镇兵的数量 和 远程兵的数量 （现在容易触发 选择攻击类型优先的兵种） √
4. 机器人模块 优化单位行动优先级 修改占领 和 攻击的优先级 √
5. 机器人模块 优化远程单位选择合适的地点进行召唤和攻击 （现在是选择最近的点）√
6. 核心模块 处理升级 √
7. 机器人模块 修改单位只修复不占理


### 按规定日期修改bug (20210228-20210314)
1. 读取游戏
    增加userRecord的游戏保存的类型
    1. 单机遭遇战
    2. 单机战役
    3. 联机遭遇战
    类型1，2 增加保存记录并退出 增加保存临时地图 在记录可以查到这个并继续游戏，最上面是一个临时地图
    
2. 多人游戏
    点击之后出现弹框（创建 搜索 刷新 房间列表 房间信息 退出 选择）
    
    创建房间 选择地图 设置初始属性 创建房间（生产房间号） 生成room对象
    创建房间号之后 连接ws 1.房主退出摧毁(刷新网页, 或者返回) 房主主动销毁 （点击退出） 超时摧毁
    如果房间还有其他人 其他人成为房主
    人加入可以聊天
### 按规定日期完成房间任务 (20210314-20210331)   
1. 设计房间 room表
2. 可以加入房间判断 
    
### 按规定日期完成剩余主要任务 (20210407-20210505)
1. 房间开始游戏，多人游戏完成 √
2. 优化gameCoreIndex页面
3. 单人可以保存遭遇战进度,并且可以从进度开始游戏 √
4. 支持故事模式，并支持基础故事（支持天堂之怒）

#### 至20210417
### 发现的缺陷
1. 单位升级有问题 没有正确升级
2. 单位购买 有问题 领主死亡不能购买
3. 单位召唤 有问题 都有召唤的action

4. 机器人选择占领和修复 优先级不够

5. 修改单位信息报错

6. 单位移动到地图边缘有问题


### 未完成功能

1. 战役模式 
2. 新建单位功能 上传
3. 单位模板和地图增加版本控制, 模板使用的是一个版本的单位 地图使用的是一个版本的模板 
4. 优化主页界面(聊天使用同一个页面)


