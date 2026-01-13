# `HazeEats`

## 项目简介

HazeEats 是一个功能完善的外卖平台系统，支持用户在线点餐、商家管理、订单处理、支付结算等全流程功能。项目采用微服务架构，前后端分离设计，支持 Web 管理端和微信小程序端，适用于餐饮外卖、校园食堂等多种应用场景。

### 核心功能

- 🍽️ **菜品管理**：菜品分类、菜品信息管理、套餐管理、口味配置
- 🛒 **购物车**：添加菜品、修改数量、清空购物车
- 📦 **订单管理**：下单、支付、订单状态跟踪、历史订单查询
- 💳 **支付系统**：支持微信支付、支付宝支付等多种支付方式
- 👥 **用户系统**：用户注册、登录、地址管理、个人信息维护
- 🏪 **商家管理**：店铺信息管理、营业状态设置、菜品上下架
- 📊 **数据报表**：营业额统计、订单统计、菜品销量分析
- 📱 **小程序端**：微信小程序用户端，支持浏览、下单、支付等功能
- 🔐 **权限管理**：基于角色的权限控制，支持多角色管理

## 系统架构图

```
┌─────────────────┐         ┌─────────────────┐
│   Web 管理端     │         │  微信小程序端    │
│  (Vue 2 + TS)   │         │   (uni-app)     │
└────────┬────────┘         └────────┬────────┘
         │                           │
         │         HTTP/HTTPS        │
         └───────────┬───────────────┘
                     │
         ┌───────────▼───────────┐
         │    API Gateway        │
         │  (Spring Cloud)       │
         └───────────┬───────────┘
                     │
      ┌──────────────┼──────────────┐
      │              │              │
┌─────▼─────┐  ┌────▼────┐  ┌─────▼─────┐
│User Service│  │Dish Svc │  │Order Svc  │
└─────┬─────┘  └────┬────┘  └─────┬─────┘
      │              │              │
┌─────▼─────┐  ┌────▼────┐  ┌─────▼─────┐
│Cart Service│  │Shop Svc │  │Pay Service│
└─────┬─────┘  └────┬────┘  └─────┬─────┘
      │              │              │
      └──────────────┼──────────────┘
                     │
      ┌──────────────┼──────────────┐
      │              │              │
┌─────▼─────┐  ┌────▼────┐  ┌─────▼─────┐
│   MySQL   │  │  Redis  │  │Aliyun OSS │
└───────────┘  └─────────┘  └───────────┘
```

## 项目结构说明

```
HazeEats/
├── backend/                    # 后端微服务项目
│   ├── eats-common/           # 公共模块（工具类、常量、配置等）
│   ├── eats-pojo/             # 实体类模块（Entity、DTO、VO、Query等）
│   ├── eats-api/              # API 接口定义模块
│   ├── eats-gateway/          # 网关服务（路由、鉴权、限流等）
│   ├── eats-server/           # 主服务模块
│   ├── user-service/          # 用户服务（用户管理、认证授权）
│   ├── dish-service/          # 菜品服务（菜品管理、分类管理）
│   ├── cart-service/          # 购物车服务
│   ├── order-service/         # 订单服务（订单管理、订单状态）
│   ├── shop-service/          # 店铺服务（店铺信息、营业状态）
│   ├── pay-service/           # 支付服务（支付接口、支付回调）
│   ├── report-service/        # 报表服务（数据统计、报表生成）
│   └── pom.xml                # Maven 父 POM
├── frontend/                   # Web 管理端前端项目
│   ├── src/                   # 源代码目录
│   │   ├── views/             # 页面组件
│   │   ├── components/        # 公共组件
│   │   ├── router/            # 路由配置
│   │   ├── store/             # Vuex 状态管理
│   │   ├── api/               # API 接口
│   │   └── utils/             # 工具函数
│   └── package.json           # 前端依赖配置
├── mp-weixin/                  # 微信小程序端
│   ├── pages/                 # 小程序页面
│   ├── components/            # 小程序组件
│   ├── common/                # 公共资源
│   └── app.json               # 小程序配置
├── documents/                  # 项目文档和资源
│   ├── 需求文档.md            # 需求文档
│   ├── 技术架构.md            # 技术架构文档
│   ├── 数据库设计.md          # 数据库设计文档
│   └── 接口文档.md            # API 接口文档
├── nginx-1.20.2/              # Nginx 配置
└── README.md                   # 项目主 README
```

## 软件架构

### 后端技术栈

#### 核心技术栈

| 技术                     | 说明                   | 版本          | 备注                                                         |
| ------------------------ | ---------------------- | ------------- | ------------------------------------------------------------ |
| `Spring Boot`            | Spring快速集成脚手架   | 2.7.3         | https://spring.io/projects/spring-boot                       |
| `Spring Cloud`           | 微服务框架             | 2021.0.3      | https://spring.io/projects/spring-cloud                      |
| `Spring Cloud Alibaba`   | 微服务组件             | 2021.0.4.0    | https://github.com/alibaba/spring-cloud-alibaba              |
| `MyBatis`                | ORM 框架               | 2.2.0         | https://mybatis.org/mybatis-3/zh/index.html                  |
| `Lombok`                 | 实体类增强工具         | 1.18.20       | https://github.com/rzwitserloot/lombok                       |
| `Knife4j`                | 接口文档工具           | 4.1.0         | https://gitee.com/xiaoym/knife4j                             |
| `JJWT`                   | `JSON Web Token`       | 0.9.1         | https://github.com/jwtk/jjwt                                 |
| `MySQL Driver`           | MySQL 数据库驱动       | 8.0+          | https://dev.mysql.com/downloads/connector/j/                 |
| `Druid`                  | 数据库连接池           | 1.2.1         | https://github.com/alibaba/druid                             |
| `PageHelper`             | MyBatis 分页插件       | 1.3.0         | https://github.com/pagehelper/Mybatis-PageHelper             |
| `AspectJ`                | AOP 框架               | 1.9.4         | https://www.eclipse.org/aspectj/                             |
| `Fastjson`               | JSON 处理库            | 1.2.76        | https://github.com/alibaba/fastjson                          |
| `Aliyun OSS SDK`         | 阿里云 OSS SDK         | 3.10.2        | https://github.com/aliyun/aliyun-oss-java-sdk                |
| `WeChat Pay SDK`         | 微信支付 SDK           | 0.4.8         | https://pay.weixin.qq.com/                                   |

### 前端技术栈

#### Web 管理端技术栈

| 技术           | 说明             | 版本      | 备注                                 |
| -------------- | ---------------- | --------- | ------------------------------------ |
| `Vue`          | 前端框架         | 2.6.10    | https://v2.vuejs.org/                |
| `Vue-Router`   | 路由框架         | 3.1.2     | https://v3.router.vuejs.org/         |
| `Vuex`         | 状态管理         | 3.1.1     | https://v3.vuex.vuejs.org/           |
| `TypeScript`   | 类型系统         | 3.6.2     | https://www.typescriptlang.org/      |
| `Element UI`   | UI 组件库        | 2.12.0    | https://element.eleme.cn/            |
| `Axios`        | HTTP 客户端      | 0.19.0    | https://axios-http.com/              |
| `ECharts`      | 数据可视化       | 5.3.2     | https://echarts.apache.org/          |
| `Webpack`      | 模块打包工具     | 4.39.3    | https://webpack.js.org/              |

#### 微信小程序端技术栈

| 技术           | 说明             | 备注                                 |
| -------------- | ---------------- | ------------------------------------ |
| `uni-app`      | 跨平台开发框架   | https://uniapp.dcloud.io/            |
| `微信小程序`   | 小程序平台       | https://developers.weixin.qq.com/    |

## 环境要求

### 开发工具

| 工具            | 说明                  | 版本      | 备注                                                         |
| --------------- | --------------------- | --------- | ------------------------------------------------------------ |
| `Navicat`       | 数据库连接工具        | latest    | https://www.navicat.com.cn/                                  |
| `RDM`           | `Redis`可视化管理工具 | latest    | https://github.com/qishibo/AnotherRedisDesktopManager        |
| `Apifox`        | `API`接口调试工具     | latest    | https://apifox.com/                                          |
| `Git`           | 项目版本管控工具      | latest    | https://git-scm.com/                                         |
| `IDEA`          | `Java`开发`IDE`       | 2022.1+   | https://www.jetbrains.com/idea/download                      |
| `Apache Maven`  | Maven 构建工具        | 3.6.3+    | https://maven.apache.org/                                    |
| `VS Code`       | 前端开发`IDE`         | latest    | https://code.visualstudio.com/Download                       |
| `微信开发者工具` | 小程序开发工具        | latest    | https://developers.weixin.qq.com/miniprogram/dev/devtools/download.html |

### 开发环境

| 依赖环境  | 版本       | 备注                      |
| --------- | ---------- | ------------------------- |
| `Windows` | 10+        | 操作系统                  |
| `JDK`     | 11+        | https://www.oracle.com/java/technologies/downloads/ |
| `NodeJS`  | 14.0.0+    | https://nodejs.org/zh-cn/ |
| `NPM`     | 6.0.0+     | https://www.npmjs.com/    |

### 服务器环境

| 依赖环境    | 版本       | 备注                                                         |
| ----------- | ---------- | ------------------------------------------------------------ |
| `Linux`     | CentOS 7+  | 操作系统                                                     |
| `Docker`    | latest     | https://www.docker.com/                                      |
| `MySQL`     | 8.0+       | https://www.mysql.com/                                       |
| `Redis`     | 6.0+       | https://redis.io/                                            |
| `Nginx`     | 1.20.2+    | https://nginx.org/en/                                        |
| `Nacos`     | 2.0+       | https://nacos.io/                                            |

## 快速开始

### 后端启动

1. 克隆项目到本地
```bash
git clone https://github.com/yourusername/HazeEats.git
cd HazeEats/backend
```

2. 配置数据库
- 创建数据库 `haze_eats`
- 导入 SQL 脚本（位于 `backend/scripts/` 目录）

3. 修改配置文件
- 修改各服务模块的 `application.yml` 配置文件
- 配置数据库连接、Redis 连接、OSS 配置等

4. 启动服务
```bash
# 使用 Maven 构建
mvn clean install

# 启动各个微服务
# 1. 先启动 Nacos
# 2. 启动 Gateway
# 3. 启动其他业务服务
```

### 前端启动

#### Web 管理端

```bash
cd frontend

# 安装依赖
npm install

# 启动开发服务器
npm run serve

# 构建生产版本
npm run build
```

#### 微信小程序端

1. 使用微信开发者工具打开 `mp-weixin` 目录
2. 配置小程序 AppID
3. 修改 API 接口地址
4. 点击编译运行

## 部署说明

### Docker 部署

```bash
# 构建镜像
docker build -t haze-eats:latest .

# 运行容器
docker-compose up -d
```

### Nginx 配置

参考 `nginx-1.20.2/conf/nginx.conf` 配置文件进行反向代理和负载均衡配置。

## 功能模块说明

### 用户端功能
- 用户注册/登录
- 浏览菜品和套餐
- 添加购物车
- 下单支付
- 订单查询
- 地址管理
- 个人信息管理

### 商家端功能
- 店铺信息管理
- 菜品分类管理
- 菜品信息管理
- 套餐管理
- 订单管理
- 营业状态设置
- 数据统计报表

## 部分功能预览图

> （此处可添加项目截图）

## 项目亮点

- ✅ 采用微服务架构，服务解耦，易于扩展和维护
- ✅ 使用 Spring Cloud 全家桶，技术栈成熟稳定
- ✅ 前后端分离，支持多端访问（Web + 小程序）
- ✅ 完善的权限管理和安全机制
- ✅ 支持多种支付方式
- ✅ 数据统计和报表功能完善
- ✅ 代码规范，注释完整，易于学习

## 开源协议

本项目遵循 MIT 开源协议。

## 特别鸣谢

`HazeEats` 的诞生离不开开源软件和社区的支持，感谢以下开源项目及项目维护者：

- `spring`：https://github.com/spring-projects
- `alibaba`：https://github.com/alibaba
- `mybatis`：https://github.com/mybatis/mybatis-3
- `vue`：https://github.com/vuejs
- `element-ui`：https://github.com/ElemeFE/element
- `echarts`：https://github.com/apache/echarts
- `knife4j`：https://gitee.com/xiaoym/knife4j

同时也感谢其他没有明确写出来的开源组件提供者与维护者。

## 联系方式

如有问题或建议，欢迎提交 Issue 或 Pull Request。

---

⭐ 如果这个项目对你有帮助，欢迎 Star 支持！
