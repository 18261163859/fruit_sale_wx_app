# 构建阶段
FROM node:20-alpine AS builder

WORKDIR /app

# 设置 npm 镜像源（淘宝镜像）
RUN npm config set registry https://registry.npmmirror.com

# 复制 package 文件
COPY package*.json ./

# 安装依赖
RUN npm install

# 复制源码
COPY . .

# 构建生产版本（跳过 TypeScript 类型检查，直接用 Vite 构建）
RUN npx vite build

# 运行阶段
FROM nginx:alpine

# 复制构建产物
COPY --from=builder /app/dist /usr/share/nginx/html

# 复制 nginx 配置
COPY nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]
