一、项目介绍

流马测试平台是一款低代码的自动化测试工具平台，支持API/WEB/APP(APP开发中)自动化测试。演示平台地址: http://demo.liumatest.cn <br>
为更好的随时随地、自由切换地支持自动化测试，流马测试平台分为管理平台和测试引擎两个项目。本项目即为管理平台前后端代码，前端项目为vue+elementUI、后端项目为java+springboot。<br>

二、功能介绍
1. API测试
```
(1) 支持单接口测试和链路测试。
(2) 支持接口统一管理，支持postman/swagger导入。
(3) 支持一键生成字段校验的接口健壮性用例。
(4) 支持全局变量、关联、断言、内置函数、自定义函数。
(5) 支持前后置脚本、失败继续、超时时间、等待/条件/循环等逻辑控制器。
(6) 支持环境与用例解耦，多种方式匹配域名，让一套用例可以在多个环境上执行。
```

2. WebUI测试
```
(1) 支持关键字驱动，零代码编写用例。
(2) 支持UI元素统一管理，Excel模板批量导入。
(3) 支持自定义关键字，封装公共的操作步骤，提升用例可读性。
(4) 支持本地引擎执行，实时查看执行过程。
(5) 支持与API用例在同一用例集合顺序执行。
```

3. AppUI测试
```
(1) 支持WebUI同等用例编写和执行能力
(2) 支持安卓和苹果系统
(3) 支持持真机管理、投屏和在线操作
(4) 支持控件元素在线获取，一键保存元素
(5) 支持实时查看执行过程
```
三、开发环境

环境依赖: nodejs 14、java 1.8、mysql 8 <br>
IDE推荐: vue使用VSCode、java使用IDEA <br>
启动项目: <br>

前端<br>
第一步: 切换目录，cd Liuma-platform/LiuMa-frontent <br>
第二步: 安装依赖，npm install <br>
第三步: 启动项目，npm run dev <br>
启动后，浏览器打开登录页，构建成功 <br>

后端<br>
第一步: IDEA打开目录 Liuma-platform/LiuMa-backend <br>
第二步: 使用maven安装依赖 <br>
第三步: 新建数据库名: liuma <br>
第四步: 配置application.properties数据库连接 <br>
第五步: 配置阿里云邮件和七牛云存储相关信息(可以先跳过 不影响启动 但无法发送邮件和保存截图 配置见后面) <br>
第六步: 启动LiuMaApplication文件 <br>
首次启动后会创建相关数据表和基础数据，启动成功后，查看数据库liuma，所有数据表均已初始化成功 <br><br>

验证启动成功<br>
项目启动后，默认会新建两个用户: 系统管理员LMadmin/Liuma@123456、演示项目的项目管理员demo/123456 <br>
使用上述初始用户登录平台，登录成功后，即表示项目启动成功 <br>

三、第三方服务

阿里云邮件<br>
主要用于计划执行后发送邮件 可用公司邮箱替换 需自行开发 <br>
配置步骤: <br>
第一步: 注册阿里云账号 申请开通邮件服务 <br>
第二步: 获取accessKey/accessSecret 设置邮件发送人 <br>
第三步: 将以上信息填写在文件~/application.properties对应位置 <br>
注: 阿里云邮件每天有200封免费额度，超过则需要计费(价格不贵)，如果不愿意使用可对接公司自己的邮箱 <br>

七牛云存储<br>
主要用于存放WEB测试执行过程截图 公司有文件存储服务可以替换 需自行开发 <br>
配置步骤: <br>
第一步: 注册七牛云账号 开通空间存储服务 <br>
第二步: 创建存储空间bucket 获取ak/sk信息 同时获取加速域名(可以先试用测试域名 后期配置自定义域名) <br>
第三步: 将以上信息填写在文件~/application.properties对应位置 <br>
注: 七牛云存储每个用户有10G免费空间容量，超过则需要计费(价格不贵)，如果不愿意使用也可用公司自己的文件存储服务 <br>
