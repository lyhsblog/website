
# 📦 Vaadin Flow SEO 预渲染部署文档（使用 Prerender + Nginx）

## ✅ 项目信息

- **项目名称**: cc.fss.vaadin.website
- **Vaadin 版本**: 24.8.4
- **Spring Boot 版本**: 3.5.4
- **Java 版本**: 17
- **数据库**: PostgreSQL + Flyway
- **消息队列**: RabbitMQ
- **端口**: 8080

## ✅ 目录结构

```
website/
├── docker/
│   ├── prerender/
│   │   └── Dockerfile
│   └── nginx/
│       └── default.conf
├── docker-compose.yml
├── docker-compose-kie.yml
├── Dockerfile
└── README.md
```

## 1️⃣ 启动先决条件

- Docker & Docker Compose 安装好
- 已构建的 Vaadin Flow 应用 JAR 包（`target/website-1.0-SNAPSHOT.jar`）
- PostgreSQL 数据库（可通过 Docker 部署）
- RabbitMQ 服务（项目已配置）
- 本机或云服务器具备域名解析和公网可访问

## 2️⃣ 构建 prerender 服务（Node.js）

在 `docker/prerender/Dockerfile` 中写入：

```dockerfile
FROM node:20-alpine

WORKDIR /app

# 安装 prerender 和必要的依赖
RUN npm install -g prerender

# 创建非 root 用户
RUN addgroup -g 1001 -S nodejs && \
    adduser -S prerender -u 1001

USER prerender

EXPOSE 3000

CMD ["prerender"]
```

## 3️⃣ 配置 Nginx 代理（含爬虫判断）

在 `docker/nginx/default.conf` 中写入：

```nginx
upstream vaadin_backend {
    server vaadin:8080;
}

upstream prerender_backend {
    server prerender:3000;
}

server {
    listen 80;
    server_name yourdomain.com;

    # 健康检查端点
    location /health {
        proxy_pass http://vaadin_backend/actuator/health;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }

    location / {
        # 设置默认不使用 prerender
        set $prerender 0;

        # 判断是否是爬虫 UA
        if ($http_user_agent ~* "Googlebot|Bingbot|Yandex|facebookexternalhit|Twitterbot|LinkedInBot|WhatsApp|TelegramBot|Slackbot|Discordbot|SkypeUriPreview|Slack-ImgProxy|WhatsApp|Telegram|Discord|Slack") {
            set $prerender 1;
        }

        # 检查是否有 _escaped_fragment_ 参数（用于手动测试）
        if ($args ~ "_escaped_fragment_") {
            set $prerender 1;
        }

        # 转发爬虫请求到 prerender 服务
        if ($prerender = 1) {
            proxy_pass http://prerender_backend/;
            proxy_set_header X-Prerender-Token YOUR_TOKEN; # 可选，默认不校验
            proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
            proxy_set_header Host $host;
            proxy_set_header X-Original-URL $scheme://$host$request_uri;
            proxy_set_header X-Forwarded-Proto $scheme;
            proxy_pass_request_headers on;

            # 设置超时时间
            proxy_connect_timeout 30s;
            proxy_send_timeout 30s;
            proxy_read_timeout 30s;
        }

        # 正常用户请求走 Spring Boot（Vaadin）
        if ($prerender = 0) {
            proxy_pass http://vaadin_backend;
            proxy_set_header Host $host;
            proxy_set_header X-Real-IP $remote_addr;
            proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
            proxy_set_header X-Forwarded-Proto $scheme;

            # WebSocket 支持（Vaadin 需要）
            proxy_http_version 1.1;
            proxy_set_header Upgrade $http_upgrade;
            proxy_set_header Connection "upgrade";
        }
    }

    # 静态资源缓存
    location ~* \.(js|css|png|jpg|jpeg|gif|ico|svg|woff|woff2|ttf|eot)$ {
        proxy_pass http://vaadin_backend;
        proxy_set_header Host $host;
        expires 1y;
        add_header Cache-Control "public, immutable";
    }
}
```

## 4️⃣ Docker Compose 一键部署

在根目录创建 `docker-compose-prerender.yml`：

```yaml
version: '3.8'

services:
  # PostgreSQL 数据库
  postgres:
    image: postgres:15-alpine
    container_name: vaadin-postgres
    environment:
      POSTGRES_DB: vaadin_db
      POSTGRES_USER: vaadin_user
      POSTGRES_PASSWORD: vaadin_password
    volumes:
      - postgres_data:/var/lib/postgresql/data
    ports:
      - "5432:5432"
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U vaadin_user -d vaadin_db"]
      interval: 30s
      timeout: 10s
      retries: 5

  # RabbitMQ 消息队列
  rabbitmq:
    image: rabbitmq:3-management-alpine
    container_name: vaadin-rabbitmq
    environment:
      RABBITMQ_DEFAULT_USER: guest
      RABBITMQ_DEFAULT_PASS: guest
    volumes:
      - rabbitmq_data:/var/lib/rabbitmq
    ports:
      - "5672:5672"
      - "15672:15672"
    healthcheck:
      test: ["CMD", "rabbitmq-diagnostics", "ping"]
      interval: 30s
      timeout: 10s
      retries: 5

  # Vaadin 应用
  vaadin:
    build:
      context: .
      dockerfile: Dockerfile
    container_name: vaadin-app
    environment:
      - SPRING_PROFILES_ACTIVE=prod
      - SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/vaadin_db
      - SPRING_DATASOURCE_USERNAME=vaadin_user
      - SPRING_DATASOURCE_PASSWORD=vaadin_password
      - RABBITMQ_HOST=rabbitmq
      - RABBITMQ_PORT=5672
      - RABBITMQ_USERNAME=guest
      - RABBITMQ_PASSWORD=guest
    depends_on:
      postgres:
        condition: service_healthy
      rabbitmq:
        condition: service_healthy
    restart: always
    healthcheck:
      test: ["CMD", "curl", "-f", "http://localhost:8080/actuator/health"]
      interval: 30s
      timeout: 10s
      retries: 5

  # Prerender 服务
  prerender:
    build:
      context: ./docker/prerender
    container_name: vaadin-prerender
    restart: always
    healthcheck:
      test: ["CMD", "curl", "-f", "http://localhost:3000/status"]
      interval: 30s
      timeout: 10s
      retries: 5

  # Nginx 反向代理
  nginx:
    image: nginx:alpine
    container_name: vaadin-nginx
    volumes:
      - ./docker/nginx/default.conf:/etc/nginx/conf.d/default.conf
    ports:
      - "80:80"
      - "443:443"
    depends_on:
      vaadin:
        condition: service_healthy
      prerender:
        condition: service_healthy
    restart: always

volumes:
  postgres_data:
  rabbitmq_data:
```

## 5️⃣ 构建和启动服务

### 构建应用
```bash
# 构建 Vaadin 应用
mvn clean package -DskipTests

# 启动所有服务
docker-compose -f docker-compose-prerender.yml up -d --build
```

### 查看服务状态
```bash
# 查看所有容器状态
docker-compose -f docker-compose-prerender.yml ps

# 查看日志
docker-compose -f docker-compose-prerender.yml logs -f vaadin
docker-compose -f docker-compose-prerender.yml logs -f prerender
docker-compose -f docker-compose-prerender.yml logs -f nginx
```

## 6️⃣ 验证爬虫可见性

### 测试爬虫访问
```bash
# 模拟 Google 爬虫
curl -A "Googlebot/2.1 (+http://www.google.com/bot.html)" http://yourdomain.com

# 模拟 Facebook 爬虫
curl -A "facebookexternalhit/1.1 (+http://www.facebook.com/externalhit_uatext.php)" http://yourdomain.com

# 使用 _escaped_fragment_ 参数手动测试
curl "http://yourdomain.com?_escaped_fragment_="
```

你应该看到的是完整的 HTML 内容，而不是 `<div id="outlet"></div>`。

### 测试正常用户访问
```bash
# 模拟正常浏览器访问
curl -A "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36" http://yourdomain.com
```

## 7️⃣ 性能优化配置

### 在 `application.properties` 中添加生产环境配置：

```properties
# 生产环境配置
spring.profiles.active=prod

# 数据库连接池优化
spring.datasource.hikari.maximum-pool-size=20
spring.datasource.hikari.minimum-idle=5
spring.datasource.hikari.connection-timeout=30000

# Vaadin 生产模式
vaadin.productionMode=true
vaadin.frontend.hotdeploy=false

# 日志级别
logging.level.root=WARN
logging.level.cc.fss.vaadin=INFO

# 健康检查端点
management.endpoints.web.exposure.include=health,info
management.endpoint.health.show-details=when-authorized
```

## ✅ FAQ

### Q: 页面渲染不完整？
在 Vaadin 应用中设置：
```java
// 在需要预渲染的页面中添加
UI.getCurrent().getPage().executeJs("window.prerenderReady = true;");
```

### Q: 如何处理动态内容？
对于需要 AJAX 加载的内容，确保在页面加载完成后设置：
```javascript
// 在动态内容加载完成后
window.prerenderReady = true;
```

### Q: 是否支持 HTTPS？
使用 nginx + Let's Encrypt 或 Cloudflare 实现：
```bash
# 使用 certbot 获取 SSL 证书
docker run -it --rm -v /etc/letsencrypt:/etc/letsencrypt -v /var/lib/letsencrypt:/var/lib/letsencrypt certbot/certbot certonly --standalone -d yourdomain.com
```

### Q: 如何处理认证页面？
对于需要登录的页面，可以配置 prerender 忽略：
```nginx
# 在 nginx 配置中添加
if ($request_uri ~* "/login|/admin") {
    set $prerender 0;
}
```

## ✅ 监控和维护

### 健康检查
```bash
# 检查所有服务健康状态
curl http://yourdomain.com/health
curl http://yourdomain.com:3000/status
```

### 日志监控
```bash
# 实时查看 prerender 日志
docker-compose -f docker-compose-prerender.yml logs -f prerender

# 查看 nginx 访问日志
docker exec vaadin-nginx tail -f /var/log/nginx/access.log
```

## ✅ 总结

| 目标 | 已完成 |
|------|--------|
| 使用 Puppeteer 预渲染 Vaadin 24.8.4 页面 | ✅ |
| 自动识别爬虫并返回 HTML 快照 | ✅ |
| 不影响正常用户访问 | ✅ |
| 集成 PostgreSQL + RabbitMQ | ✅ |
| 使用 Docker 一键部署 | ✅ |
| 支持健康检查和监控 | ✅ |
| 生产环境优化配置 | ✅ |
