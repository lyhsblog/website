# AMQP 集成指南

本项目已成功集成AMQP（Advanced Message Queuing Protocol）消息队列功能，使用RabbitMQ作为消息代理。

## 功能特性

- ✅ 通知消息队列（Notification Queue）
- ✅ JSON消息格式支持
- ✅ 消息发送和接收服务
- ✅ 自动消息监听器
- ✅ 测试界面
- ✅ Docker支持

## 快速开始

### 1. 启动RabbitMQ

使用Docker Compose启动RabbitMQ：

```bash
docker-compose up -d
```

或者手动启动RabbitMQ：

```bash
docker run -d --name rabbitmq -p 5672:5672 -p 15672:15672 rabbitmq:3-management
```

### 2. 启动应用

```bash
mvn spring-boot:run
```

### 3. 访问测试界面

打开浏览器访问：`http://localhost:8080/amqp-test`

## 配置说明

### RabbitMQ配置

在 `application.properties` 中配置RabbitMQ连接：

```properties
# RabbitMQ Configuration
spring.rabbitmq.host=localhost
spring.rabbitmq.port=5672
spring.rabbitmq.username=guest
spring.rabbitmq.password=guest
spring.rabbitmq.virtual-host=/
```

### 环境变量

可以通过环境变量覆盖默认配置：

- `RABBITMQ_HOST`: RabbitMQ主机地址
- `RABBITMQ_PORT`: RabbitMQ端口
- `RABBITMQ_USERNAME`: 用户名
- `RABBITMQ_PASSWORD`: 密码
- `RABBITMQ_VHOST`: 虚拟主机

## 消息类型

### 通知消息 (NotificationMessage)

用于处理系统通知：

```java
NotificationMessage notificationMessage = new NotificationMessage(
    "notification-id",
    "通知标题",
    "通知内容",
    "INFO", // 类型：INFO, SUCCESS, WARNING, ERROR
    "user-id"
);
```

## 使用示例

### 发送消息

```java
@Autowired
private AmqpMessageService amqpMessageService;

// 发送成功通知
amqpMessageService.sendSuccessNotification("成功", "操作成功完成", "用户ID");

// 发送错误通知
amqpMessageService.sendErrorNotification("错误", "操作失败", "用户ID");
```

### 接收消息

消息接收器会自动监听队列并处理消息：

- `NotificationMessageListener`: 处理通知消息

## 队列和交换机

### 通知队列
- **队列名称**: `notification.queue`
- **交换机**: `notification.exchange`
- **路由键**: `notification.routing.key`

## 监控和管理

### RabbitMQ管理界面

访问 `http://localhost:15672` 查看RabbitMQ管理界面：
- 用户名：`guest`
- 密码：`guest`

### 日志监控

应用日志会记录AMQP相关的操作：

```properties
logging.level.org.springframework.amqp=INFO
logging.level.cc.fss.vaadin.amqp=DEBUG
```

## 故障排除

### 连接问题

1. 确保RabbitMQ服务正在运行
2. 检查连接配置是否正确
3. 查看应用日志中的错误信息

### 消息丢失

1. 检查队列是否存在
2. 确认交换机绑定正确
3. 验证路由键配置

### 性能优化

1. 调整连接池大小
2. 配置消息持久化
3. 启用消息确认机制

## 扩展功能

### 添加新的消息类型

1. 创建新的消息模型类
2. 在 `AmqpConfig` 中添加队列和交换机配置
3. 创建对应的监听器
4. 在 `AmqpMessageService` 中添加发送方法

### 自定义消息处理

在监听器中实现自定义的业务逻辑：

```java
@RabbitListener(queues = "custom.queue")
public void handleCustomMessage(CustomMessage message) {
    // 实现自定义处理逻辑
}
```

## 安全考虑

1. 在生产环境中使用强密码
2. 配置SSL/TLS连接
3. 限制用户权限
4. 定期更新RabbitMQ版本

## 相关文档

- [Spring AMQP 文档](https://docs.spring.io/spring-amqp/docs/current/reference/html/)
- [RabbitMQ 文档](https://www.rabbitmq.com/documentation.html)
- [AMQP 协议规范](https://www.amqp.org/specification/0-9-1/amqp-org-download/)
