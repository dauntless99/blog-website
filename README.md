# 博客论坛 - Blog Forum

基于 **Vue 3 + Spring Cloud** 的前后端分离博客论坛系统。

## 项目结构

```
blog-website/
├── frontend/                    # Vue 3 前端
│   ├── src/
│   │   ├── api/                 # API 请求模块
│   │   ├── components/          # 公共组件
│   │   ├── layouts/             # 布局组件
│   │   ├── router/              # 路由配置
│   │   ├── stores/              # Pinia 状态管理
│   │   ├── styles/              # 全局样式
│   │   └── views/               # 页面视图
│   │       ├── auth/            # 登录/注册
│   │       ├── blog/            # 博客文章
│   │       ├── forum/           # 论坛帖子
│   │       └── user/            # 个人中心
│   ├── package.json
│   └── vite.config.js
│
├── backend/                     # Spring Cloud 后端
│   ├── blog-common/             # 公共模块(实体/DTO/工具)
│   ├── blog-gateway/            # API 网关 (端口 8080)
│   ├── blog-auth/               # 认证服务 (端口 8081)
│   ├── blog-service/            # 博客服务 (端口 8082)
│   ├── blog-forum/              # 论坛服务 (端口 8083)
│   ├── blog-user/               # 用户服务 (端口 8084)
│   └── pom.xml                  # 父 POM
│
├── docker-compose.yml           # MySQL 容器
└── init.sql                     # 数据库初始化
```

## 技术栈

### 前端
- **Vue 3** (Composition API + `<script setup>`)
- **Vite** 5 (构建工具)
- **Vue Router 4** (路由)
- **Pinia** (状态管理)
- **Element Plus** (UI 组件库)
- **Axios** (HTTP 请求)
- **Marked** (Markdown 渲染)

### 后端
- **Spring Boot 3.2**
- **Spring Cloud Gateway** (API 网关)
- **Spring Data JPA** (ORM)
- **MySQL 8.0** (数据库)
- **JWT** (认证鉴权)
- **Lombok** (代码简化)

## 功能特性

- 用户注册/登录 (JWT 认证)
- 博客文章 CRUD (Markdown 编写)
- 文章分类与标签
- 文章搜索
- 热门文章/最新文章
- 论坛板块分类
- 发帖/回复 (支持盖楼)
- 个人资料编辑
- 响应式设计

## 快速开始

### 1. 启动 MySQL

```bash
docker-compose up -d
```

或手动创建以下4个数据库：
- `blog_auth`
- `blog_service`
- `blog_forum`
- `blog_user`

### 2. 启动后端服务

按顺序启动各服务（每个服务是独立的 Spring Boot 应用）：

```bash
cd backend

# 1. 先编译公共模块
cd blog-common && mvn clean install -DskipTests && cd ..

# 2. 启动网关
cd blog-gateway && mvn spring-boot:run &

# 3. 启动认证服务
cd ../blog-auth && mvn spring-boot:run &

# 4. 启动博客服务
cd ../blog-service && mvn spring-boot:run &

# 5. 启动论坛服务
cd ../blog-forum && mvn spring-boot:run &

# 6. 启动用户服务
cd ../blog-user && mvn spring-boot:run &
```

### 3. 启动前端

```bash
cd frontend
npm install
npm run dev
```

访问 http://localhost:5173

## API 接口

| 服务 | 端口 | 基础路径 | 说明 |
|------|------|----------|------|
| Gateway | 8080 | /api/** | API 网关入口 |
| Auth | 8081 | /api/auth | 登录/注册 |
| Blog | 8082 | /api/blog | 文章 CRUD |
| Forum | 8083 | /api/forum | 论坛帖子/回复 |
| User | 8084 | /api/user | 个人资料 |

## 数据库配置

默认 MySQL 连接：`localhost:3306`，用户名 `root`，密码 `123456`

可在各服务的 `application.yml` 中修改。
