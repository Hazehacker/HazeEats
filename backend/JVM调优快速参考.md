# JVM调优快速参考

## 一、内存分配方案（2C4G服务器）

### 推荐配置
```
总内存: 4GB
- 系统预留: 1GB
- Nacos: 512MB
- Gateway: 384MB
- 其他7个服务: 每个192-256MB
```

### 各服务JVM参数速查

#### Gateway (384MB)
```bash
-Xms384m -Xmx384m -XX:MetaspaceSize=128m -XX:MaxMetaspaceSize=128m
```

#### 标准服务 (256MB)
```bash
-Xms256m -Xmx256m -XX:MetaspaceSize=128m -XX:MaxMetaspaceSize=128m
```

#### 轻量级服务 (192MB)
```bash
-Xms192m -Xmx192m -XX:MetaspaceSize=96m -XX:MaxMetaspaceSize=96m
```

## 二、常用命令

### 查看Java进程
```bash
# Windows
Get-Process java

# Linux
ps aux | grep java
pgrep -f "java.*jar"
```

### 查看JVM内存
```bash
# 查看堆内存
jmap -heap <pid>

# 查看GC统计
jstat -gc <pid> 1000

# 查看对象统计
jmap -histo <pid> | head -20
```

### 生成堆转储
```bash
# 手动生成
jmap -dump:format=b,file=heapdump.hprof <pid>

# 分析堆转储
jhat heapdump.hprof
# 或使用 jvisualvm
```

### 查看线程
```bash
jstack <pid> > thread_dump.txt
```

## 三、OOM排查步骤

1. **检查日志**
   ```bash
   grep -i "OutOfMemoryError" logs/**/*.log
   ```

2. **查看堆转储**
   - 位置: `logs/*/heapdump.hprof`
   - 分析: `jhat` 或 `jvisualvm`

3. **检查GC日志**
   ```bash
   tail -f logs/*/gc.log | grep "Full GC"
   ```

4. **实时监控**
   ```bash
   # 使用监控脚本
   bash scripts/monitor-memory.sh
   # 或
   .\scripts\monitor-memory.ps1
   ```

## 四、紧急处理

### 服务频繁重启
1. 检查OOM: `bash scripts/check-oom.sh`
2. 临时增加内存: 修改启动脚本中的 `-Xmx` 参数
3. 重启服务: 使用启动脚本重启

### 内存不足
1. 清理日志: `find logs/ -name "*.log" -mtime +7 -delete`
2. 重启Nacos: 释放内存
3. 降级服务: 临时关闭非核心服务

## 五、性能优化检查清单

- [ ] 使用G1GC (`-XX:+UseG1GC`)
- [ ] 设置合理的堆内存大小
- [ ] 启用OOM自动转储 (`-XX:+HeapDumpOnOutOfMemoryError`)
- [ ] 配置GC日志 (`-Xlog:gc*`)
- [ ] 关闭开发环境功能（Swagger、调试日志）
- [ ] 优化数据库查询（避免N+1）
- [ ] 使用Redis缓存热点数据
- [ ] 设置合理的连接池大小

## 六、监控指标

### 关键指标
- **堆内存使用率**: < 80%
- **Full GC频率**: < 1次/小时
- **GC暂停时间**: < 200ms
- **线程数**: < 200

### 告警阈值
- 内存使用 > 80%: 警告
- 内存使用 > 90%: 严重警告
- Full GC > 5次/小时: 警告
- GC暂停 > 500ms: 警告

## 七、启动服务

### Windows
```powershell
# 启动单个服务
.\scripts\start-user-service.ps1

# 启动所有服务
.\scripts\start-all-services.ps1

# 监控内存
.\scripts\monitor-memory.ps1

# 检查OOM
.\scripts\check-oom.ps1
```

### Linux
```bash
# 启动单个服务
bash scripts/start-user-service.sh

# 启动所有服务（后台）
bash scripts/start-all-services.sh

# 监控内存
bash scripts/monitor-memory.sh

# 检查OOM
bash scripts/check-oom.sh
```

## 八、常见问题

### Q: 服务启动失败，提示内存不足
**A**: 检查系统内存，可能需要：
- 减少某个服务的内存配置
- 关闭其他占用内存的程序
- 升级服务器配置

### Q: 服务运行一段时间后OOM
**A**: 
1. 检查是否有内存泄漏（使用jmap分析）
2. 增加堆内存大小
3. 优化代码，减少对象创建

### Q: Full GC频繁
**A**:
1. 检查堆内存是否过小
2. 检查是否有大对象
3. 调整G1GC参数（`-XX:InitiatingHeapOccupancyPercent`）

### Q: 如何确定合适的内存大小？
**A**:
1. 启动服务后，使用 `jmap -heap <pid>` 查看实际使用
2. 观察GC日志，确保Full GC不频繁
3. 预留20-30%的缓冲空间

