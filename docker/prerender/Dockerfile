FROM node:20-alpine

WORKDIR /app

# 安装 prerender 和必要的依赖
RUN npm install -g prerender

# 安装 curl 用于健康检查
RUN apk add --no-cache curl

# 创建非 root 用户
RUN addgroup -g 1001 -S nodejs && \
    adduser -S prerender -u 1001

USER prerender

EXPOSE 3000

CMD ["prerender"]
