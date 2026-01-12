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

#### 方式一：使用环境变量（推荐）

1. 复制 `.env.example` 为 `.env`：
   ```bash
   cp .env.example .env
   ```

2. 编辑 `.env` 文件，填入实际配置：
   ```env
   # 数据库配置
   DB_URL=jdbc:mysql://localhost:3306/db_personal?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true
   DB_USERNAME=root
   DB_PASSWORD=your-database-password

   # JWT密钥（至少32个字符）
   JWT_SECRET=your-jwt-secret-key-at-least-32-characters-long

   # 加密密钥（必须是32个字符）
   APP_ENCRYPTION_KEY=your-encryption-key-must-be-32-chars

   # Python服务地址
   PYTHON_STOCK_SERVICE_URL=http://localhost:8000
   ```

3. 在 IDE 或启动脚本中加载环境变量：
   - **IDEA**: Run Configuration -> Environment Variables
   - **命令行**: 使用 `dotenv-maven-plugin` 或手动导出变量

#### 方式二：使用本地配置文件

1. 复制 `application-local.yml.example` 为 `application-local.yml`：
   ```bash
   cp src/main/resources/application-local.yml.example src/main/resources/application-local.yml
   ```

2. 编辑 `application-local.yml`，填入实际配置值

3. 激活本地配置：
   ```bash
   mvn spring-boot:run -Dspring-boot.run.profiles=local
   ```

#### 必需的环境变量

| 变量名 | 说明 | 示例 |
|--------|------|------|
| `DB_URL` | 数据库连接地址 | `jdbc:mysql://localhost:3306/db_personal?...` |
| `DB_USERNAME` | 数据库用户名 | `root` |
| `DB_PASSWORD` | 数据库密码 | `your-password` |
| `JWT_SECRET` | JWT密钥（至少32字符） | `abc123...` |
| `APP_ENCRYPTION_KEY` | 加密密钥（必须32字符） | `12345678901234567890123456789012` |
| `PYTHON_STOCK_SERVICE_URL` | Python服务地址 | `http://localhost:8000` |

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

