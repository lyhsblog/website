# Flyway 数据库迁移指南

## 概述

本项目已从 Liquibase 迁移到 Flyway 进行数据库版本管理。Flyway 是 Vaadin 官方推荐的数据库迁移工具，提供了简单、可靠和版本化的数据库架构管理。

## 配置

### 依赖配置

在 `pom.xml` 中添加了 Flyway 依赖：

```xml
<dependency>
    <groupId>org.flywaydb</groupId>
    <artifactId>flyway-core</artifactId>
</dependency>
```

### 应用配置

在 `application.properties` 中配置了 Flyway：

```properties
# Flyway Configuration
spring.flyway.enabled=true
spring.flyway.locations=classpath:db/migration
spring.flyway.baseline-on-migrate=true
spring.flyway.validate-on-migrate=true

# 禁用 Hibernate 自动 DDL 生成
spring.jpa.hibernate.ddl-auto=validate
```

## 迁移文件结构

迁移文件位于 `src/main/resources/db/migration/` 目录下，命名格式为：

```
V<version>__<description>.sql
```

例如：
- `V1__Create_task_table.sql` - 创建任务表
- `V2__Add_user_table.sql` - 添加用户表
- `V3__Add_indexes.sql` - 添加索引

## 当前迁移文件

### V1__Create_task_table.sql

创建了 `task` 表，包含以下字段：
- `task_id` (BIGINT PRIMARY KEY) - 任务ID，使用序列 `task_seq` 自动生成
- `description` (VARCHAR(255) NOT NULL) - 任务描述
- `creation_date` (TIMESTAMP NOT NULL) - 创建时间
- `due_date` (DATE) - 截止日期

并创建了性能优化索引：
- `idx_task_creation_date` - 创建时间索引
- `idx_task_due_date` - 截止日期索引

**注意**：使用序列而不是 BIGSERIAL，以匹配 Task 实体的 `@GeneratedValue(strategy = GenerationType.SEQUENCE)` 配置。

## 使用方法

### 开发环境

1. **启动应用**：Flyway 会在应用启动时自动执行迁移
2. **查看迁移状态**：可以通过日志查看迁移执行情况

### 生产环境

1. **手动迁移**：在生产环境部署前，建议手动执行迁移
2. **备份数据库**：执行迁移前务必备份数据库
3. **测试迁移**：在测试环境验证迁移脚本

### 创建新的迁移文件

1. 在 `src/main/resources/db/migration/` 目录下创建新的 SQL 文件
2. 文件名格式：`V<下一个版本号>__<描述>.sql`
3. 编写 SQL 语句
4. 重启应用或手动执行迁移

## 最佳实践

1. **版本号递增**：确保版本号严格递增
2. **描述清晰**：使用清晰的描述说明迁移内容
3. **幂等性**：迁移脚本应该是幂等的（可重复执行）
4. **测试**：在测试环境验证迁移脚本
5. **备份**：生产环境执行前务必备份

## 常用命令

### Maven 命令

```bash
# 查看迁移信息
mvn flyway:info

# 执行迁移
mvn flyway:migrate

# 验证迁移
mvn flyway:validate

# 清理数据库（仅开发环境）
mvn flyway:clean
```

### 应用内查看

启动应用后，可以通过日志查看 Flyway 执行情况：

```
2024-01-01 10:00:00.000  INFO 12345 --- [main] o.f.c.internal.license.VersionPrinter    : Flyway Community Edition 9.22.3 by Redgate
2024-01-01 10:00:00.000  INFO 12345 --- [main] o.f.c.internal.database.base.DatabaseType : Database: jdbc:postgresql://localhost:5432/mydb (PostgreSQL 15.0)
2024-01-01 10:00:00.000  INFO 12345 --- [main] o.f.c.internal.command.DbMigrate         : Current version of schema "public": 1
2024-01-01 10:00:00.000  INFO 12345 --- [main] o.f.c.internal.command.DbMigrate         : Schema "public" is up to date. No migration necessary.
```

## 注意事项

1. **版本控制**：迁移文件应该纳入版本控制
2. **不可修改**：已执行的迁移文件不应修改
3. **回滚**：Flyway Community Edition 不支持自动回滚，需要手动编写回滚脚本
4. **团队协作**：团队成员应该协调迁移文件的创建

## 故障排除

### 常见问题

1. **版本冲突**：确保版本号不重复
2. **SQL 语法错误**：检查 SQL 语句语法
3. **权限问题**：确保数据库用户有足够权限
4. **锁定问题**：确保没有其他进程正在修改数据库架构
5. **Hibernate 与 Flyway 配置不匹配**：
   - 确保 Flyway 迁移文件中的表结构与 Hibernate 实体配置一致
   - 如果实体使用 `GenerationType.SEQUENCE`，迁移文件应创建对应的序列
   - 如果实体使用 `GenerationType.IDENTITY`，迁移文件应使用 `SERIAL` 或 `BIGSERIAL`
   - 设置 `spring.jpa.hibernate.ddl-auto=validate` 让 Hibernate 验证架构而不自动创建

### 日志级别

可以通过以下配置调整 Flyway 日志级别：

```properties
logging.level.org.flywaydb=DEBUG
```

## 参考资源

- [Flyway 官方文档](https://flywaydb.org/documentation/)
- [Vaadin Flyway 集成指南](https://vaadin.com/docs/latest/building-apps/forms-data/add-flyway)
- [Spring Boot Flyway 集成](https://docs.spring.io/spring-boot/docs/current/reference/html/howto.html#howto.data-initialization.migration-tool.flyway)
