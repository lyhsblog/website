# KIE Server 版本选择指南

## 版本说明

### 为什么选择 8.44.0.Final？

#### 1. **版本兼容性**
- **Spring Boot 3.x**: 8.44.0.Final 与 Spring Boot 3.5.x 完全兼容
- **Java 17+**: 支持 Java 17 及以上版本
- **现代技术栈**: 基于最新的 WildFly 应用服务器

#### 2. **功能特性**
- **增强的 REST API**: 更完善的 REST 服务接口
- **改进的性能**: 更好的内存管理和执行效率
- **安全增强**: 最新的安全补丁和认证机制
- **监控能力**: 更丰富的监控和日志功能

#### 3. **稳定性**
- **生产就绪**: 经过充分测试的生产环境版本
- **长期支持**: 获得 Red Hat 的长期支持
- **社区活跃**: 活跃的开发和维护社区

## 版本对比

| 版本 | Spring Boot 兼容性 | Java 版本 | 特性 | 推荐度 |
|------|-------------------|-----------|------|--------|
| 7.73.0.Final | 2.x | 8+ | 基础功能 | ⭐⭐⭐ |
| 8.44.0.Final | 3.x | 17+ | 现代特性 | ⭐⭐⭐⭐⭐ |
| 8.45.0.Final | 3.x | 17+ | 最新特性 | ⭐⭐⭐⭐ |

## 版本历史

### KIE Server 7.x 系列
- **7.73.0.Final**: 7.x 系列的最后一个稳定版本
- **适用场景**: 遗留系统、Spring Boot 2.x 项目
- **限制**: 不支持 Java 17+ 的新特性

### KIE Server 8.x 系列
- **8.44.0.Final**: 当前推荐的稳定版本
- **8.45.0.Final**: 最新版本，但可能存在稳定性问题
- **优势**: 完全支持现代 Java 和 Spring Boot 生态

## 迁移指南

### 从 7.x 迁移到 8.x

#### 1. **依赖更新**
```xml
<!-- 旧版本 -->
<dependency>
    <groupId>org.kie.server</groupId>
    <artifactId>kie-server-client</artifactId>
    <version>7.73.0.Final</version>
</dependency>

<!-- 新版本 -->
<dependency>
    <groupId>org.kie.server</groupId>
    <artifactId>kie-server-client</artifactId>
    <version>8.44.0.Final</version>
</dependency>
```

#### 2. **配置变更**
```properties
# 旧版本配置
kie.server.url=http://localhost:8080/kie-server

# 新版本配置（基本不变）
kie.server.url=http://localhost:8080/kie-server
```

#### 3. **API 变更**
```java
// 8.x 版本中的新 API
KieServicesConfiguration config = KieServicesFactory.newRestConfiguration(
    kieServerUrl, username, password);
config.setMarshallingFormat(MarshallingFormat.JSON);
// 新增：设置超时时间
config.setTimeout(30000);
```

## 生产环境建议

### 1. **版本选择策略**
- **开发环境**: 使用最新版本 (8.45.0.Final)
- **测试环境**: 使用稳定版本 (8.44.0.Final)
- **生产环境**: 使用稳定版本 (8.44.0.Final)

### 2. **升级计划**
```bash
# 1. 备份当前环境
docker commit <container_id> kie-server-backup

# 2. 测试新版本
docker run -p 8080:8080 jboss/kie-server-showcase:8.44.0.Final

# 3. 验证功能
curl -u kie-server:kie-server1! http://localhost:8080/kie-server/services/rest/server

# 4. 生产部署
docker-compose -f docker-compose-kie.yml up -d
```

### 3. **回滚策略**
```bash
# 如果新版本有问题，快速回滚
docker-compose -f docker-compose-kie.yml down
docker run -p 8080:8080 kie-server-backup
```

## 常见问题

### Q: 为什么不用最新版本 8.45.0.Final？
A: 最新版本可能存在稳定性问题，建议在生产环境使用经过充分测试的 8.44.0.Final。

### Q: 7.73.0.Final 还能用吗？
A: 可以，但建议升级到 8.x 系列以获得更好的性能和功能。

### Q: 如何检查当前版本？
```bash
curl -u kie-server:kie-server1! http://localhost:8080/kie-server/services/rest/server
```

## 总结

**推荐版本**: `8.44.0.Final`
- ✅ 与 Spring Boot 3.x 完全兼容
- ✅ 支持 Java 17+ 新特性
- ✅ 生产环境稳定可靠
- ✅ 功能丰富，性能优秀

**使用建议**:
1. 新项目直接使用 8.44.0.Final
2. 现有项目逐步迁移到 8.x 系列
3. 保持版本的一致性，避免混用不同版本
