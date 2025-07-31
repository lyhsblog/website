# Vaadin Spring Boot 企业级应用

基于 Vaadin 24 和 Spring Boot 3.5 构建的现代化企业级 Web 应用，集成了多种企业级功能。

## 🚀 快速开始

### 开发环境启动

```bash
# 克隆项目
git clone <repository-url>
cd website

# 启动应用
./mvnw
```

### 生产环境构建

```bash
./mvnw -Pproduction package
```

## 📚 功能特性

### 核心功能
- ✅ **Vaadin 24** - 现代化 Web UI 框架
- ✅ **Spring Boot 3.5** - 企业级应用框架
- ✅ **PostgreSQL** - 企业级数据库
- ✅ **Flyway** - 数据库版本管理
- ✅ **Spring Security** - 安全认证
- ✅ **AMQP/RabbitMQ** - 消息队列集成

### 企业级集成
- ✅ **KIE Server** - 规则引擎集成
- ✅ **WebFlux** - 响应式编程
- ✅ **LitTemplate** - 自定义组件
- ✅ **ArchUnit** - 架构测试

## 🔧 集成指南

### KIE Server 规则引擎
- [快速开始指南](KIE_SERVER_QUICKSTART.md) - 5分钟快速集成
- [完整集成指南](KIE_SERVER_INTEGRATION_GUIDE.md) - 详细配置和最佳实践
- [版本选择指南](KIE_SERVER_VERSION_GUIDE.md) - 版本兼容性和迁移说明

### 其他集成
- [AMQP 集成指南](AMQP_INTEGRATION_GUIDE.md) - RabbitMQ 消息队列
- [WebFlux 集成指南](WEBFLUX_INTEGRATION_GUIDE.md) - 响应式编程
- [LitTemplate 指南](LITTEMPLATE_GUIDE.md) - 自定义 Vaadin 组件
- [Flyway 迁移指南](FLYWAY_MIGRATION_GUIDE.md) - 数据库版本管理

## 🏗️ 项目结构

```
src/
├── main/
│   ├── java/cc/fss/vaadin/
│   │   ├── config/          # 配置类
│   │   ├── security/        # 安全配置
│   │   ├── taskmanagement/  # 任务管理模块
│   │   ├── amqp/           # AMQP 消息队列
│   │   ├── webflux/        # WebFlux 响应式
│   │   └── ui/             # Vaadin UI 组件
│   ├── frontend/           # 前端资源
│   └── resources/          # 配置文件
└── test/                   # 测试代码
```

## 🐳 Docker 部署

### 启动 KIE Server
```bash
docker-compose -f docker-compose-kie.yml up -d
```

### 启动应用
```bash
docker build -t vaadin-app .
docker run -p 8080:8080 vaadin-app
```

## 📖 文档资源

- [Vaadin 官方文档](https://vaadin.com/docs/latest/getting-started)
- [Spring Boot 文档](https://spring.io/projects/spring-boot)
- [Drools 规则引擎](https://www.drools.org/learn/documentation.html)
- [ArchUnit 架构测试](https://www.archunit.org/)

## 🤝 贡献指南

1. Fork 项目
2. 创建功能分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 打开 Pull Request

## 📄 许可证

本项目采用 [MIT 许可证](LICENSE.md)。
