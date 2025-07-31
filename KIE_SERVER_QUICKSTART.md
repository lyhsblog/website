# KIE Server 快速开始指南

## 5分钟快速集成

### 1. 添加依赖

在 `pom.xml` 中添加：

```xml
<dependency>
    <groupId>org.kie.server</groupId>
    <artifactId>kie-server-client</artifactId>
    <version>8.44.0.Final</version>
</dependency>
<dependency>
    <groupId>org.kie</groupId>
    <artifactId>kie-api</artifactId>
    <version>8.44.0.Final</version>
</dependency>
```

### 2. 创建配置类

```java:src/main/java/cc/fss/vaadin/config/KieServerConfig.java
@Configuration
public class KieServerConfig {

    @Bean
    public KieServicesClient kieServicesClient() {
        KieServicesConfiguration config = KieServicesFactory.newRestConfiguration(
                "http://localhost:8080/kie-server",
                "kie-server",
                "kie-server1!");
        config.setMarshallingFormat(MarshallingFormat.JSON);
        return KieServicesFactory.newKieServicesClient(config);
    }
}
```

### 3. 创建服务类

```java:src/main/java/cc/fss/vaadin/service/KieRuleService.java
@Service
public class KieRuleService {

    private final KieServicesClient kieServicesClient;

    public KieRuleService(KieServicesClient kieServicesClient) {
        this.kieServicesClient = kieServicesClient;
    }

    public <T> T executeRules(String containerId, T fact) {
        RuleServicesClient ruleClient = kieServicesClient.getServicesClient(RuleServicesClient.class);
        return ruleClient.executeCommandsWithResults(containerId,
            kieServicesClient.getCommandsFactory().newInsert(fact));
    }
}
```

### 4. 启动 KIE Server

```bash
# 使用 Docker 启动
docker run -p 8080:8080 -e KIE_SERVER_USER=kie-server -e KIE_SERVER_PASSWORD=kie-server1! jboss/kie-server-showcase:8.44.0.Final
```

### 5. 测试连接

```java
@RestController
public class TestController {

    @Autowired
    private KieRuleService kieRuleService;

    @GetMapping("/test-kie")
    public String testKie() {
        try {
            // 测试连接
            kieServicesClient.getServerInfo();
            return "KIE Server 连接成功！";
        } catch (Exception e) {
            return "连接失败: " + e.getMessage();
        }
    }
}
```

## 常用命令

### 启动服务

```bash
# 启动 KIE Server
docker-compose -f docker-compose-kie.yml up -d

# 启动 KIE Workbench
docker run -p 8081:8080 -e KIE_ADMIN_USER=admin -e KIE_ADMIN_PASSWORD=admin jboss/kie-workbench-showcase:8.44.0.Final
```

### 检查状态

```bash
# 检查 KIE Server 状态
curl -u kie-server:kie-server1! http://localhost:8080/kie-server/services/rest/server

# 检查容器列表
curl -u kie-server:kie-server1! http://localhost:8080/kie-server/services/rest/server/containers
```

### 部署规则

```bash
# 部署 KJAR 到 KIE Server
curl -X PUT -H "Content-Type: application/json" -u kie-server:kie-server1! \
  -d '{"container-id":"order-rules","release-id":{"group-id":"cc.fss.vaadin","artifact-id":"order-rules","version":"1.0.0"}}' \
  http://localhost:8080/kie-server/services/rest/server/containers/order-rules
```

## 常见问题解决

### 1. 连接被拒绝

```bash
# 检查端口是否被占用
netstat -an | grep 8080

# 检查防火墙设置
firewall-cmd --list-ports
```

### 2. 认证失败

```bash
# 重置 KIE Server 密码
docker exec -it <container_id> /opt/jboss/wildfly/bin/add-user.sh -a -u kie-server -p kie-server1! -g kie-server
```

### 3. 规则执行失败

```bash
# 检查容器状态
curl -u kie-server:kie-server1! http://localhost:8080/kie-server/services/rest/server/containers/order-rules

# 查看详细日志
docker logs <container_id>
```

## 示例规则

### 简单折扣规则

```drl
package cc.fss.vaadin.rules;

import cc.fss.vaadin.model.Order;

rule "VIP Discount"
when
    $order : Order(customerType == "VIP", amount > 500)
then
    $order.setDiscount(new java.math.BigDecimal("0.10"));
end
```

### 复杂业务规则

```drl
package cc.fss.vaadin.rules;

import cc.fss.vaadin.model.Order;
import java.math.BigDecimal;

rule "Seasonal Discount"
when
    $order : Order(amount > 1000)
    eval(java.time.LocalDateTime.now().getMonthValue() == 12)
then
    $order.setDiscount(new BigDecimal("0.20"));
    $order.setDiscountReason("圣诞季特别折扣");
end
```

## 性能优化

### 1. 连接池配置

```java
@Configuration
public class KieServerConfig {

    @Bean
    public KieServicesClient kieServicesClient() {
        KieServicesConfiguration config = KieServicesFactory.newRestConfiguration(
                kieServerUrl, username, password);
        config.setMarshallingFormat(MarshallingFormat.JSON);

        // 设置连接池
        config.setTimeout(30000);
        config.setExtraJaxbClasses(Arrays.asList(Order.class));

        return KieServicesFactory.newKieServicesClient(config);
    }
}
```

### 2. 批量处理

```java
@Service
public class BatchRuleService {

    public List<Order> processBatch(List<Order> orders) {
        List<Order> results = new ArrayList<>();

        for (Order order : orders) {
            try {
                Order processed = kieRuleService.executeRules("order-rules", order);
                results.add(processed);
            } catch (Exception e) {
                // 记录错误，继续处理其他订单
                log.error("处理订单失败: " + order.getOrderId(), e);
            }
        }

        return results;
    }
}
```

## 监控和日志

### 1. 应用日志

```properties
# application.properties
logging.level.org.kie.server=INFO
logging.level.cc.fss.vaadin.service=DEBUG
logging.level.org.drools=INFO
```

### 2. 性能监控

```java
@Component
public class KiePerformanceMonitor {

    private final MeterRegistry meterRegistry;

    public KiePerformanceMonitor(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    public <T> T executeWithMonitoring(String containerId, T fact, Supplier<T> operation) {
        Timer.Sample sample = Timer.start(meterRegistry);
        try {
            T result = operation.get();
            sample.stop(Timer.builder("kie.rule.execution")
                    .tag("container", containerId)
                    .tag("status", "success")
                    .register(meterRegistry));
            return result;
        } catch (Exception e) {
            sample.stop(Timer.builder("kie.rule.execution")
                    .tag("container", containerId)
                    .tag("status", "error")
                    .register(meterRegistry));
            throw e;
        }
    }
}
```

## 下一步

1. 阅读完整的 [KIE Server 集成指南](KIE_SERVER_INTEGRATION_GUIDE.md)
2. 探索 [KIE Workbench](http://localhost:8081/kie-wb) 进行规则管理
3. 查看 [Drools 官方文档](https://www.drools.org/learn/documentation.html)
4. 加入 [Drools 社区](https://groups.google.com/forum/#!forum/drools-setup)
