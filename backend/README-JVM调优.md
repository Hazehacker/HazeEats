# JVM调优方案说明

## 📋 概述

针对2C4G云服务器运行8个微服务+Nacos的资源紧张问题，提供了完整的JVM调优方案。

## 📁 文件说明

### 文档
- `JVM调优指南.md` - 详细的JVM调优文档，包含理论、配置、排查方法
- `JVM调优快速参考.md` - 快速参考手册，常用命令和配置速查

### 启动脚本 (Windows PowerShell)
- `scripts/start-*-service.ps1` - 各服务的Windows启动脚本
- `scripts/start-all-services.ps1` - 一键启动所有服务
- `scripts/monitor-memory.ps1` - 内存监控脚本
- `scripts/check-oom.ps1` - OOM问题排查脚本

### 启动脚本 (Linux Bash)
- `scripts/start-*-service.sh` - 各服务的Linux启动脚本
- `scripts/start-all-services.sh` - 一键启动所有服务（后台运行）
- `scripts/monitor-memory.sh` - 内存监控脚本
- `scripts/check-oom.sh` - OOM问题排查脚本
- `scripts/generate-start-scripts.sh` - 自动生成Linux启动脚本

## 🚀 快速开始

### Windows环境

1. **启动单个服务**
   ```powershell
   .\scripts\start-user-service.ps1
   ```

2. **启动所有服务**
   ```powershell
   .\scripts\start-all-services.ps1
   ```

3. **监控内存使用**
   ```powershell
   .\scripts\monitor-memory.ps1
   ```

4. **检查OOM问题**
   ```powershell
   .\scripts\check-oom.ps1
   ```

### Linux环境

1. **生成启动脚本**（如果还没有）
   ```bash
   chmod +x scripts/generate-start-scripts.sh
   bash scripts/generate-start-scripts.sh
   ```

2. **启动单个服务**
   ```bash
   bash scripts/start-user-service.sh
   ```

3. **启动所有服务**（后台运行）
   ```bash
   bash scripts/start-all-services.sh
   ```

4. **监控内存使用**
   ```bash
   bash scripts/monitor-memory.sh
   ```

5. **检查OOM问题**
   ```bash
   bash scripts/check-oom.sh
   ```

## 💡 核心优化策略

### 内存分配（总计4GB）

| 组件 | 内存分配 | 说明 |
|------|---------|------|
| 系统预留 | 1GB | 操作系统和其他进程 |
| Nacos | 512MB | 服务注册中心 |
| Gateway | 384MB | 网关服务，需要更多内存 |
| 其他7个服务 | 192-256MB/个 | 根据服务复杂度分配 |

### JVM参数要点

1. **使用G1GC**: 适合低延迟场景，Java 17推荐
2. **合理设置堆内存**: 避免过大导致OOM，过小导致频繁GC
3. **启用OOM自动转储**: 方便问题排查
4. **配置GC日志**: 用于性能分析和问题定位

## 🔍 问题排查

### 服务频繁重启

1. **检查OOM日志**
   ```bash
   # Windows
   .\scripts\check-oom.ps1
   
   # Linux
   bash scripts/check-oom.sh
   ```

2. **查看GC日志**
   ```bash
   tail -f logs/*/gc.log | grep "Full GC"
   ```

3. **实时监控内存**
   ```bash
   # Windows
   .\scripts\monitor-memory.ps1
   
   # Linux
   bash scripts/monitor-memory.sh
   ```

### 内存不足

1. **临时解决方案**
   - 清理旧日志文件
   - 重启Nacos释放内存
   - 临时关闭非核心服务

2. **长期解决方案**
   - 优化代码，减少内存占用
   - 升级服务器配置（推荐4C8G或更高）
   - 考虑服务合并部署

## 📊 监控指标

### 关键指标阈值

- **堆内存使用率**: < 80% (警告: > 80%, 严重: > 90%)
- **Full GC频率**: < 1次/小时 (警告: > 5次/小时)
- **GC暂停时间**: < 200ms (警告: > 500ms)
- **线程数**: < 200

### 监控工具

- **内置脚本**: `monitor-memory.sh/ps1`
- **JDK工具**: `jstat`, `jmap`, `jstack`
- **可视化工具**: `jvisualvm`, `jconsole`

## ⚠️ 注意事项

1. **首次使用前**
   - 确保已安装Java 17或更高版本
   - 确保已编译所有服务（`mvn clean package`）
   - 检查日志目录权限

2. **生产环境**
   - 根据实际负载调整内存参数
   - 定期检查GC日志和堆转储
   - 设置监控告警

3. **资源限制**
   - 2C4G服务器资源有限，建议升级到4C8G
   - 考虑使用Docker限制资源
   - 考虑服务合并部署

## 📚 更多信息

详细配置说明和优化建议请参考：
- `JVM调优指南.md` - 完整文档
- `JVM调优快速参考.md` - 快速参考

## 🆘 常见问题

**Q: 服务启动失败，提示内存不足**
- 检查系统内存使用情况
- 减少某个服务的内存配置
- 关闭其他占用内存的程序

**Q: 如何确定合适的内存大小？**
- 启动服务后使用 `jmap -heap <pid>` 查看实际使用
- 观察GC日志，确保Full GC不频繁
- 预留20-30%的缓冲空间

**Q: Full GC频繁怎么办？**
- 检查堆内存是否过小
- 检查是否有大对象
- 调整G1GC参数

更多问题请参考 `JVM调优指南.md` 中的"常见问题"章节。

