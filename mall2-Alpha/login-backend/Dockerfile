FROM openjdk:17-jdk-alpine

# 设置工作目录
WORKDIR /app

# 复制构建产物
COPY target/login-backend-1.0-SNAPSHOT.jar app.jar

# 暴露端口
EXPOSE 8080

# 启动应用
ENTRYPOINT ["java", "-jar", "app.jar"]