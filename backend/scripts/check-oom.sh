#!/bin/bash
# OOM问题排查脚本 (Linux)
# 检查日志中的OOM错误和堆转储文件

echo "========================================"
echo "OOM问题排查"
echo "========================================"
echo ""

LOG_BASE_DIR="./logs"

echo "1. 检查OOM错误日志..."
oom_found=false
find "$LOG_BASE_DIR" -name "*.log" -type f | while read -r logfile; do
    if grep -i "OutOfMemoryError\|OOM\|java.lang.OutOfMemoryError" "$logfile" > /dev/null 2>&1; then
        echo "发现OOM错误: $logfile"
        oom_found=true
        grep -i "OutOfMemoryError\|OOM" "$logfile" -A 2 -B 2 | tail -10
    fi
done

if [ "$oom_found" = false ]; then
    echo "未发现OOM错误"
fi

echo ""
echo "2. 检查堆转储文件..."
heapdumps=$(find "$LOG_BASE_DIR" -name "heapdump.hprof" -type f 2>/dev/null)
if [ -n "$heapdumps" ]; then
    echo "发现堆转储文件:"
    echo "$heapdumps" | while read -r dump; do
        size_mb=$(du -m "$dump" | cut -f1)
        mod_time=$(stat -c %y "$dump" 2>/dev/null || stat -f %Sm "$dump" 2>/dev/null)
        echo "  $dump - ${size_mb} MB - 修改时间: $mod_time"
    done
    echo ""
    echo "分析堆转储文件:"
    first_dump=$(echo "$heapdumps" | head -1)
    echo "  jhat $first_dump"
    echo "  或使用 jvisualvm 打开文件"
else
    echo "未发现堆转储文件"
fi

echo ""
echo "3. 检查GC日志..."
gc_logs=$(find "$LOG_BASE_DIR" -name "gc.log*" -type f 2>/dev/null)
if [ -n "$gc_logs" ]; then
    echo "发现GC日志文件:"
    echo "$gc_logs" | while read -r gc_log; do
        size_mb=$(du -m "$gc_log" | cut -f1)
        echo "  $gc_log - ${size_mb} MB"
        
        # 检查Full GC频率
        full_gc_count=$(tail -1000 "$gc_log" 2>/dev/null | grep -c "Full GC\|Pause Full" || echo "0")
        if [ "$full_gc_count" -gt 0 ]; then
            if [ "$full_gc_count" -gt 10 ]; then
                echo "    最近1000行中发现 $full_gc_count 次Full GC (警告: 频率过高!)"
            else
                echo "    最近1000行中发现 $full_gc_count 次Full GC"
            fi
        fi
    done
else
    echo "未发现GC日志文件"
fi

echo ""
echo "4. 当前Java进程内存使用..."
java_pids=$(pgrep -f "java.*jar")
if [ -n "$java_pids" ]; then
    for pid in $java_pids; do
        memory_kb=$(ps -p $pid -o rss= 2>/dev/null)
        if [ -n "$memory_kb" ]; then
            memory_mb=$(echo "scale=2; $memory_kb / 1024" | bc)
            process_name=$(ps -p $pid -o comm= 2>/dev/null)
            if (( $(echo "$memory_mb > 500" | bc -l) )); then
                echo "  PID $pid ($process_name): ${memory_mb} MB (警告: 内存使用过高!)"
            elif (( $(echo "$memory_mb > 300" | bc -l) )); then
                echo "  PID $pid ($process_name): ${memory_mb} MB (注意: 内存使用较高)"
            else
                echo "  PID $pid ($process_name): ${memory_mb} MB"
            fi
        fi
    done
fi

echo ""
echo "排查完成！"

