# 企业论坛系统 (Enterprise Forum System)

一个功能完整的企业级论坛系统，支持高并发（6000+ QPS），包含博客文章、论坛帖子、用户管理、私信通知等功能。

## 项目简介

本项目是一个面向企业用户的社区论坛系统，采用前后端分离架构，后端基于Spring Boot微服务，前端基于Vue 3 + Element Plus。

### 技术栈

#### 后端技术
- **Spring Boot 3.2** - 核心框架
- **Spring Cloud Gateway** - API网关
- **Spring Data JPA** - ORM框架
- **MySQL 8.0** - 主数据库
- **Redis 7** - 缓存层
- **RabbitMQ** - 消息队列
- **JWT** - 身份认证

#### 前端技术
- **Vue 3** - 前端框架
- **Element Plus** - UI组件库
- **Vite** - 构建工具
- **Axios** - HTTP客户端

## 系统架构

```
┌─────────────────────────────────────────────────────────────┐
│                    Nginx (负载均衡)                        │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│              API Gateway (Spring Cloud Gateway)             │
│  - 全局限流: 1000 QPS                                       │
│  - 文章服务: 2000 QPS                                       │
│  - 论坛服务: 1500 QPS                                       │
│  - 认证服务: 500 QPS                                        │
└─────────────────────────────────────────────────────────────┘
                              │
          ┌───────────────────┼───────────────────┐
          ▼                   ▼                   ▼
┌─────────────────┐  ┌─────────────────┐  ┌─────────────────┐
│  blog-service   │  │  blog-forum     │  │  blog-auth      │
│  (文章服务)      │  │  (论坛服务)      │  │  (认证服务)      │
│  端口: 8082     │  │  端口: 8083     │  │  端口: 8081     │
└─────────────────┘  └─────────────────┘  └─────────────────┘
          │                   │                   │
          └───────────────────┼───────────────────┘
                              ▼
┌─────────────────────────────────────────────────────────────┐
│  ┌──────────┐  ┌──────────┐  ┌──────────────────────────┐  │
│  │  Redis   │  │ MySQL    │  │  RabbitMQ                 │  │
│  │  缓存    │  │ 主数据库  │  │  异步消息处理             │  │
│  └──────────┘  └──────────┘  └──────────────────────────┘  │
└─────────────────────────────────────────────────────────────┘
```

## 功能模块

### 1. 用户系统
- 用户注册/登录（JWT认证）
- 角色权限管理（RBAC）
- 用户等级与积分系统
- 个人资料管理

### 2. 文章管理
- 文章发布/编辑/删除
- Markdown编辑器
- 评论与回复
- 点赞功能
- 文章审核流程
- 分类与标签

### 3. 论坛社区
- 多板块分类
- 帖子发布/回复
- 置顶/精华/锁定
- 热门帖子排行

### 4. 消息系统
- 系统通知
- @提及提醒
- 私信功能

### 5. 企业特性
- 部门管理（树形结构）
- 公告系统（优先级/置顶）
- 文件上传下载

### 6. 高并发优化
- Redis缓存（热点数据预热）
- 网关限流（6000 QPS）
- 分布式锁（Redis）
- 接口幂等性（Token机制）
- 异步消息处理（RabbitMQ）

## 快速开始

### 环境要求
- Docker & Docker Compose
- Node.js 18+
- Java 17+

### 方式一：Docker部署（推荐）

```bash
# 1. 进入项目目录
cd blog-website

# 2. 启动所有服务
docker-compose up -d

# 3. 查看服务状态
docker-compose ps

# 4. 查看日志
docker-compose logs -f
```

访问地址：
- 前端：http://localhost
- API网关：http://localhost:8080
- RabbitMQ管理：http://localhost:15672（guest/guest）

### 方式二：本地开发

#### 1. 启动基础设施

```bash
docker-compose up -d mysql redis rabbitmq
```

#### 2. 启动后端服务

```bash
cd backend
mvn clean package -DskipTests
java -jar blog-gateway/target/blog-gateway.jar &
java -jar blog-auth/target/blog-auth.jar &
java -jar blog-service/target/blog-service.jar &
java -jar blog-forum/target/blog-forum.jar &
java -jar blog-user/target/blog-user.jar &
```

#### 3. 启动前端

```bash
cd frontend
npm install
npm run dev
```

访问 http://localhost:5173

## 默认账号

| 角色 | 用户名 | 密码 |
|------|--------|------|
| 超级管理员 | admin | 123456 |
| 普通用户 | test | 123456 |

## API文档

### 认证模块 `/api/auth`
| 方法 | 路径 | 说明 |
|------|------|------|
| POST | /auth/login | 用户登录 |
| POST | /auth/register | 用户注册 |
| GET | /auth/current | 获取当前用户 |
| GET | /auth/users | 获取用户列表（管理员） |

### 文章模块 `/api/blog`
| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /blog/posts | 获取文章列表 |
| GET | /blog/posts/{id} | 获取文章详情 |
| POST | /blog/posts | 创建文章 |
| PUT | /blog/posts/{id} | 更新文章 |
| DELETE | /blog/posts/{id} | 删除文章 |
| GET | /blog/hot | 热门文章 |
| GET | /blog/latest | 最新文章 |
| POST | /blog/comments | 添加评论 |
| POST | /blog/likes/post/{id} | 点赞文章 |

### 论坛模块 `/api/forum`
| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /forum/categories | 获取分类列表 |
| GET | /forum/threads | 获取帖子列表 |
| POST | /forum/threads | 创建帖子 |
| GET | /forum/threads/{id} | 获取帖子详情 |
| POST | /forum/replies | 添加回复 |

### 搜索模块 `/api/search`
| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /search/posts | 搜索文章 |
| GET | /search/threads | 搜索帖子 |

### 管理模块 `/api/admin`
| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /admin/stats | 系统统计 |
| GET | /announcements | 公告列表 |
| POST | /announcements | 创建公告 |
| GET | /departments | 部门列表 |

## 性能测试

运行高并发测试脚本：

```bash
cd test
# 编译并运行测试
./run_test.bat   # Windows
# 或
javac LoadTest.java
java LoadTest
```

测试配置：
- 目标QPS：6000
- 测试时长：30秒
- 并发线程：100

## 项目结构

```
blog-website/
├── backend/
│   ├── blog-auth/        # 认证服务
│   ├── blog-common/      # 公共模块
│   ├── blog-forum/       # 论坛服务
│   ├── blog-gateway/      # API网关
│   ├── blog-service/      # 文章服务
│   └── blog-user/         # 用户服务
├── frontend/              # 前端应用
├── test/                  # 测试工具
├── docker-compose.yml     # Docker编排
├── init.sql              # 数据库初始化
└── README.md             # 项目文档
```

## 高并发特性

### 1. 限流配置
| 服务 | 正常QPS | 峰值QPS |
|------|---------|---------|
| 全局默认 | 1000 | 2000 |
| 认证服务 | 500 | 1000 |
| 文章服务 | 2000 | 4000 |
| 论坛服务 | 1500 | 3000 |
| 用户服务 | 500 | 1000 |
| 管理后台 | 200 | 500 |

### 2. 缓存策略
- 热门文章：5分钟TTL
- 最新文章：3分钟TTL
- 文章详情：30分钟TTL
- 论坛分类：1小时TTL

### 3. 消息队列
- 阅读量更新异步处理
- 评论数更新异步处理
- 缓存清除通知

## 配置说明

### 数据库连接池
```yaml
spring:
  datasource:
    hikari:
      minimum-idle: 10
      maximum-pool-size: 50
      idle-timeout: 30000
```

### Redis配置
```yaml
spring:
  data:
    redis:
      lettuce:
        pool:
          max-active: 8
          max-idle: 8
          min-idle: 2
```

### RabbitMQ配置
```yaml
spring:
  rabbitmq:
    listener:
      simple:
        concurrency: 5
        max-concurrency: 10
        prefetch: 10
```

## 注意事项

1. **首次部署**：数据库初始化脚本会自动执行，创建所有表和初始数据
2. **端口占用**：确保以下端口未被占用：80, 3306, 5672, 6379, 8080-8084, 15672
3. **内存要求**：Docker部署建议至少4GB可用内存
4. **数据持久化**：MySQL、Redis、RabbitMQ数据已配置持久化

## License

MIT License
