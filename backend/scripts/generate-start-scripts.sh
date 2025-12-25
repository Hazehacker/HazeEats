#!/bin/bash
# 生成所有服务的Linux启动脚本

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

# 服务配置: 脚本名:服务名:JAR路径:堆内存:元空间:直接内存
SERVICES=(
    "start-gateway.sh:eats-gateway:./eats-gateway/target/eats-gateway-1.0.1-SNAPSHOT.jar:384:128:64"
    "start-server.sh:eats-server:./eats-server/target/eats-server-1.0.1-SNAPSHOT.jar:256:128:64"
    "start-user-service.sh:user-service:./user-service/target/user-service-1.0.1-SNAPSHOT.jar:256:128:64"
    "start-cart-service.sh:cart-service:./cart-service/target/cart-service-1.0.1-SNAPSHOT.jar:192:96:48"
    "start-order-service.sh:order-service:./order-service/target/order-service-1.0.1-SNAPSHOT.jar:256:128:64"
    "start-dish-service.sh:dish-service:./dish-service/target/dish-service-1.0.1-SNAPSHOT.jar:192:96:48"
    "start-shop-service.sh:shop-service:./shop-service/target/shop-service-1.0.1-SNAPSHOT.jar:192:96:48"
    "start-pay-service.sh:pay-service:./pay-service/target/pay-service-1.0.1-SNAPSHOT.jar:192:96:48"
    "start-report-service.sh:report-service:./report-service/target/report-service-1.0.1-SNAPSHOT.jar:192:96:48"
)

TEMPLATE='#!/bin/bash
# {SERVICE_NAME} 启动脚本 (Linux)
# 内存配置: {HEAP_SIZE}MB堆内存

JAR_PATH="{JAR_PATH}"
LOG_DIR="./logs/{SERVICE_NAME}"
SERVICE_NAME="{SERVICE_NAME}"

# 创建日志目录
mkdir -p $LOG_DIR

echo "Starting $SERVICE_NAME..."

java -Xms{HEAP_SIZE}m -Xmx{HEAP_SIZE}m \
     -XX:MetaspaceSize={META_SIZE}m -XX:MaxMetaspaceSize={META_SIZE}m \
     -XX:MaxDirectMemorySize={DIRECT_SIZE}m \
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
'

for service_info in "${SERVICES[@]}"; do
    IFS=':' read -r script_name service_name jar_path heap_size meta_size direct_size <<< "$service_info"
    script_path="$SCRIPT_DIR/$script_name"
    
    # 如果脚本已存在，跳过
    if [ -f "$script_path" ]; then
        echo "跳过已存在的脚本: $script_name"
        continue
    fi
    
    # 生成脚本内容
    content=$(echo "$TEMPLATE" | \
        sed "s/{SERVICE_NAME}/$service_name/g" | \
        sed "s/{JAR_PATH}/$jar_path/g" | \
        sed "s/{HEAP_SIZE}/$heap_size/g" | \
        sed "s/{META_SIZE}/$meta_size/g" | \
        sed "s/{DIRECT_SIZE}/$direct_size/g")
    
    echo "$content" > "$script_path"
    chmod +x "$script_path"
    echo "生成脚本: $script_name"
done

echo ""
echo "所有启动脚本生成完成！"

