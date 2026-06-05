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
  - [Profile 与环境变量](#profile-与环境变量)
  - [Nacos 配置中心](#nacos-配置中心)
  - [服务注册元数据](#服务注册元数据)
- [本地开发](#本地开发)
- [部署](#部署)
  - [环境文件](#环境文件)
  - [构建与推送镜像](#构建与推送镜像)
  - [Docker Compose 部署](#docker-compose-部署)
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
├── docker-compose.yml           # 应用服务编排
├── docker-compose-basic.yml     # 基础设施（MySQL / Redis / Nacos）
├── docker-deploy.sh             # 批量构建并推送镜像
├── .env.example.properties      # Compose 环境变量模板（复制为 .env 使用）
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

项目**无**根级 `application.yml` / `bootstrap.yml`，采用 **Profile + 环境变量 + Nacos** 三层分环境策略：

| 层级 | 位置 | 职责 |
|------|------|------|
| Profile 骨架 | 各模块 `application-{profile}.yml`（打进 JAR） | 定义如何连接 Nacos、是否使用本地内置配置 |
| 发布参数 | `.env` / 容器环境变量 | 切换 Profile、注入 Nacos 地址与命名空间 |
| 业务配置 | Nacos 各环境独立 namespace | 数据源、Redis、路由、白名单等运行时配置 |

### Profile 与环境变量

各服务均提供 `application-dev.yml`、`application-demo.yml`、`application-test.yml`、`application-prod.yml`。

| Profile | 用途 | 本地 yml 内容 | 典型场景 |
|---------|------|--------------|----------|
| `dev` | 本地联调 | 写死 Nacos 地址（`vita-dev` / `dev`） | IDEA / `mvn spring-boot:run` 连本地 Nacos |
| `demo` | 单机演示 | 完整内置配置（数据源、Redis、路由等） | 不启 Nacos，快速本地跑通 |
| `test` | 测试环境 | `${NACOS_*}` 占位符，由环境变量注入 | 测试服务器 / 质检发布 |
| `prod` | 生产环境 | 同 `test`，通过不同 `.env` 区分 namespace | 生产发布 |

**容器启动**：Dockerfile 通过 `ACTIVE_PROFILE` 指定 Profile，未设置时默认 `dev`：

```bash
-Dspring.profiles.active=${ACTIVE_PROFILE:-dev}
```

**测试 / 生产必填环境变量**（`test` / `prod` Profile）：

| 变量 | 说明 | 示例 |
|------|------|------|
| `NACOS_SERVER_ADDR` | Nacos 地址 | `127.0.0.1:8838` |
| `NACOS_USERNAME` | 认证用户名 | `nacos` |
| `NACOS_PASSWORD` | 认证密码 | — |
| `NACOS_NAMESPACE` | 命名空间 ID | `vita-test` / `vita-prod` |
| `NACOS_GROUP` | 配置分组 | `test` / `prod` |

### Nacos 配置中心

按环境在 Nacos 控制台创建独立 **namespace**，推荐命名：

| 环境 | Namespace | Group | 说明 |
|------|-----------|-------|------|
| 开发 | `vita-dev` | `dev` | 本地联调，`dev` Profile 默认连接 |
| 测试 | `vita-test` | `test` | 质检 / 测试发布 |
| 生产 | `vita-prod` | `prod` | 生产发布 |

每个服务在对应 namespace 下维护 **Data ID = `{服务名}.yml`**（如 `vita-api.yml`），`application-test.yml` / `application-prod.yml` 通过 `spring.config.import` 拉取。

Nacos 中需维护的配置项：

- 各服务完整配置（数据源、Redis、端口、Discovery 等）
- 网关路由规则
- `interface.white-list` — 认证白名单
- `interface.url-token-to-header-list` — URL Token 转 Header 路径（如 WebSocket）
- SMTP 邮件配置
- 日志级别

### 服务注册元数据

各服务 `NacosConfiguration` 在注册时将当前环境写入实例元数据，可在 Nacos 控制台 **服务列表 → 实例详情 → 元数据** 查看：

| 元数据键 | 来源 | 说明 |
|----------|------|------|
| `env` | `spring.profiles.active` | 当前 Profile（`dev` / `test` / `prod` 等） |
| `version` | `app.version` | 应用版本号 |
| `startup.time` | 启动时刻 | ISO 8601 格式启动时间 |

确保元数据 `env` 与目标环境一致：发布时设置正确的 `ACTIVE_PROFILE`，并使用独立 Nacos namespace 隔离各环境实例。

### 注意事项

1. 涉及权限模型变更时，建议下线所有在线用户后重新登录
2. vita-common 通过 SPI 自动注入，Servlet 服务引入依赖即可生效，无需手动配置
3. 密码采用 MD5 + `vita.` 前缀；Token 采用 UUID + `vita.` 前缀
4. 同一套镜像可用于各环境，**无需为环境打不同 JAR**；切换环境只需更换 `.env` 中的 `ACTIVE_PROFILE` 与 `NACOS_*`

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

1. 复制环境模板并启动基础设施：

```bash
cp .env.example.properties .env
docker compose -f docker-compose-basic.yml --env-file .env up -d
```

2. 访问 Nacos 控制台（v3 默认 `http://localhost:7080`，端口见 `.env` 中 `NACOS_V3_CONSOLE_PORT`）
3. 在命名空间 `vita-dev`、分组 `dev` 下配置各服务 `{服务名}.yml`
4. 以 `dev` Profile 启动（本地 IDEA 或 Maven；容器未设 `ACTIVE_PROFILE` 时 Dockerfile 默认 `dev`）：

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

### 健康检查

```bash
curl http://localhost:8888/health/check          # 网关
curl http://localhost:8033/health/check          # 认证中心
curl http://localhost:8023/health/check          # API
curl http://localhost:8043/health/check          # 消息
curl http://localhost:8053/health/check          # IM
```

---

## 部署

### 环境文件

复制模板并按目标环境修改：

```bash
cp .env.example.properties .env
```

关键变量说明见 [Profile 与环境变量](#profile-与环境变量)。测试与生产建议使用**不同 `.env` 文件**发布，仅修改 `ACTIVE_PROFILE` 与 `NACOS_*`：

```properties
# .env.test
ACTIVE_PROFILE=test
NACOS_SERVER_ADDR=nacos.test.internal:8838
NACOS_NAMESPACE=vita-test
NACOS_GROUP=test

# .env.prod
ACTIVE_PROFILE=prod
NACOS_SERVER_ADDR=nacos.prod.internal:8838
NACOS_NAMESPACE=vita-prod
NACOS_GROUP=prod
```

### 构建与推送镜像

各模块均包含 Dockerfile，需先打包 JAR 再构建镜像：

```bash
# 根目录编译全部模块
mvn clean package -DskipTests

# 单模块构建（示例）
docker build -t vita-gateway:2.0.0 ./vita-gateway
docker build -t vita-auth-center:2.0.0 ./vita-auth-center
docker build -t vita-api:2.0.0 ./vita-api
docker build -t vita-message:2.0.0 ./vita-message
docker build -t vita-im:2.0.0 ./vita-im
```

批量构建并推送到私有仓库（需先配置 `DOCKER_REGISTRY` / `DOCKER_NAMESPACE`）：

```bash
export DOCKER_REGISTRY=registry.example.com
export DOCKER_NAMESPACE=vita
./docker-deploy.sh
```

`docker-deploy.sh` 会依次处理 `vita-gateway`、`vita-auth-center`、`vita-api`、`vita-message`、`vita-im`，镜像 tag 为时间戳 `YYYYMMDD_HHMM`。

### Docker Compose 部署

**1. 启动基础设施**（MySQL / Redis / Nacos）：

```bash
docker compose -f docker-compose-basic.yml --env-file .env up -d
```

**2. 在 Nacos 对应 namespace 配置各服务**（测试环境示例：`vita-test` / `test`）

**3. 启动应用服务**：

```bash
# 测试环境
docker compose -f docker-compose.yml --env-file .env up -d

# 或指定环境文件
docker compose -f docker-compose.yml --env-file .env.prod up -d
```

`docker-compose.yml` 向所有服务统一注入 `ACTIVE_PROFILE` 与 `NACOS_*` 环境变量，默认 `ACTIVE_PROFILE=test`。

```bash
# 停止
docker compose -f docker-compose.yml --env-file .env stop

# 停止并移除
docker compose -f docker-compose.yml --env-file .env down

# 查看某服务日志
docker compose -f docker-compose.yml logs -f vita-gateway
```

**本地 demo 模式**（不依赖 Nacos）：在 `.env` 中设置 `ACTIVE_PROFILE=demo`，各服务使用 `application-demo.yml` 内置配置即可启动。

**发布检查**：在 Nacos 服务列表确认实例元数据 `env` 字段与目标环境一致（`test` / `prod`）。

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
