# 构建阶段
FROM maven:3.9-eclipse-temurin-17 AS builder

WORKDIR /app

# 复制 Maven 配置（使用阿里云镜像加速）
COPY settings.xml /root/.m2/settings.xml

# 复制 pom 和源码
COPY pom.xml .
COPY src ./src

# 构建（跳过测试）
RUN mvn clean package -DskipTests -B

# 运行阶段 (使用支持多架构的镜像，兼容 ARM64/AMD64)
FROM eclipse-temurin:17-jre

WORKDIR /app

# 安装字体支持（中文）- Debian 系统
RUN apt-get update && apt-get install -y --no-install-recommends \
    fontconfig fonts-dejavu-core \
    && rm -rf /var/lib/apt/lists/*

# 复制构建产物
COPY --from=builder /app/target/*.jar app.jar

# 创建日志目录
RUN mkdir -p /app/logs

EXPOSE 8000

# JVM 参数优化
ENV JAVA_OPTS="-Xms256m -Xmx512m -XX:+UseG1GC -Djava.security.egd=file:/dev/./urandom"
ENV SPRING_PROFILES_ACTIVE=docker

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -Dspring.profiles.active=${SPRING_PROFILES_ACTIVE} -jar app.jar"]
