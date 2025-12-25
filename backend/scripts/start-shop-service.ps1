# Shop Service 启动脚本
# 内存配置: 192MB堆内存（轻量级服务）

$JAR_PATH = ".\shop-service\target\shop-service-1.0.1-SNAPSHOT.jar"
$LOG_DIR = ".\logs\shop-service"
$SERVICE_NAME = "shop-service"

# 创建日志目录
if (-not (Test-Path $LOG_DIR)) {
    New-Item -ItemType Directory -Path $LOG_DIR -Force | Out-Null
}

Write-Host "Starting $SERVICE_NAME..." -ForegroundColor Green

java -Xms192m -Xmx192m `
     -XX:MetaspaceSize=96m -XX:MaxMetaspaceSize=96m `
     -XX:MaxDirectMemorySize=48m `
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

