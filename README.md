# Vita

> **vita** — 拉丁语「生命」

基于 Spring Cloud 微服务架构的**个人生活综合管理平台**（当前版本 **2.0.0**）。

---

## 目录

- [项目模块](#项目模块)
- [架构设计](#架构设计)
- [功能模块与 API](#功能模块与-api)
- [技术栈](#技术栈)
- [配置说明](#配置说明)
- [本地开发](#本地开发)
- [部署](#部署)
- [开发状态](#开发状态)

---

## 项目模块

```
vita/
├── vita-common        # 公共包 — 全局异常、SPI 自动配置、Feign 客户端、通用 DTO/工具
├── vita-gateway       # 网关 — WebFlux 认证过滤、路由转发、TraceId 注入
├── vita-auth-center   # 认证中心 — 用户/角色管理、登录登出、Token 管理
├── vita-api           # 外部交互 — SMTP 邮件实际发送
├── vita-message       # 消息服务 — 邮件编排 + 发送记录持久化
├── vita-im            # IM 服务 — WebSocket 即时通讯（基础能力已实现）
├── docker-compose.yml         # 应用服务编排
├── docker-compose-basic.yml   # 基础设施（MySQL / Redis / Nacos）
└── pom.xml
```

---

## 架构设计

### 请求流转

```
客户端
  ↓
vita-gateway (:8888)
  ├── TraceFilter          → 生成 traceId 并写入请求头
  ├── UrlTokenToHeaderFilter → URL 末尾 Token 转为 Authorization 头
  ├── AuthFilter           → Token 校验（Redis）、白名单放行、注入 UserInfo 头
  ↓ 路由转发（Nacos / demo 本地路由）
  ├── vita-auth-center (:8033)  → 用户 / 角色
  ├── vita-message   (:8043)  → 邮件编排
  │     ↓ Feign（CommonFeignInterceptor 传播 traceId + UserInfo）
  │     vita-api (:8023)  → SMTP 实际发送
  └── vita-im        (:8053)  → WebSocket 即时通讯
```

### 网关路由前缀（demo 本地配置）

| 前缀 | 目标服务 | 示例 |
|------|---------|------|
| `/auth/**` | vita-auth-center | `POST /auth/user/login` |
| `/message/**` | vita-message | `POST /message/email/send` |
| `/api/**` | vita-api | `POST /api/mail/send` |
| `/im/**` | vita-im（WebSocket） | `ws://host:8888/im/ws/{token}` |

> 经网关访问时，路由会 StripPrefix 去掉前缀后再转发至下游服务。

### 认证鉴权（三层防御）

```
第一层 — 网关 AuthFilter
  · /health/check 直接放行
  · Nacos 白名单 interface.white-list 放行
  · 其余请求须携带 Authorization 头（Token 格式 vita.{UUID}）
  · Redis 校验 Token → 续期 → 注入 UserInfo 请求头

第二层 — 服务级 @AuthPermission 拦截器
  · 比对用户角色与注解要求（COMMON_ROLE / ROOT_ROLE）
  · 权限不足返回 HTTP 405

第三层 — Feign CommonFeignInterceptor
  · 服务间调用自动传播 traceId 与 UserInfo 头
```

### RBAC 权限模型

```
User → UserRoleRel → Role → RoleInterface → InterfacePermission
```

- `data_auth` 字段：`OWNER`（仅本人数据）/ `ALL`（全部数据），角色 CRUD 已支持配置，业务层尚未强制执行
- 接口级权限基础设施已存在，当前主要通过 `@AuthPermission` 注解做角色校验

---

## 功能模块与 API

### 认证中心（vita-auth-center）

#### 用户接口 — `/user`

| API | 方法 | 路径 | 功能 | 权限 |
|-----|------|------|------|------|
| 用户登录 | POST | `/user/login` | 邮箱 + 密码登录，MD5 校验，生成 Token 存入 Redis（3 天 TTL） | 无 |
| 用户信息 | GET | `/user/info` | 获取当前登录用户信息 | 已登录 |
| 用户信息（指定 Token） | GET | `/user/info/{token}` | 通过 Token 查询指定用户信息 | 已登录 |
| 用户登出 | GET | `/user/logout` | 清除 Redis 中该用户所有 Token（多设备同时登出） | 已登录 |

#### 用户运维接口 — `/user/ops`（ROOT_ROLE）

| API | 方法 | 路径 | 功能 |
|-----|------|------|------|
| 用户分页列表 | POST | `/user/ops/page` | 分页查询未删除用户（支持邮箱/手机/用户名筛选） |
| 新增用户 | POST | `/user/ops/create` | 创建用户 |
| 编辑用户 | POST | `/user/ops/update` | 更新用户信息 |
| 删除用户 | POST | `/user/ops/delete` | 逻辑删除用户 |
| 用户详情 | GET | `/user/ops/detail/{id}` | 查询用户详情（含角色） |

#### 角色接口 — `/role`（ROOT_ROLE）

| API | 方法 | 路径 | 功能 |
|-----|------|------|------|
| 角色分页列表 | POST | `/role/page` | 分页查询角色 |
| 新增角色 | POST | `/role/create` | 创建角色（含 data_auth） |
| 编辑角色 | POST | `/role/update` | 更新角色 |
| 删除角色 | POST | `/role/delete` | 逻辑删除角色 |
| 角色关联 | POST | `/role/associate` | 为用户分配角色（先删后插） |

#### Token 机制

| 项 | 说明 |
|----|------|
| Token 格式 | `vita.{UUID}` |
| Redis 键 | `user:token:{token}` → UserInfoBO JSON，TTL 3 天 |
| 多会话追踪 | `user:online:{userId}` → Set\<活跃 Token\> |
| 密码加密 | MD5(`vita.{原始密码}`)，统一由 `PasswordUtil.encodePassword()` 处理 |
| Token 续期 | 每次经网关的认证请求自动延长 TTL |

---

### 消息服务（vita-message）

#### 邮件发送流程

```
POST /email/send
  → EmailFacadeImpl
      → MailFeignClient.send()  → vita-api MailServiceImpl（SMTP）
      → vitaCommonTaskExecutor 异步持久化 EmailRecord 至 MySQL
```

| API | 方法 | 路径 | 功能 |
|-----|------|------|------|
| 发送邮件 | POST | `/email/send` | 编排发送 + 异步落库 |

参数校验：`receiver`（必填，≤64）、`subject`（必填，≤255）、`content`（必填）

---

### 外部交互（vita-api）

| API | 方法 | 路径 | 功能 |
|-----|------|------|------|
| 邮件发送 | POST | `/mail/send` | JavaMailSender 发送 MimeMessage（支持 HTML） |

---

### IM 服务（vita-im）

- WebSocket 端点：`/ws/{token}`（经网关：`/im/ws/{token}`，需在 `url-token-to-header-list` 白名单中）
- 握手阶段通过 Feign 调用认证中心校验 Token，将会话绑定 userId
- 已实现：连接管理、文本消息广播（排除发送者）、消息 ACK
- 待完善：点对点私聊、文件/音视频分片传输、消息持久化

---

### 公共包（vita-common）

通过 `META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports` 自动注册：

| 自动配置类 | 内容 |
|-----------|------|
| `CommonAutoConfiguration` | GlobalExceptionHandler + HealthController |
| `VitaAutoConfiguration` | TraceId 拦截器 + AuthPermission 拦截器（仅 Servlet 服务） |
| `ThreadConfiguration` | vitaCommonTaskExecutor 线程池 |
| `FeignAutoConfiguration` | FeignClient 扫描 + CommonFeignInterceptor |

通用能力：全局异常处理、TraceId 分布式追踪、健康检查（`GET /health/check`）、领域 DTO/VO/BO、AuthCenterClient、AssertUtil / TokenUtil / PasswordUtil。

> 网关使用 WebFlux 响应式栈，vita-common 的 Servlet 拦截器**不对网关生效**。

---

## 技术栈

| 分类 | 技术 | 版本 |
|------|------|------|
| 语言 | Java (JDK) | 17 |
| 核心框架 | Spring Boot | 3.5.9 |
| 微服务 | Spring Cloud | 2025.0.0 |
| 微服务组件 | Spring Cloud Alibaba (Nacos) | 2025.0.0.0 |
| 网关 | Spring Cloud Gateway (WebFlux) | — |
| ORM | MyBatis Plus | 3.5.15 |
| 对象映射 | MapStruct | 1.5.5.Final |
| 服务调用 | OpenFeign | — |
| 缓存 | Redis (Spring Data Redis) | — |
| 邮件 | Spring Boot Mail | — |
| WebSocket | Spring Boot WebSocket | —（IM 模块） |
| 工具库 | Hutool | 5.8.43 |
| JSON | Gson | — |
| 验证 | Jakarta Validation | — |
| 模板引擎 | Velocity | 2.4.1（代码生成） |

### 中间件

| 组件 | 版本                                                   |
|------|------------------------------------------------------|
| Nacos | 3.x（docker-compose-basic 提供 v2.4.0-sb2 / v3.2.1-sb3） |
| MySQL | 8.0.28                                               |
| Redis | 6+                                                   |

### 服务端口

| 服务 | 端口 |
|------|------|
| vita-gateway | 8888 |
| vita-auth-center | 8033 |
| vita-api | 8023 |
| vita-message | 8043 |
| vita-im | 8053 |

---

## 配置说明

项目**无**根级 `application.yml` / `bootstrap.yml`，采用 **Profile 分环境**策略：

| Profile | 用途        | 说明 |
|---------|-----------|------|
| `dev` | 联调 / 容器默认 | 仅配置 Nacos 地址，运行时配置从 Nacos 拉取 |
| `demo` | 远程配置的demo | 内置数据源、Redis、网关路由等完整本地配置 |
| `test` | 测试 / 生产容器 | 从 Nacos（`vita-test` 命名空间）拉取配置 |

Nacos 中需维护的配置项：

- 各服务 `application.yml`（数据源、Redis、端口等）
- 网关路由规则
- `interface.white-list` — 认证白名单
- `interface.url-token-to-header-list` — URL Token 转 Header 路径（如 WebSocket）
- SMTP 邮件配置
- 日志级别

### 注意事项

1. 涉及权限模型变更时，建议下线所有在线用户后重新登录
2. vita-common 通过 SPI 自动注入，Servlet 服务引入依赖即可生效，无需手动配置
3. 密码采用 MD5 + `vita.` 前缀；Token 采用 UUID + `vita.` 前缀

---

## 本地开发

### 环境要求

| 依赖 | 版本                        |
|------|---------------------------|
| JDK | 17+                       |
| Maven | 3.6+                      |
| MySQL | 8.0+                      |
| Redis | 6+                        |
| Nacos | 3.2.1（使用 `dev` Profile 时） |

### 方式一：demo Profile 本地单机

1. 启动 MySQL、Redis（可参考 `docker-compose-basic.yml` 中的中间件部分）
2. 初始化业务数据库（demo 配置默认库名 `demo`）
3. 编译并启动各服务：

```bash
# 根目录编译
mvn clean package -DskipTests

# 以 demo Profile 启动（示例：认证中心）
cd vita-auth-center
mvn spring-boot:run -Dspring-boot.run.profiles=demo
```

4. 按依赖顺序启动：`vita-auth-center` → `vita-api` → `vita-message` → `vita-im` → `vita-gateway`

### 方式二：dev Profile + Nacos

1. 启动基础设施：`docker compose -f docker-compose-basic.yml up -d`
2. 在 Nacos（命名空间 `vita-dev`，分组 `dev`）配置各服务
3. 以 `dev` Profile 启动服务（Docker 默认即 `ACTIVE_PROFILE=dev`）

### 健康检查

```bash
curl http://localhost:8888/health/check          # 网关
curl http://localhost:8033/health/check          # 认证中心
curl http://localhost:8023/health/check          # API
curl http://localhost:8043/health/check          # 消息
```

---

## 部署

### 构建镜像

各模块均包含 Dockerfile，需先打包 JAR：

```bash
mvn clean package -DskipTests
docker build -t vita-auth-center:2.0.0 ./vita-auth-center
# 其余模块同理
```

### Docker Compose 部署应用

```bash
# 启动
docker compose -f docker-compose.yml up -d

# 停止
docker compose -f docker-compose.yml stop

# 停止并移除
docker compose -f docker-compose.yml down
```

> **注意**：`docker-compose.yml` 中 `vita-im` 与 `vita-message` 当前映射端口均为 `8043`，与代码中 IM 实际端口 `8053` 不一致，部署前请修正 IM 端口映射为 `8053:8053`。

容器通过环境变量 `ACTIVE_PROFILE` 指定 Profile（Compose 中默认为 `test`）。

---

## 开发状态

| 模块 | 状态 |
|------|------|
| vita-common | ✅ 完整 |
| vita-gateway | ✅ 完整 |
| vita-auth-center | ✅ 完整（用户/角色 CRUD + 认证） |
| vita-api | ✅ 邮件发送 |
| vita-message | ✅ 邮件编排 + 记录持久化 |
| vita-im | ⚠️ 基础 WebSocket（广播 / ACK），私聊与持久化待完善 |

**待实现**：

- 数据级权限 `data_auth` 在业务查询层强制执行
- 接口级权限 `sys_interface_permission` 与 Controller 层联动
- IM 点对点消息、文件传输、消息落库
- 前端项目不在本仓库

---

## 日志

- 各服务独立 `logback-spring.xml`
- info / error 日志分文件，单文件 50MB 滚动
- Profile 行为：`dev` 仅控制台；`test` / `prod` 控制台 + 文件
- 日志格式包含 traceId，便于链路追踪
