#!/bin/bash
# User Service 启动脚本 (Linux)
# 内存配置: 256MB堆内存

JAR_PATH="./user-service/target/user-service-1.0.1-SNAPSHOT.jar"
LOG_DIR="./logs/user-service"
SERVICE_NAME="user-service"

# 创建日志目录
mkdir -p $LOG_DIR

echo "Starting $SERVICE_NAME..."

java -Xms256m -Xmx256m \
     -XX:MetaspaceSize=128m -XX:MaxMetaspaceSize=128m \
     -XX:MaxDirectMemorySize=64m \
     -XX:+UseG1GC \
     -XX:MaxGCPauseMillis=200 \
     -XX:G1HeapRegionSize=4m \
     -XX:InitiatingHeapOccupancyPercent=45 \
     -XX:+HeapDumpOnOutOfMemoryError \
     -XX:HeapDumpPath=$LOG_DIR/heapdump.hprof \
     -Xlog:gc*:file=$LOG_DIR/gc.log:time,tags,level:filecount=5,filesize=10M \
     -XX:+UseStringDeduplication \
     -XX:+DisableExplicitGC \
     -Dspring.profiles.active=prod \
     -jar $JAR_PATH

