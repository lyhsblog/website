#!/bin/bash

# Vaadin 预渲染服务一键部署脚本
# 作者: cc.fss.vaadin.website
# 版本: 1.0

set -e

echo "🚀 开始部署 Vaadin 预渲染服务..."

# 检查 Docker 是否安装
if ! command -v docker &> /dev/null; then
    echo "❌ Docker 未安装，请先安装 Docker"
    exit 1
fi

# 检查 Docker Compose 是否安装
if ! command -v docker-compose &> /dev/null; then
    echo "❌ Docker Compose 未安装，请先安装 Docker Compose"
    exit 1
fi

# 检查 Maven 是否安装
if ! command -v mvn &> /dev/null; then
    echo "❌ Maven 未安装，请先安装 Maven"
    exit 1
fi

echo "✅ 环境检查通过"

# 构建 Vaadin 应用
echo "📦 构建 Vaadin 应用..."
mvn clean package -DskipTests

if [ ! -f "target/website-1.0-SNAPSHOT.jar" ]; then
    echo "❌ 构建失败，未找到 JAR 文件"
    exit 1
fi

echo "✅ Vaadin 应用构建成功"

# 创建必要的目录
echo "📁 创建必要的目录..."
mkdir -p docker/prerender
mkdir -p docker/nginx

# 检查配置文件是否存在
if [ ! -f "docker/prerender/Dockerfile" ]; then
    echo "❌ 缺少 docker/prerender/Dockerfile"
    exit 1
fi

if [ ! -f "docker/nginx/default.conf" ]; then
    echo "❌ 缺少 docker/nginx/default.conf"
    exit 1
fi

if [ ! -f "docker-compose-prerender.yml" ]; then
    echo "❌ 缺少 docker-compose-prerender.yml"
    exit 1
fi

echo "✅ 配置文件检查通过"

# 停止现有服务
echo "🛑 停止现有服务..."
docker-compose -f docker-compose-prerender.yml down --remove-orphans 2>/dev/null || true

# 启动服务
echo "🚀 启动预渲染服务..."
docker-compose -f docker-compose-prerender.yml up -d --build

# 等待服务启动
echo "⏳ 等待服务启动..."
sleep 30

# 检查服务状态
echo "🔍 检查服务状态..."
docker-compose -f docker-compose-prerender.yml ps

# 检查健康状态
echo "🏥 检查健康状态..."
for i in {1..10}; do
    if curl -f http://localhost/health >/dev/null 2>&1; then
        echo "✅ 服务健康检查通过"
        break
    else
        echo "⏳ 等待服务启动... ($i/10)"
        sleep 10
    fi
done

# 测试预渲染功能
echo "🧪 测试预渲染功能..."
echo "测试 Google 爬虫访问:"
curl -s -A "Googlebot/2.1 (+http://www.google.com/bot.html)" http://localhost | head -20

echo ""
echo "测试正常用户访问:"
curl -s -A "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36" http://localhost | head -20

echo ""
echo "🎉 部署完成！"
echo ""
echo "📋 服务信息:"
echo "  - Vaadin 应用: http://localhost"
echo "  - 健康检查: http://localhost/health"
echo "  - RabbitMQ 管理: http://localhost:15672 (guest/guest)"
echo "  - PostgreSQL: localhost:5432"
echo ""
echo "🔧 常用命令:"
echo "  - 查看日志: docker-compose -f docker-compose-prerender.yml logs -f"
echo "  - 停止服务: docker-compose -f docker-compose-prerender.yml down"
echo "  - 重启服务: docker-compose -f docker-compose-prerender.yml restart"
echo ""
echo "📖 更多信息请查看: prerender-vaadin.md"
