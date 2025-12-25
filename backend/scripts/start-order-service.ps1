# Order Service 启动脚本
# 内存配置: 256MB堆内存

$JAR_PATH = ".\order-service\target\order-service-1.0.1-SNAPSHOT.jar"
$LOG_DIR = ".\logs\order-service"
$SERVICE_NAME = "order-service"

# 创建日志目录
if (-not (Test-Path $LOG_DIR)) {
    New-Item -ItemType Directory -Path $LOG_DIR -Force | Out-Null
}

Write-Host "Starting $SERVICE_NAME..." -ForegroundColor Green

java -Xms256m -Xmx256m `
     -XX:MetaspaceSize=128m -XX:MaxMetaspaceSize=128m `
     -XX:MaxDirectMemorySize=64m `
     -XX:+UseG1GC `
     -XX:MaxGCPauseMillis=200 `
     -XX:G1HeapRegionSize=4m `
     -XX:InitiatingHeapOccupancyPercent=45 `
     -XX:+HeapDumpOnOutOfMemoryError `
     -XX:HeapDumpPath="$LOG_DIR\heapdump.hprof" `
     -Xlog:gc*:file="$LOG_DIR\gc.log":time,tags,level:filecount=5,filesize=10M `
     -XX:+UseStringDeduplication `
     -XX:+DisableExplicitGC `
     -Dspring.profiles.active=prod `
     -jar $JAR_PATH

