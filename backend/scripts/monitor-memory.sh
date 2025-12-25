#!/bin/bash
# 内存监控脚本 (Linux)
# 用于监控所有Java进程的内存使用情况

echo "========================================"
echo "JVM内存监控"
echo "========================================"
echo ""

# 获取所有Java进程
java_pids=$(pgrep -f "java.*jar")

if [ -z "$java_pids" ]; then
    echo "未找到运行中的Java进程"
    exit 0
fi

echo "进程ID | 进程名 | 内存使用(MB) | CPU(%) | 启动时间"
echo "------------------------------------------------------------"

total_memory=0
for pid in $java_pids; do
    if [ -d "/proc/$pid" ]; then
        # 获取进程信息
        process_name=$(ps -p $pid -o comm= 2>/dev/null)
        memory_kb=$(ps -p $pid -o rss= 2>/dev/null)
        memory_mb=$(echo "scale=2; $memory_kb / 1024" | bc)
        cpu=$(ps -p $pid -o %cpu= 2>/dev/null | xargs)
        start_time=$(ps -p $pid -o lstart= 2>/dev/null | xargs)
        
        printf "%-8s | %-20s | %12.2f | %6s | %s\n" \
            "$pid" "$process_name" "$memory_mb" "$cpu" "$start_time"
        
        total_memory=$(echo "$total_memory + $memory_mb" | bc)
    fi
done

echo "------------------------------------------------------------"
total_memory_gb=$(echo "scale=2; $total_memory / 1024" | bc)
echo "总内存使用: ${total_memory} MB (${total_memory_gb} GB)"

# 获取系统内存信息
if command -v free &> /dev/null; then
    echo ""
    echo "系统内存信息:"
    free -h
fi

echo ""
echo "提示: 使用 'jstat -gc <pid> 1000' 查看详细GC信息"
echo "提示: 使用 'jmap -heap <pid>' 查看堆内存详情"

