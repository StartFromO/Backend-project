#!/bin/bash

# 启动后端服务
echo "正在启动商城后端服务..."
cd backend
mvn clean install -DskipTests
mvn spring-boot:run

docker run \
    --name docker-demo \
    -p 8080:8080 \
    -d \
    docker-demo:1.0