# 后端 Dockerfile
FROM openjdk:21-jdk-slim

WORKDIR /app

# 复制 Maven 构建的 jar 文件
COPY target/mall-backend-1.0.0.jar app.jar

# 暴露端口
EXPOSE 8080

# 启动命令
ENTRYPOINT ["java", "-jar", "app.jar", "--spring.profiles.active=prod"]
