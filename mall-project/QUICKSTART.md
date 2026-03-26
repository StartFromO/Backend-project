# 快速开始指南

## 环境要求

- **JDK 17+** - 后端运行环境
- **Maven 3.8+** - 后端构建工具
- **Node.js 20+** - 前端运行环境
- **npm 10+** 或 **yarn** - 前端包管理器
- **MySQL 8.0** (可选) - 生产环境数据库

## 方式一：本地开发环境启动

### 1. 启动后端

```bash
# 进入后端目录
cd backend

# 编译并运行
mvn clean install
mvn spring-boot:run
```

后端服务将启动在 `http://localhost:8080`

H2 数据库控制台: `http://localhost:8080/h2-console`
- JDBC URL: `jdbc:h2:mem:malldb`
- Username: `sa`
- Password: (空)

### 2. 启动前端

```bash
# 进入前端目录
cd frontend

# 安装依赖
npm install

# 启动开发服务器
npm run dev
```

前端服务将启动在 `http://localhost:3000`

### 3. 访问应用

打开浏览器访问: `http://localhost:3000`

## 方式二：使用启动脚本

```bash
# 后端启动脚本
./start-backend.sh

# 前端启动脚本（新开终端）
./start-frontend.sh
```

## 方式三：Docker 部署

### 1. 构建并启动所有服务

```bash
# 构建并启动
docker-compose up --build

# 后台运行
docker-compose up -d --build
```

### 2. 访问应用

- 前端: `http://localhost`
- 后端 API: `http://localhost:8080`

### 3. 停止服务

```bash
docker-compose down

# 同时删除数据卷
docker-compose down -v
```

## 方式四：IDEA 中启动

### 后端启动

1. 使用 IDEA 打开 `backend` 目录
2. 等待 Maven 自动导入依赖
3. 找到 `MallApplication.java`
4. 右键点击 `Run 'MallApplication'`

### 前端启动

1. 使用 VS Code 或 WebStorm 打开 `frontend` 目录
2. 打开终端，执行:
```bash
npm install
npm run dev
```

## 默认测试数据

系统启动时会自动初始化 12 条商品数据：

| 商品名称 | 分类 | 价格 | 是否热门 |
|---------|------|------|---------|
| iPhone 15 Pro Max | 手机数码 | ¥9999 | ✅ |
| MacBook Pro 14英寸 | 电脑办公 | ¥14999 | ✅ |
| AirPods Pro 2 | 手机数码 | ¥1899 | ✅ |
| iPad Air 5 | 平板电脑 | ¥4799 | ❌ |
| Apple Watch Series 9 | 智能穿戴 | ¥2999 | ✅ |
| Sony WH-1000XM5 | 手机数码 | ¥2499 | ❌ |
| Nintendo Switch OLED | 游戏娱乐 | ¥2199 | ✅ |
| Dyson V15 Detect | 家用电器 | ¥4999 | ❌ |
| 小米14 Pro | 手机数码 | ¥4999 | ✅ |
| 华为Mate 60 Pro | 手机数码 | ¥6999 | ✅ |
| 戴森吹风机 HD15 | 家用电器 | ¥3199 | ❌ |
| Kindle Paperwhite 5 | 电子阅读 | ¥1099 | ❌ |

## 主要功能页面

| 页面 | 路径 | 功能描述 |
|------|------|---------|
| 首页 | `/` | 数据统计、热门商品、快捷入口 |
| 商品管理 | `/products` | 商品列表、增删改查 |
| 添加商品 | `/products/add` | 添加新商品 |
| 编辑商品 | `/products/edit/:id` | 编辑商品信息 |
| 热门推荐 | `/hot` | 热门商品排行、统计 |
| 商品搜索 | `/search` | 关键字搜索、高级筛选 |

## API 接口测试

可以使用 Postman 或浏览器直接测试 API：

```bash
# 获取商品列表
curl http://localhost:8080/api/products

# 搜索商品
curl "http://localhost:8080/api/products/search?keyword=iPhone"

# 获取热门商品
curl http://localhost:8080/api/products/hot
```

## 常见问题

### 1. 端口冲突

如果 8080 或 3000 端口被占用：

**后端端口修改**: `backend/src/main/resources/application.yml`
```yaml
server:
  port: 8081  # 修改为其他端口
```

**前端端口修改**: `frontend/vite.config.ts`
```typescript
server: {
  port: 3001  // 修改为其他端口
}
```

### 2. 前端代理配置

如果后端地址不是 `localhost:8080`，修改 `frontend/vite.config.ts`：

```typescript
proxy: {
  '/api': {
    target: 'http://your-backend-url:port',
    changeOrigin: true
  }
}
```

### 3. 数据库切换为 MySQL

修改 `backend/src/main/resources/application.yml`：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/mall_db?useUnicode=true&characterEncoding=utf-8
    username: root
    password: your_password
    driver-class-name: com.mysql.cj.jdbc.Driver
  jpa:
    hibernate:
      ddl-auto: update
    database-platform: org.hibernate.dialect.MySQLDialect
```

## 生产环境部署

### 1. 构建生产包

```bash
# 后端
mvn clean package -DskipTests

# 前端
npm run build
```

### 2. 部署

将构建产物部署到服务器：
- 后端: `backend/target/mall-backend-1.0.0.jar`
- 前端: `frontend/dist` 目录

### 3. 使用 Docker 生产部署

```bash
# 使用生产环境配置
docker-compose -f docker-compose.yml up -d
```

## 技术支持

如有问题，请查看：
- [API 文档](./API.md)
- [项目 README](./README.md)
