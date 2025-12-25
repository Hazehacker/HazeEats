# OOM问题排查脚本
# 检查日志中的OOM错误和堆转储文件

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "OOM问题排查" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# 检查日志目录
$logDirs = @(
    ".\logs\user-service",
    ".\logs\cart-service",
    ".\logs\gateway",
    ".\logs\eats-server",
    ".\logs\order-service",
    ".\logs\dish-service",
    ".\logs\shop-service",
    ".\logs\pay-service",
    ".\logs\report-service"
)

Write-Host "1. 检查OOM错误日志..." -ForegroundColor Yellow
$oomFound = $false
foreach ($logDir in $logDirs) {
    if (Test-Path $logDir) {
        $logFiles = Get-ChildItem -Path $logDir -Filter "*.log" -Recurse -ErrorAction SilentlyContinue
        foreach ($logFile in $logFiles) {
            $content = Get-Content $logFile -Tail 100 -ErrorAction SilentlyContinue
            if ($content -match "OutOfMemoryError|OOM|java.lang.OutOfMemoryError") {
                Write-Host "发现OOM错误: $($logFile.FullName)" -ForegroundColor Red
                $oomFound = $true
                # 显示相关日志
                $content | Select-String -Pattern "OutOfMemoryError|OOM" -Context 2,2 | ForEach-Object {
                    Write-Host $_.Line -ForegroundColor Red
                }
            }
        }
    }
}

if (-not $oomFound) {
    Write-Host "未发现OOM错误" -ForegroundColor Green
}

Write-Host ""
Write-Host "2. 检查堆转储文件..." -ForegroundColor Yellow
$heapDumps = Get-ChildItem -Path ".\logs" -Filter "heapdump.hprof" -Recurse -ErrorAction SilentlyContinue
if ($heapDumps.Count -gt 0) {
    Write-Host "发现堆转储文件:" -ForegroundColor Yellow
    foreach ($dump in $heapDumps) {
        $sizeMB = [math]::Round($dump.Length / 1MB, 2)
        Write-Host "  $($dump.FullName) - $sizeMB MB - 创建时间: $($dump.CreationTime)" -ForegroundColor Yellow
    }
    Write-Host ""
    Write-Host "分析堆转储文件:" -ForegroundColor Cyan
    Write-Host "  jhat $($heapDumps[0].FullName)" -ForegroundColor Gray
    Write-Host "  或使用 jvisualvm 打开文件" -ForegroundColor Gray
} else {
    Write-Host "未发现堆转储文件" -ForegroundColor Green
}

Write-Host ""
Write-Host "3. 检查GC日志..." -ForegroundColor Yellow
$gcLogs = Get-ChildItem -Path ".\logs" -Filter "gc.log*" -Recurse -ErrorAction SilentlyContinue
if ($gcLogs.Count -gt 0) {
    Write-Host "发现GC日志文件:" -ForegroundColor Yellow
    foreach ($gcLog in $gcLogs) {
        $sizeMB = [math]::Round($gcLog.Length / 1MB, 2)
        Write-Host "  $($gcLog.FullName) - $sizeMB MB" -ForegroundColor Yellow
        
        # 检查Full GC频率
        $content = Get-Content $gcLog -Tail 1000 -ErrorAction SilentlyContinue
        $fullGCCount = ($content | Select-String -Pattern "Full GC|Pause Full").Count
        if ($fullGCCount -gt 0) {
            Write-Host "    最近1000行中发现 $fullGCCount 次Full GC" -ForegroundColor $(if ($fullGCCount -gt 10) { "Red" } else { "Yellow" })
        }
    }
} else {
    Write-Host "未发现GC日志文件" -ForegroundColor Yellow
}

Write-Host ""
Write-Host "4. 当前Java进程内存使用..." -ForegroundColor Yellow
$javaProcesses = Get-Process java -ErrorAction SilentlyContinue
if ($javaProcesses) {
    foreach ($proc in $javaProcesses) {
        $memoryMB = [math]::Round($proc.WorkingSet64 / 1MB, 2)
        $color = if ($memoryMB -gt 500) { "Red" } elseif ($memoryMB -gt 300) { "Yellow" } else { "Green" }
        Write-Host "  PID $($proc.Id) ($($proc.ProcessName)): $memoryMB MB" -ForegroundColor $color
    }
}

Write-Host ""
Write-Host "排查完成！" -ForegroundColor Green

