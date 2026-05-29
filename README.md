# vita
> vita 拉丁语：生命

# 项目介绍
> 项目描述：个人生活综合项目 — 基于 Spring Cloud 微服务架构的个人生活综合管理平台

# 项目模块树形图
```
├── README.md
├── vita-api          // 外部交互服务 — SMTP邮件发送
├── vita-auth-center  // 认证中心服务 — 用户/角色管理、登录/登出、Token管理
├── vita-common       // 公共包（包含全局异常等SPI注入处理）
├── vita-gateway      // 网关服务 — WebFlux认证过滤、路由转发
├── vita-message      // 消息服务 — 邮件编排 + 发送记录持久化
├── vita-im           // IM服务 — WebSocket即时通讯（早期开发阶段）
├── docker-compose.yml
└── pom.xml
```

# 架构图

## 请求流转架构

```
客户端请求
    ↓
vita-gateway (8888)
    ├── UrlTokenToHeaderFilter → URL中提取Token转为Authorization头
    ├── AuthFilter → 验证Token(Redis)、白名单放行、注入UserInfo头
    ↓ 路由转发（Nacos管理路由配置）
    ├── vita-auth-center (8033) → 用户/角色操作
    ├── vita-message (8043) → 邮件操作
    │       ↓ Feign调用 (CommonFeignInterceptor传播traceId+UserInfo)
    │       vita-api (8023) → 实际SMTP邮件发送
    └── vita-im (8043) → WebSocket即时通讯
```

## 认证鉴权架构（多层防御）

```
第一层 — 网关认证 (AuthFilter):
    健康检查路径直接放行
    Nacos白名单放行
    其他请求必须携带 Authorization 头 (Token格式 vita.{UUID})
    Token验证 → Redis查询 → 成功则续期+注入UserInfo头

第二层 — 服务级权限拦截 (AuthPermissionHandlerInterceptor):
    检查 @AuthPermission 注解
    比对用户角色与注解要求 (COMMON_ROLE / ROOT_ROLE)
    权限不足返回 405

第三层 — Feign调用上下文传播 (CommonFeignInterceptor):
    服务间调用自动传播 traceId 和 UserInfo 头
```

## RBAC权限模型

```
User → UserRoleRel → Role → RoleInterface → InterfacePermission
```
- `data_auth` 字段：OWNER(仅本人数据) / ALL(全部数据)
- 接口级权限基础设施已存在，当前通过 `@AuthPermission` 注解实现角色校验

# 功能模块

## 认证中心服务
1. 用户、角色
   - 网关 - 校验健康检查、白名单、是否登录
   - 角色权限 - 拦截器+注解校验具体角色

### 用户管理

| API | 方法 | 路径 | 功能 | 权限 |
|-----|------|------|------|------|
| 用户登录 | POST | `/user/login` | 邮箱+密码登录，MD5校验，生成Token存入Redis(3天TTL) | 无 |
| 用户信息 | GET | `/user/info` | 获取当前登录用户信息 | 已登录 |
| 用户信息(指定Token) | GET | `/user/info/{token}` | 通过Token查询指定用户信息 | 已登录 |
| 用户登出 | GET | `/user/logout` | 清除Redis中该用户所有Token | 已登录 |
| 用户列表 | POST | `/user/list` | 分页查询用户（支持邮箱/手机/用户名筛选） | ROOT_ROLE |

### 角色管理

| API | 方法 | 路径 | 功能 | 权限 |
|-----|------|------|------|------|
| 角色列表 | GET | `/role/list` | 查询所有角色 | ROOT_ROLE |
| 角色关联 | POST | `/role/associate` | 为用户分配角色（先删后插） | ROOT_ROLE |

### Token管理机制
- Token格式: `vita.{UUID}`
- Token存储: Redis键 `user:token:{token}` → UserInfoBO JSON，TTL 3天
- 多会话追踪: Redis键 `user:online:{userId}` → Set<所有活跃Token>
- 密码加密: MD5哈希 `vita.{原始密码}`
- Token续期: 每次网关请求自动延长Token TTL
- 登出: 删除用户所有Token（多设备同时登出）

## 消息服务
1. 邮件发送（邮件发送 → 发送记录存储）

### 邮件发送流程

```
客户端 POST /email/send
    → vita-message EmailFacadeImpl
        → MailFeignClient.send() → vita-api MailServiceImpl (SMTP发送)
        → vitaCommonTaskExecutor 异步保存 EmailRecord 到 MySQL
```

| API | 方法 | 路径 | 功能 |
|-----|------|------|------|
| 发送邮件 | POST | `/email/send` | 编排邮件发送：Feign调用vita-api发送 → 异步持久化发送记录 |
| 邮件发送 | POST | `/mail/send` | 通过JavaMailSender发送MimeMessage(支持HTML) — vita-api |

参数校验: receiver(必填,≤64字符)、subject(必填,≤255字符)、content(必填)

## 网关服务
- UrlTokenToHeaderFilter: 特定URL模式将路径末尾Token转为Authorization头（支持URL携带Token场景如WebSocket/文件下载）
- AuthFilter: 全局认证过滤，白名单放行、Token验证、UserInfo注入
- CorsConfig: 允许所有来源、头部、方法，支持凭证

## IM服务（早期开发阶段）
- 已有 IMApplication 入口类 + WebSocket依赖
- Feign/MapperScan注解已注释
- 无Controller/Handler实现

## 公共包 (vita-common)

### SPI自动配置（注入所有Servlet服务）

通过 `META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports` 注册：

| 自动配置类 | 注册内容 |
|-----------|---------|
| `CommonAutoConfiguration` | GlobalExceptionHandler + HealthController |
| `VitaAutoConfiguration` | TraceId拦截器 + AuthPermission拦截器（仅Servlet服务） |
| `ThreadConfiguration` | vitaCommonTaskExecutor线程池 |
| `FeignAutoConfiguration` | FeignClient扫描 + CommonFeignInterceptor |

### 通用功能
- 全局异常处理（ServiceException、参数校验异常、兜底异常）
- TraceId分布式追踪（MDC + 请求头传播）
- AuthPermission注解鉴权拦截
- Feign调用上下文传播
- 健康检查端点 (`GET /health/check` 返回服务名 + "is healthy")
- 通用线程池配置（CPU核心数动态计算）
- 领域DTO/VO/BO定义
- FeignClient（AuthCenterClient）
- 工具类（AssertUtil、TokenUtil）

# 注意
1. 涉及权限的改动必须下线所有用户
2. 所有运行时配置存储在Nacos，本地无 application.yml / bootstrap.yml
3. vita-common通过SPI机制自动注入，无需手动配置即可生效
4. 网关使用WebFlux响应式栈（非Servlet），vita-common的拦截器仅对Servlet服务生效
5. 密码采用MD5+前缀加密，Token采用UUID+前缀格式

# 技术栈

| 分类 | 技术 | 版本 |
|------|------|------|
| 语言 | Java (JDK) | 17 |
| 核心框架 | Spring Boot | 3.5.9 |
| 微服务框架 | Spring Cloud | 2025.0.0 |
| 微服务组件 | Spring Cloud Alibaba (Nacos) | 2025.0.0.0 |
| 网关 | Spring Cloud Gateway (WebFlux) | — |
| ORM | MyBatis Plus | 3.5.15 |
| 对象映射 | MapStruct | 1.5.5.Final |
| 服务调用 | OpenFeign | — |
| 缓存 | Redis (Spring Data Redis) | — |
| 邮件 | Spring Boot Mail (JavaMailSender) | — |
| WebSocket | Spring Boot WebSocket | — (仅IM模块) |
| 工具库 | Hutool | 5.8.43 |
| JSON | Gson | — |
| 验证 | Jakarta Validation | — |
| 模板引擎 | Velocity | 2.4.1 (代码生成) |

---
## 中间件
- nacos 3.0.3
- mysql 8.0.28
- redis 6+

## 服务端口

| 服务 | 端口 |
|------|------|
| vita-gateway | 8888 |
| vita-auth-center | 8033 |
| vita-api | 8023 |
| vita-message | 8043 |
| vita-im | 8043 |

# 分布式设计

## 日志体系
- 每个服务独立 logback-spring.xml
- 日志分离: info日志 + error日志独立文件
- 滚动策略: 50MB大小触发滚动
- Profile行为: dev仅控制台、test/prod控制台+文件
- 日志格式包含traceId用于问题追踪

# 项目部署方式

## 环境依赖

| 依赖 | 版本要求 |
|------|---------|
| JDK | 17+ |
| Maven | 3.6+ |
| Docker | 20+ |
| Docker Compose | v2+ |
| Nacos | 3.0.3 |
| MySQL | 8.0.28 |
| Redis | 6+ |

## Nacos配置

在Nacos配置中心（命名空间 vita-test）配置以下内容：

- 各服务 application.yml（数据源、Redis连接、服务端口等）
- 网关路由规则
- `interface.whiteList`（认证白名单路径）
- `interface.urlTokenToHeaderList`（URL Token转换路径）
- SMTP邮件配置（host、port、username、password）
- 日志配置

> [docker && docker compose 命令](https://docs.docker.com/engine/reference/commandline/compose/)

## Docker Compose部署

```shell
# 启动服务
docker-compose -f docker-compose.yml up -d
# 停止服务
docker-compose -f docker-compose.yml stop
# 停止并删除服务
docker-compose -f docker-compose.yml down
```

## 健康检查

```bash
# 网关健康检查
curl http://localhost:8888/health/check

# 各服务健康检查（需在白名单中配置）
curl http://localhost:8033/health/check
curl http://localhost:8023/health/check
curl http://localhost:8043/health/check
```

# 开发状态总结

| 模块 | 状态 |
|------|------|
| vita-common | ✅ 完整实现 |
| vita-gateway | ✅ 完整实现 |
| vita-auth-center | ✅ 完整实现 |
| vita-api | ✅ 部分实现（邮件发送） |
| vita-message | ⚠️ 早期阶段（仅有依赖和入口类） |
| vita-im | ⚠️ 早期开发（仅有依赖和入口类，核心注解已注释） |

**未实现功能**:
- 数据级权限 `data_auth` 未在代码中强制执行
- 接口级权限 `sys_interface_permission` 基础设施存在但未在Controller层使用
- IM即时通讯功能未实现
- 前端项目未包含在当前仓库