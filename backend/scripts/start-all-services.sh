#!/bin/bash
# 启动所有微服务的脚本 (Linux)
# 使用nohup在后台运行

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR/.."

echo "========================================"
echo "启动所有微服务"
echo "========================================"
echo ""

# 启动服务列表
SERVICES=(
    "start-gateway.sh:eats-gateway:8080"
    "start-server.sh:eats-server:8080"
    "start-user-service.sh:user-service:8082"
    "start-cart-service.sh:cart-service:8081"
    "start-order-service.sh:order-service:8083"
    "start-dish-service.sh:dish-service:8084"
    "start-shop-service.sh:shop-service:8085"
    "start-pay-service.sh:pay-service:8086"
    "start-report-service.sh:report-service:8087"
)

for service_info in "${SERVICES[@]}"; do
    IFS=':' read -r script service_name port <<< "$service_info"
    script_path="$SCRIPT_DIR/$script"
    
    if [ -f "$script_path" ]; then
        echo "启动服务: $service_name (端口: $port)"
        chmod +x "$script_path"
        nohup bash "$script_path" > "$SCRIPT_DIR/../logs/$service_name/startup.log" 2>&1 &
        sleep 3
    else
        echo "警告: 找不到启动脚本 $script"
    fi
done

echo ""
echo "所有服务启动完成！"
echo "提示: 使用 'ps aux | grep java' 查看运行中的服务"
echo "提示: 使用 'bash scripts/monitor-memory.sh' 监控内存使用情况"

