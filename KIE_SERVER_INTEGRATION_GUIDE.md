# KIE Server 集成指南

## 概述

本指南详细介绍了如何在Vaadin Spring Boot应用中集成KIE Server，实现规则引擎的远程调用和管理。KIE Server提供了集中化的规则管理、动态部署和版本控制功能。

## 架构设计

### 集成方式对比

| 特性 | 直接集成KJAR | KIE Server |
|------|-------------|------------|
| 部署复杂度 | 简单 | 中等 |
| 性能 | 高（本地执行） | 中等（网络调用） |
| 规则管理 | 静态 | 动态 |
| 扩展性 | 有限 | 高 |
| 监控能力 | 基础 | 丰富 |
| 适用场景 | 中小型项目 | 企业级项目 |

### 系统架构

```
┌─────────────────┐    HTTP/REST    ┌─────────────────┐
│   Vaadin App    │ ──────────────► │   KIE Server    │
│   (Spring Boot) │                 │                 │
│                 │ ◄────────────── │                 │
└─────────────────┘                 └─────────────────┘
         │                                   │
         │                                   │
         ▼                                   ▼
┌─────────────────┐                 ┌─────────────────┐
│   PostgreSQL    │                 │   KIE Workbench │
│   (业务数据)     │                 │   (规则管理)     │
└─────────────────┘                 └─────────────────┘
```

## 依赖配置

### Maven 依赖

在 `pom.xml` 中添加以下依赖：

```xml
<!-- KIE Server Client Dependencies -->
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
<dependency>
    <groupId>org.kie</groupId>
    <artifactId>kie-internal</artifactId>
    <version>8.44.0.Final</version>
</dependency>
<dependency>
    <groupId>org.kie</groupId>
    <artifactId>kie-ci</artifactId>
    <version>8.44.0.Final</version>
</dependency>
```

### 版本兼容性

| Spring Boot 版本 | KIE Server 版本 | Java 版本 |
|-----------------|----------------|-----------|
| 3.5.x           | 8.44.0.Final   | 17+       |
| 3.4.x           | 8.44.0.Final   | 17+       |
| 3.3.x           | 8.44.0.Final   | 17+       |

## 配置实现

### 1. KIE Server 配置类

```java:src/main/java/cc/fss/vaadin/config/KieServerConfig.java
package cc.fss.vaadin.config;

import org.kie.server.api.marshalling.MarshallingFormat;
import org.kie.server.client.KieServicesClient;
import org.kie.server.client.KieServicesConfiguration;
import org.kie.server.client.KieServicesFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KieServerConfig {

    @Value("${kie.server.url:http://localhost:8080/kie-server}")
    private String kieServerUrl;

    @Value("${kie.server.username:kie-server}")
    private String username;

    @Value("${kie.server.password:kie-server1!}")
    private String password;

    @Bean
    public KieServicesClient kieServicesClient() {
        KieServicesConfiguration config = KieServicesFactory.newRestConfiguration(
                kieServerUrl, username, password);
        config.setMarshallingFormat(MarshallingFormat.JSON);
        return KieServicesFactory.newKieServicesClient(config);
    }
}
```

### 2. 规则服务类

```java:src/main/java/cc/fss/vaadin/service/KieRuleService.java
package cc.fss.vaadin.service;

import org.kie.api.runtime.process.ProcessInstance;
import org.kie.server.api.model.instance.ProcessInstanceList;
import org.kie.server.client.KieServicesClient;
import org.kie.server.client.ProcessServicesClient;
import org.kie.server.client.RuleServicesClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class KieRuleService {

    private final KieServicesClient kieServicesClient;
    private final RuleServicesClient ruleServicesClient;
    private final ProcessServicesClient processServicesClient;

    @Autowired
    public KieRuleService(KieServicesClient kieServicesClient) {
        this.kieServicesClient = kieServicesClient;
        this.ruleServicesClient = kieServicesClient.getServicesClient(RuleServicesClient.class);
        this.processServicesClient = kieServicesClient.getServicesClient(ProcessServicesClient.class);
    }

    /**
     * 执行规则
     */
    public <T> T executeRules(String containerId, String sessionName, T fact) {
        return ruleServicesClient.executeCommandsWithResults(containerId,
            kieServicesClient.getCommandsFactory().newInsert(fact));
    }

    /**
     * 执行规则并返回结果
     */
    public <T> T executeRulesWithResults(String containerId, String sessionName, T fact, Class<T> resultType) {
        return ruleServicesClient.executeCommandsWithResults(containerId,
            kieServicesClient.getCommandsFactory().newInsert(fact), resultType);
    }

    /**
     * 启动流程
     */
    public Long startProcess(String containerId, String processId, Map<String, Object> parameters) {
        return processServicesClient.startProcess(containerId, processId, parameters);
    }

    /**
     * 获取流程实例列表
     */
    public ProcessInstanceList getProcessInstances(String containerId) {
        return processServicesClient.findProcessInstances(containerId, 0, 100);
    }

    /**
     * 获取容器信息
     */
    public org.kie.server.api.model.KieContainerResource getContainerInfo(String containerId) {
        return kieServicesClient.getContainerInfo(containerId);
    }
}
```

### 3. 应用配置

在 `application.properties` 中添加：

```properties
# KIE Server Configuration
kie.server.url=http://localhost:8080/kie-server
kie.server.username=kie-server
kie.server.password=kie-server1!

# KIE Server Logging
logging.level.org.kie.server=INFO
logging.level.cc.fss.vaadin.service=DEBUG
```

## 业务模型

### 订单模型示例

```java:src/main/java/cc/fss/vaadin/drools/model/Order.java
package cc.fss.vaadin.drools.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Order {
    private String orderId;
    private String customerId;
    private BigDecimal amount;
    private String customerType; // REGULAR, VIP, PREMIUM
    private LocalDateTime orderDate;
    private BigDecimal discount;
    private String discountReason;

    // Constructors
    public Order() {}

    public Order(String orderId, String customerId, BigDecimal amount, String customerType) {
        this.orderId = orderId;
        this.customerId = customerId;
        this.amount = amount;
        this.customerType = customerType;
        this.orderDate = LocalDateTime.now();
    }

    // Getters and Setters
    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }

    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public String getCustomerType() { return customerType; }
    public void setCustomerType(String customerType) { this.customerType = customerType; }

    public LocalDateTime getOrderDate() { return orderDate; }
    public void setOrderDate(LocalDateTime orderDate) { this.orderDate = orderDate; }

    public BigDecimal getDiscount() { return discount; }
    public void setDiscount(BigDecimal discount) { this.discount = discount; }

    public String getDiscountReason() { return discountReason; }
    public void setDiscountReason(String discountReason) { this.discountReason = discountReason; }

    @Override
    public String toString() {
        return "Order{" +
                "orderId='" + orderId + '\'' +
                ", customerId='" + customerId + '\'' +
                ", amount=" + amount +
                ", customerType='" + customerType + '\'' +
                ", discount=" + discount +
                ", discountReason='" + discountReason + '\'' +
                '}';
    }
}
```

## 业务服务

### 订单服务

```java:src/main/java/cc/fss/vaadin/service/OrderService.java
package cc.fss.vaadin.service;

import cc.fss.vaadin.drools.model.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class OrderService {

    private final KieRuleService kieRuleService;

    @Autowired
    public OrderService(KieRuleService kieRuleService) {
        this.kieRuleService = kieRuleService;
    }

    /**
     * 处理订单并应用规则
     */
    public Order processOrder(Order order) {
        // 假设容器ID为 "order-rules"
        String containerId = "order-rules";
        String sessionName = "order-session";

        // 执行规则
        Order processedOrder = kieRuleService.executeRules(containerId, sessionName, order);

        return processedOrder;
    }

    /**
     * 创建示例订单
     */
    public Order createSampleOrder(String customerType) {
        return new Order(
            "ORD-" + System.currentTimeMillis(),
            "CUST-001",
            new BigDecimal("1000.00"),
            customerType
        );
    }
}
```

## 用户界面

### KIE Server 视图

```java:src/main/java/cc/fss/vaadin/ui/view/KieServerView.java
package cc.fss.vaadin.ui.view;

import cc.fss.vaadin.drools.model.Order;
import cc.fss.vaadin.service.OrderService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.PageTitle;

@Route(value = "kie-server", layout = MainLayout.class)
@PageTitle("KIE Server Integration")
public class KieServerView extends VerticalLayout {

    private final OrderService orderService;
    private final TextArea resultArea;

    public KieServerView(OrderService orderService) {
        this.orderService = orderService;

        setSizeFull();
        setPadding(true);
        setSpacing(true);

        // 标题
        add(new H3("KIE Server 规则引擎集成"));

        // 客户类型选择
        ComboBox<String> customerTypeCombo = new ComboBox<>("客户类型");
        customerTypeCombo.setItems("REGULAR", "VIP", "PREMIUM");
        customerTypeCombo.setValue("REGULAR");

        // 处理按钮
        Button processButton = new Button("处理订单", e -> {
            try {
                String customerType = customerTypeCombo.getValue();
                Order order = orderService.createSampleOrder(customerType);

                // 显示原始订单
                resultArea.setValue("原始订单:\n" + order.toString() + "\n\n");

                // 处理订单
                Order processedOrder = orderService.processOrder(order);

                // 显示处理结果
                resultArea.setValue(resultArea.getValue() + "处理后的订单:\n" + processedOrder.toString());

                Notification.show("订单处理完成！", 3000, Notification.Position.MIDDLE);
            } catch (Exception ex) {
                resultArea.setValue("错误: " + ex.getMessage());
                Notification.show("处理失败: " + ex.getMessage(), 5000, Notification.Position.MIDDLE);
            }
        });

        // 结果显示区域
        resultArea = new TextArea("处理结果");
        resultArea.setWidth("100%");
        resultArea.setHeight("300px");
        resultArea.setReadOnly(true);

        // 添加组件
        add(customerTypeCombo, processButton, resultArea);
    }
}
```

## 部署配置

### Docker Compose 配置

```yaml:docker-compose-kie.yml
version: '3.8'

services:
  kie-server:
    image: jboss/kie-server-showcase:7.73.0.Final
    ports:
      - "8080:8080"
    environment:
      - KIE_SERVER_USER=kie-server
      - KIE_SERVER_PASSWORD=kie-server1!
      - KIE_SERVER_LOCATION=http://localhost:8080/kie-server/services/rest/server
    volumes:
      - kie-server-data:/opt/jboss/wildfly/standalone/data
    networks:
      - kie-network

  kie-workbench:
    image: jboss/kie-workbench-showcase:7.73.0.Final
    ports:
      - "8081:8080"
    environment:
      - KIE_ADMIN_USER=admin
      - KIE_ADMIN_PASSWORD=admin
    volumes:
      - kie-workbench-data:/opt/jboss/wildfly/standalone/data
    networks:
      - kie-network

volumes:
  kie-server-data:
  kie-workbench-data:

networks:
  kie-network:
    driver: bridge
```

## 使用指南

### 1. 启动 KIE Server

```bash
# 启动 KIE Server 和 Workbench
docker-compose -f docker-compose-kie.yml up -d

# 检查服务状态
docker-compose -f docker-compose-kie.yml ps
```

### 2. 访问 KIE Workbench

- **URL**: http://localhost:8081/kie-wb
- **用户名**: admin
- **密码**: admin

### 3. 创建规则项目

1. 登录 KIE Workbench
2. 创建新的规则项目
3. 添加规则文件（.drl）
4. 构建并部署到 KIE Server

### 4. 示例规则

```drl
package cc.fss.vaadin.drools.rules;

import cc.fss.vaadin.drools.model.Order;

rule "VIP Customer Discount"
when
    $order : Order(customerType == "VIP", amount > 500)
then
    $order.setDiscount(new java.math.BigDecimal("0.10"));
    $order.setDiscountReason("VIP客户折扣");
end

rule "Premium Customer Discount"
when
    $order : Order(customerType == "PREMIUM", amount > 1000)
then
    $order.setDiscount(new java.math.BigDecimal("0.15"));
    $order.setDiscountReason("Premium客户折扣");
end

rule "Regular Customer Discount"
when
    $order : Order(customerType == "REGULAR", amount > 2000)
then
    $order.setDiscount(new java.math.BigDecimal("0.05"));
    $order.setDiscountReason("普通客户大额订单折扣");
end
```

### 5. 测试集成

1. 启动 Vaadin 应用
2. 访问 http://localhost:8080/kie-server
3. 选择客户类型并点击"处理订单"
4. 查看规则执行结果

## 监控和管理

### KIE Server REST API

| 端点 | 描述 | 方法 |
|------|------|------|
| `/kie-server/services/rest/server` | 服务器信息 | GET |
| `/kie-server/services/rest/server/containers` | 容器列表 | GET |
| `/kie-server/services/rest/server/containers/{containerId}` | 容器详情 | GET |
| `/kie-server/services/rest/server/containers/{containerId}/release-id` | 发布版本 | GET |

### 健康检查

```bash
# 检查 KIE Server 状态
curl -u kie-server:kie-server1! \
     http://localhost:8080/kie-server/services/rest/server

# 检查容器状态
curl -u kie-server:kie-server1! \
     http://localhost:8080/kie-server/services/rest/server/containers
```

## 故障排除

### 常见问题

1. **连接失败**
   - 检查 KIE Server 是否启动
   - 验证 URL 和认证信息
   - 检查网络连接

2. **规则执行失败**
   - 确认容器已部署
   - 检查规则语法
   - 验证模型类路径

3. **性能问题**
   - 优化规则复杂度
   - 使用规则缓存
   - 考虑规则分组

### 日志配置

```properties
# 详细日志
logging.level.org.kie.server=DEBUG
logging.level.cc.fss.vaadin.service=DEBUG

# 规则执行日志
logging.level.org.drools=INFO
```

## 最佳实践

### 1. 规则设计

- 保持规则简单和可读
- 使用有意义的规则名称
- 避免规则间的复杂依赖

### 2. 性能优化

- 使用规则缓存
- 批量处理数据
- 优化规则条件

### 3. 安全考虑

- 使用 HTTPS 连接
- 实施适当的认证
- 限制规则访问权限

### 4. 版本管理

- 使用语义化版本
- 保持向后兼容
- 实施回滚策略

## 扩展功能

### 1. 规则版本管理

```java
// 获取特定版本的规则
public void executeRulesWithVersion(String containerId, String version, Object fact) {
    // 实现版本控制逻辑
}
```

### 2. 规则监控

```java
// 添加规则执行监控
public void addRuleExecutionListener() {
    // 实现监控逻辑
}
```

### 3. 规则热部署

```java
// 动态更新规则
public void updateRules(String containerId, String newVersion) {
    // 实现热部署逻辑
}
```

## 总结

KIE Server 集成提供了强大的规则管理能力，适合企业级应用的需求。通过合理的架构设计和配置，可以实现高效的规则引擎集成。

### 关键优势

- **集中管理**: 统一的规则管理平台
- **动态部署**: 支持规则热更新
- **版本控制**: 完整的规则版本管理
- **监控能力**: 丰富的执行监控功能
- **扩展性**: 支持多租户和分布式部署

### 适用场景

- 复杂的业务规则管理
- 需要频繁规则变更的场景
- 多应用共享规则的情况
- 企业级规则治理需求
