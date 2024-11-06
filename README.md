# vita
> vita 拉丁语：生命

# 项目介绍
> 项目描述：个人生活综合项目

# 项目模块树形图
├── README.md   
├── vita-api   // 外部交互服务   
├── vita-auth-center   // 认证中心服务   
├── vita-common    // 公共包（包含全局异常等SPI注入处理）  
├── vita-gateway   // 网关服务     
├── vita-message   // 消息服务
├── docker-compose.yml  
└── pom.xml

# 架构图

# 功能模块
## 认证中心服务
1. 用户、角色、权限（菜单权限、接口权限、数据权限）[权限均与角色关联]

## 消息服务
1. 邮件发送（邮件发送 -> 发送记录存储）

# 注意
1. 涉及权限的改动必须下线所有用户

# 技术栈
- jdk 8
- Springboot 2.7.12
- SpringCloud 2021.0.6
- SpringCloud Alibaba 2021.0.4.0
- knife4j 4.1.0
- mybatis-plus 3.5.3

---
## 中间件
- nacos 2.4.0
- mysql 8.0.13

# 项目部署方式
> [docker && docker compose 命令](https://docs.docker.com/engine/reference/commandline/compose/)
- docker
- docker-compose
> 常用命令
```shell
# 启动服务
docker-compose -f docker-compose.yml up -d
# 停止服务
docker-compose -f docker-compose.yml stop
# 停止并删除服务
docker-compose -f docker-compose.yml down
```
