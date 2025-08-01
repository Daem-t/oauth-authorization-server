# OAuth2 认证授权服务器项目计划

## 1. 项目概述

本项目旨在构建一个功能完备、生产级别的 OAuth2 认证授权服务器。后端采用 Java 17、Spring Boot 和 Spring Authorization Server，并遵循领域驱动设计（DDD）原则。前端采用 React 和 Material-UI 技术栈，提供一个现代化的管理界面。项目将前后端集成构建，并支持国际化。

## 2. 核心功能

一个完整的 OAuth2 认证授权服务器应具备以下核心功能：

- **OAuth2 标准端点:**
    - **授权端点 (`/oauth2/authorize`):** 处理用户的授权请求，并引导用户进行认证和授权。
    - **令牌端点 (`/oauth2/token`):** 验证客户端凭据和授权码、刷新令牌，并颁发访问令牌。
    - **令牌撤销端点 (`/oauth2/revoke`):** 允许客户端或用户撤销已颁发的令牌。
    - **令牌自省端点 (`/oauth2/introspect`):** 允许资源服务器验证令牌的有效性。
    - **JWK Set 端点 (`/oauth2/jwks`):** 发布用于签名 JWT 访问令牌的公钥。
- **OpenID Connect (OIDC) 扩展:**
    - **用户信息端点 (`/userinfo`):** 在获取相应 scope 的情况下，提供有关经过身份验证的用户的信息。
- **管理功能:**
    - **客户端管理:** 动态注册、查看、更新和删除 OAuth2 客户端 (`RegisteredClient`)。
    - **授权管理:** 查看和撤销用户对特定客户端的授权 (`OAuth2AuthorizationConsent`)。
    - **用户管理:** (可选范围) 管理系统内的用户账户。
- **用户体验:**
    - **自定义登录页面:** 提供用户友好的登录界面。
    - **自定义授权同意页面:** 清晰地向用户展示所请求的权限，并让用户选择同意或拒绝。
- **安全性:**
    - **PKCE 支持:** (Proof Key for Code Exchange) 增强公共客户端的安全性。
    - **CORS 配置:** 为所有端点提供安全的跨域资源共享策略。
    - **HTTPS 强制:** 所有通信必须通过 TLS 进行。

## 3. 技术栈

- **后端:**
    - **语言/框架:** Java 17, Spring Boot 3.5.3, Spring Authorization Server 1.5.1, Spring Security 6
    - **数据持久化:** Spring Data JPA, Hibernate, PostgreSQL
    - **构建工具:** Apache Maven
- **前端:**
    - **核心库:** React, Redux
    - **UI 框架:** Material-UI (MUI)
    - **HTTP 客户端:** Axios
    - **构建/开发:** Node.js, npm/yarn
- **集成:**
    - **构建插件:** `frontend-maven-plugin`
- **国际化 (i18n):**
    - **后端:** Spring `ResourceBundleMessageSource`
    - **前端:** `react-i18next`

## 4. 架构设计

### 4.1. 后端 (DDD)

我们将遵循 DDD 的分层架构，将业务逻辑与技术实现分离。

- **Domain Layer (领域层):**
    - **Aggregates & Entities:** `RegisteredClient`, `Authorization`, `AuthorizationConsent`, `User` (自定义用户实体) 等。这些是业务核心，不依赖任何外部框架。
    - **Repositories:** 定义仓储接口，如 `RegisteredClientRepository`, `UserRepository`。
    - **Domain Services:** 封装核心领域逻辑。
    - **包结构:** `com.daem.domain`

- **Application Layer (应用层):**
    - **Application Services:** 编排领域对象以完成用例（例如 `ClientManagementService`）。处理 DTO 转换和事务。
    - **DTOs:** 用于在各层之间传输数据。
    - **包结构:** `com.daem.application`

- **Infrastructure Layer (基础设施层):**
    - **JPA Repositories:** Spring Data JPA 实现领域层的仓储接口。
    - **Spring Security Config:** 实现 `UserDetailsService`，配置 `SecurityFilterChain` 和 `AuthorizationServerSettings`。
    - **包结构:** `com.daem.infrastructure`

- **Interface Layer (接口层):**
    - **REST Controllers:** 为前端管理功能提供 API 端点。
    - **Spring MVC/Security Endpoints:** 暴露标准的 OAuth2 和 OIDC 端点。
    - **包结构:** `com.daem.interfaces`

### 4.2. 前端

- **目录结构:** 所有前端代码位于 `src/main/webapp/`。`package.json` 和 `node_modules` 位于项目根目录。
- **状态管理:** Redux 用于全局状态管理（如用户信息、认证状态）。
- **组件化:** 使用 Material-UI 构建可重用的 UI 组件（如表格、表单、对话框）。
- **路由:** 使用 `react-router-dom` 管理页面导航。
- **API 通信:** 创建一个 Axios 实例来处理对后端 API 的所有请求，包括错误处理和 token 管理。

## 5. 详细开发计划

### 阶段一：后端基础与核心 OAuth2 功能 (5-7天)

1.  **项目初始化:**
    - 创建 Spring Boot 项目，添加 `web`, `security`, `data-jpa`, `postgresql`, `oauth2-authorization-server` 依赖。
    - 配置 `pom.xml` 以使用 Java 17。
2.  **数据库配置:**
    - 在 `application.yml` 中配置 PostgreSQL 数据源。
    - 运行 `docs/sql` 目录下的 SQL 脚本，创建 Spring Authorization Server 所需的表。
3.  **核心配置:**
    - 创建 `SecurityConfig` 和 `AuthorizationServerConfig` 类。
    - 配置 `SecurityFilterChain` 用于登录页面和 API 安全。
    - 配置 `AuthorizationServerConfigurer` 以启用所有 OAuth2 端点。
    - 提供 `UserDetailsService` 的实现，用于用户认证（初期可使用内存用户）。
4.  **数据初始化:**
    - 创建一个 `CommandLineRunner` Bean。
    - 在该 Bean 中，使用 `RegisteredClientRepository` 检查并注册应用自身作为一个客户端（用于后续测试和管理 API）。
5.  **验证:**
    - 启动服务器。
    - 使用 `curl` 或 Postman 手动执行 `authorization_code` 流程，确保可以成功获取令牌。

### 阶段二：后端管理 API 开发 (4-6天)

1.  **API 设计:**
    - 设计用于 CRUD 操作 `RegisteredClient` 的 RESTful API。
    - 设计用于查看和撤销 `AuthorizationConsent` 的 API。
2.  **DDD 实现:**
    - 在 `com.daem.domain` 中定义 `Client` 聚合。
    - 在 `com.daem.application` 中创建 `ClientManagementService`，处理业务逻辑和 DTO 转换。
    - 在 `com.daem.interfaces` 中创建 `ClientController`，暴露 REST 端点。
3.  **API 安全:**
    - 保护管理 API，要求用户具有特定权限/范围（例如 `scope=server.admin`）。
4.  **测试:**
    - 编写 JUnit 5 和 Mockito 测试来验证应用层和接口层的逻辑。

### 阶段三：前端项目设置与构建集成 (3-4天)

1.  **前端环境:**
    - 在项目根目录创建 `package.json`。
    - `npm install` 安装 React, Redux, Axios, Material-UI, react-router-dom, react-i18next 等依赖。
2.  **Maven 集成:**
    - 在 `pom.xml` 中配置 `frontend-maven-plugin`。
    - 配置 `npm install` (execution: `install-node-and-npm`) 和 `npm run build` (execution: `npm-build`) 目标。
    - 将 React 构建输出目录 (`build` 或 `dist`) 配置为 Spring Boot 的静态资源目录。
3.  **基础 UI:**
    - 在 `src/main/webapp/src` 中创建基础应用骨架。
    - 设置路由、Redux store 和一个使用 MUI 的基本布局（例如，带导航栏的 App Layout）。
4.  **验证:**
    - 运行 `mvn clean install`，确保前端项目被正确构建并打包到最终的 JAR 文件中。
    - 启动应用，访问根 URL，应能看到 React 应用。

### 阶段四：前端管理页面实现 (7-10天)

1.  **客户端管理页面:**
    - 创建一个页面，使用表格 (MUI `DataGrid`) 展示所有已注册的客户端。
    - 实现创建、编辑和删除客户端的表单和对话框。
    - 将这些 UI 组件连接到 Redux 和后端 API。
2.  **授权管理页面:**
    - 创建一个页面，允许管理员查看用户的授权记录并执行撤销操作。
3.  **登录与认证:**
    - 实现前端登录逻辑，在用户登录后，使用 Axios 拦截器自动附加访问令牌到后续请求。
    - 处理令牌刷新和安全退出。

### 阶段五：国际化 (i18n) (2-3天)

1.  **后端 i18n:**
    - 创建 `messages_en.properties` 和 `messages_zh_CN.properties`。
    - 在需要的地方（如错误信息）注入 `MessageSource`。
2.  **前端 i18n:**
    - 配置 `react-i18next`。
    - 创建 `public/locales/en/translation.json` 和 `public/locales/zh/translation.json`。
    - 在所有 UI 组件中用 `t()` 函数替换硬编码的文本。

### 阶段六：最终测试与文档 (3-4天)

1.  **端到端测试:**
    - 全面测试整个流程：用户登录 -> 管理客户端 -> 使用新客户端进行 OAuth2 授权 -> 撤销授权。
2.  **文档:**
    - 编写 `README.md`，提供详细的构建、配置和运行说明。
    - (可选) 使用 Swagger/OpenAPI 为管理 API 生成文档。
3.  **代码审查和重构:**
    - 清理代码，确保所有部分都符合 DDD 和项目规范。
