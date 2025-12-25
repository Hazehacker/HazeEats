# JVM调优指南 - 2C4G服务器运行8个微服务+Nacos

## 一、资源分配策略

### 1.1 总体内存分配（4GB）
- **系统预留**: 1GB（操作系统、其他进程）
- **Nacos**: 512MB
- **Gateway**: 512MB（网关需要更多内存处理请求）
- **其他7个微服务**: 每个约300MB，共2.1GB
- **总计**: 约4GB

### 1.2 各服务内存配置建议

| 服务名称 | 堆内存 | 元空间 | 直接内存 | 说明 |
|---------|--------|--------|----------|------|
| eats-gateway | 384MB | 128MB | 64MB | 网关服务，需要处理大量请求 |
| eats-server | 256MB | 128MB | 64MB | 主服务 |
| user-service | 256MB | 128MB | 64MB | 用户服务 |
| order-service | 256MB | 128MB | 64MB | 订单服务 |
| cart-service | 192MB | 96MB | 48MB | 购物车服务（相对简单） |
| dish-service | 192MB | 96MB | 48MB | 菜品服务 |
| shop-service | 192MB | 96MB | 48MB | 店铺服务 |
| pay-service | 192MB | 96MB | 48MB | 支付服务 |
| report-service | 192MB | 96MB | 48MB | 报表服务 |

**注意**: 实际运行时可根据服务负载情况动态调整。

## 二、JVM参数配置

### 2.1 核心JVM参数说明

#### 堆内存设置
- `-Xms`: 初始堆内存（建议与-Xmx相同，避免动态扩容）
- `-Xmx`: 最大堆内存
- `-XX:MetaspaceSize`: 元空间初始大小
- `-XX:MaxMetaspaceSize`: 元空间最大大小

#### GC选择（Java 17推荐使用G1GC）
- `-XX:+UseG1GC`: 启用G1垃圾收集器
- `-XX:MaxGCPauseMillis=200`: 最大GC暂停时间200ms
- `-XX:G1HeapRegionSize=4m`: G1堆区域大小（小堆用4MB）
- `-XX:InitiatingHeapOccupancyPercent=45`: 触发并发GC的堆占用率

#### 其他优化参数
- `-XX:+HeapDumpOnOutOfMemoryError`: OOM时自动生成堆转储
- `-XX:HeapDumpPath`: 堆转储文件路径
- `-XX:+PrintGCDetails`: 打印GC详细信息
- `-XX:+PrintGCDateStamps`: 打印GC时间戳
- `-Xlog:gc*:file=gc.log:time,tags,level`: Java 17的GC日志格式
- `-XX:+UseStringDeduplication`: 字符串去重（G1GC特性）
- `-XX:+DisableExplicitGC`: 禁用显式GC调用

### 2.2 标准启动参数模板（适用于大多数微服务）

```bash
java -Xms256m -Xmx256m \
     -XX:MetaspaceSize=128m -XX:MaxMetaspaceSize=128m \
     -XX:MaxDirectMemorySize=64m \
     -XX:+UseG1GC \
     -XX:MaxGCPauseMillis=200 \
     -XX:G1HeapRegionSize=4m \
     -XX:InitiatingHeapOccupancyPercent=45 \
     -XX:+HeapDumpOnOutOfMemoryError \
     -XX:HeapDumpPath=./logs/heapdump.hprof \
     -Xlog:gc*:file=./logs/gc.log:time,tags,level:filecount=5,filesize=10M \
     -XX:+UseStringDeduplication \
     -XX:+DisableExplicitGC \
     -Dspring.profiles.active=prod \
     -jar your-service.jar
```

## 三、针对不同服务的具体配置

### 3.1 Gateway服务（需要更多内存）
```bash
-Xms384m -Xmx384m \
-XX:MetaspaceSize=128m -XX:MaxMetaspaceSize=128m \
-XX:MaxDirectMemorySize=64m
```

### 3.2 轻量级服务（cart-service, shop-service等）
```bash
-Xms192m -Xmx192m \
-XX:MetaspaceSize=96m -XX:MaxMetaspaceSize=96m \
-XX:MaxDirectMemorySize=48m
```

### 3.3 Nacos配置
```bash
-Xms512m -Xmx512m \
-XX:MetaspaceSize=128m -XX:MaxMetaspaceSize=256m \
-XX:+UseG1GC \
-XX:MaxGCPauseMillis=200
```

## 四、OOM问题排查

### 4.1 常见OOM原因
1. **堆内存溢出**: 对象过多，无法回收
2. **元空间溢出**: 类加载过多
3. **直接内存溢出**: Netty等NIO框架使用直接内存
4. **线程栈溢出**: 线程过多或递归过深

### 4.2 排查步骤

#### 步骤1: 查看GC日志
```bash
# 查看GC日志，关注Full GC频率
tail -f logs/gc.log | grep "Full GC"
```

#### 步骤2: 分析堆转储文件
```bash
# 使用jhat或jvisualvm分析heapdump.hprof
jhat heapdump.hprof
# 或使用jvisualvm打开heapdump.hprof文件
```

#### 步骤3: 实时监控JVM
```bash
# 查看JVM内存使用情况
jstat -gc <pid> 1000

# 查看堆内存详情
jmap -heap <pid>

# 查看对象统计
jmap -histo <pid> | head -20
```

#### 步骤4: 检查线程
```bash
# 查看线程栈
jstack <pid> > thread_dump.txt
```

## 五、性能优化建议

### 5.1 应用层面
1. **减少不必要的对象创建**: 使用对象池、缓存
2. **优化数据库查询**: 避免N+1查询，使用分页
3. **合理使用缓存**: Redis缓存热点数据
4. **异步处理**: 非关键路径使用异步处理
5. **关闭不必要的功能**: 如开发环境的Swagger、调试日志

### 5.2 系统层面
1. **使用Docker限制资源**: 为每个容器设置内存限制
2. **监控告警**: 设置内存使用率告警（>80%）
3. **定期重启**: 在低峰期重启服务，清理内存碎片
4. **升级服务器**: 如果可能，升级到4C8G或更高配置

### 5.3 Spring Boot优化
```yaml
# application.yml
spring:
  jpa:
    open-in-view: false  # 关闭Open Session in View
  jackson:
    default-property-inclusion: non_null  # 不序列化null值
  servlet:
    multipart:
      max-file-size: 10MB  # 限制文件上传大小
      max-request-size: 10MB
```

## 六、监控脚本

### 6.1 内存监控脚本
见 `monitor-memory.sh` 或 `monitor-memory.ps1`

### 6.2 快速重启脚本
见 `restart-services.sh` 或 `restart-services.ps1`

## 七、应急处理

### 7.1 服务频繁重启
1. 检查系统日志: `journalctl -u your-service` 或查看应用日志
2. 检查OOM日志: `grep -i "outofmemory" logs/*.log`
3. 临时增加内存: 修改启动脚本中的 `-Xmx` 参数
4. 降级非核心服务: 临时关闭部分服务

### 7.2 内存不足
1. 清理日志文件: `find logs/ -name "*.log" -mtime +7 -delete`
2. 清理GC日志: `find logs/ -name "gc.log*" -mtime +3 -delete`
3. 重启Nacos: 释放内存
4. 考虑服务合并: 将轻量级服务合并部署

## 八、长期建议

1. **服务拆分优化**: 考虑将部分服务合并，减少服务数量
2. **使用Kubernetes**: 更好的资源管理和自动扩缩容
3. **升级服务器配置**: 至少4C8G，推荐8C16G
4. **使用云原生方案**: 考虑Serverless或容器化部署
5. **引入APM工具**: 如SkyWalking、Pinpoint等，实时监控性能

