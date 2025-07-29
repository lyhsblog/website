# WebFlux 集成指南

## 概述

本项目已成功集成Spring WebFlux，提供了响应式编程能力，支持非阻塞I/O和流式数据处理。

## 两种使用方式

### 1. API端点方式（WebFluxTestView）
通过HTTP API端点提供WebFlux功能，适合：
- 外部系统调用
- 前端JavaScript调用
- 微服务间通信
- 测试和调试

> **⚠️ 重要说明：由于Vaadin的Spring MVC路由与WebFlux路由存在冲突，API端点形式目前暂时无法正常工作。**
>
> **问题描述：**
> - WebFlux API端点（如 `/api/webflux/hello`）被Vaadin的Spring MVC拦截
> - 返回Vaadin的HTML页面而不是预期的JSON响应
> - 这是Spring MVC和WebFlux在同一应用中并存时的已知限制
>
> **解决方案：**
> - 推荐使用服务端内部方式（WebFluxServiceView）
> - 或者将WebFlux API部署为独立的微服务
> - 或者使用不同的端口来避免路由冲突

### 2. 服务端内部方式（WebFluxServiceView）
在服务端内部直接使用WebFlux服务，适合：
- 异步业务逻辑处理
- 响应式数据流处理
- 非阻塞I/O操作
- 并发处理

> **✅ 推荐使用：服务端内部方式可以正常工作，性能更好，无需HTTP开销。**

## 主要功能

### 1. 响应式API端点
- `/api/webflux/hello` - 简单的Hello端点
- `/api/webflux/stream` - 流式数据端点
- `/api/webflux/process` - 数据处理端点
- `/api/webflux/tasks` - 任务列表端点

### 2. 核心组件

#### WebFluxConfig
- 配置WebFlux路由
- 启用WebFlux功能
- 定义API端点映射

#### WebFluxHandler
- 处理HTTP请求和响应
- 提供响应式端点实现
- 支持流式数据处理

#### WebFluxService
- 提供响应式业务逻辑
- 异步数据处理
- 实时数据流生成

#### WebFluxTestView
- Vaadin UI测试界面
- 测试API端点功能
- 实时显示流式数据

#### WebFluxServiceView
- 纯服务端WebFlux示例
- 展示服务端内部使用
- 异步任务处理
- 并发处理示例

## 使用方法

### 1. 启动应用
```bash
mvn spring-boot:run
```

### 2. 访问测试界面

#### API端点测试
打开浏览器访问：`http://localhost:8080/webflux-test`

#### 服务端内部测试
打开浏览器访问：`http://localhost:8080/webflux-service`

### 3. 测试API端点

> **⚠️ 注意：以下API端点由于路由冲突问题，目前无法正常工作。仅供参考。**

#### Hello端点
```bash
curl http://localhost:8080/api/webflux/hello
```

#### 流式数据
```bash
curl http://localhost:8080/api/webflux/stream
```

#### 处理数据
```bash
curl -X POST http://localhost:8080/api/webflux/process \
  -H "Content-Type: application/json" \
  -d '{"name": "test", "value": 123}'
```

#### 获取任务
```bash
curl http://localhost:8080/api/webflux/tasks
```

> **✅ 实际测试建议：使用WebFluxServiceView进行功能测试，该视图可以正常工作。**

## 技术特性

### 1. 响应式编程
- 使用Reactor (Mono/Flux)
- 非阻塞I/O操作
- 背压处理

### 2. 流式处理
- 实时数据流
- 服务器发送事件(SSE)
- 流式响应

### 3. 异步处理
- 异步业务逻辑
- 并发处理能力
- 资源高效利用

## 服务端内部使用示例

### 异步任务处理
```java
webFluxService.processTaskAsync(task)
    .subscribe(
        processedTask -> {
            // 处理完成后的逻辑
        },
        error -> {
            // 错误处理
        }
    );
```

### 实时数据流
```java
webFluxService.generateDataStream()
    .take(10)
    .subscribe(
        data -> {
            // 处理每个数据项
        },
        error -> {
            // 错误处理
        },
        () -> {
            // 流处理完成
        }
    );
```

### 并发处理
```java
Flux.zip(task1, task2, task3)
    .subscribe(
        results -> {
            // 所有任务完成后的处理
        }
    );
```

## 依赖说明

项目已添加以下WebFlux相关依赖：
- `spring-boot-starter-webflux` - WebFlux核心依赖
- `reactor-core` - 响应式编程库
- `spring-webflux` - WebFlux框架

## 注意事项

1. **⚠️ API端点限制：** WebFlux API端点与Vaadin的Spring MVC路由存在冲突，目前无法正常工作
2. WebFlux与传统Spring MVC可以共存，但路由优先级需要特别注意
3. 响应式编程需要不同的编程模型
4. 数据库操作需要适配响应式模式
5. 错误处理使用响应式方式
6. **✅ 推荐：** 服务端内部使用不需要HTTP请求，性能更好，无路由冲突问题

## 扩展建议

1. 集成响应式数据库（如R2DBC）
2. 添加WebSocket支持
3. 实现更复杂的流式处理
4. 添加响应式安全配置
5. 在更多业务场景中使用服务端WebFlux

## 相关文档

- [Spring WebFlux官方文档](https://docs.spring.io/spring-framework/reference/web/webflux.html)
- [Reactor参考指南](https://projectreactor.io/docs/core/release/reference/)
- [响应式编程最佳实践](https://projectreactor.io/docs/core/release/reference/#best-practices)
