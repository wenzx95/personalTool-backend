# PersonalTool Backend

个人工具集后端服务 - 基于Spring Boot 3.x，提供博客、记账、A股复盘等功能的API接口。

## 技术栈

- Spring Boot 3.2.0
- MyBatis Plus 3.5.5
- MySQL 8.0
- Spring Security + JWT
- Caffeine缓存
- Knife4j API文档

## 功能模块

- 博客系统
- 个人记账工具（数据加密）
- A股复盘系统（调用Python服务）
- 用户认证授权
- 统一API网关

## 快速开始

### 环境要求

- JDK 17+
- Maven 3.6+
- MySQL 8.0+

### 配置

1. 复制 `application.yml` 并配置数据库连接
2. 设置环境变量：
   - `DB_USERNAME`: 数据库用户名
   - `DB_PASSWORD`: 数据库密码
   - `JWT_SECRET`: JWT密钥
   - `APP_ENCRYPTION_KEY`: 数据加密密钥（32位）

### 运行

```bash
# 开发环境
mvn spring-boot:run

# 生产环境
mvn clean package
java -jar target/backend-1.0.0.jar
```

### Docker构建

```bash
docker build -t personalTool-backend:latest .
docker run -p 8080:8080 personalTool-backend:latest
```

## API文档

启动服务后访问：http://localhost:8080/doc.html

## 项目结构

```
src/main/java/com/personal/
├── BackendApplication.java      # 启动类
├── common/                      # 通用类
│   └── Result.java             # 统一响应格式
├── config/                      # 配置类
│   └── SecurityConfig.java      # 安全配置
├── controller/                  # 控制器
│   └── AuthController.java      # 认证接口
├── service/                     # 业务逻辑
├── mapper/                      # MyBatis Mapper
├── entity/                      # 实体类
└── util/                        # 工具类
```

