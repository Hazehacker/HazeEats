# 内存监控脚本
# 用于监控所有Java进程的内存使用情况

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "JVM内存监控" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# 获取所有Java进程
$javaProcesses = Get-Process java -ErrorAction SilentlyContinue

if ($null -eq $javaProcesses -or $javaProcesses.Count -eq 0) {
    Write-Host "未找到运行中的Java进程" -ForegroundColor Yellow
    exit
}

Write-Host "进程ID | 进程名 | 内存使用(MB) | CPU(%) | 启动时间" -ForegroundColor Green
Write-Host "------------------------------------------------------------" -ForegroundColor Gray

$totalMemory = 0
foreach ($proc in $javaProcesses) {
    $memoryMB = [math]::Round($proc.WorkingSet64 / 1MB, 2)
    $cpu = [math]::Round($proc.CPU, 2)
    $startTime = $proc.StartTime.ToString("yyyy-MM-dd HH:mm:ss")
    
    Write-Host ("{0,-8} | {1,-20} | {2,12} | {3,6} | {4}" -f `
        $proc.Id, $proc.ProcessName, $memoryMB, $cpu, $startTime)
    
    $totalMemory += $memoryMB
}

Write-Host "------------------------------------------------------------" -ForegroundColor Gray
Write-Host ("总内存使用: {0} MB ({1} GB)" -f [math]::Round($totalMemory, 2), [math]::Round($totalMemory/1024, 2)) -ForegroundColor Yellow

# 获取系统总内存
$totalSystemMemory = (Get-CimInstance Win32_ComputerSystem).TotalPhysicalMemory / 1GB
$usedSystemMemory = (Get-Counter '\Memory\Available MBytes').CounterSamples[0].CookedValue / 1024
$usedSystemMemoryGB = $totalSystemMemory - $usedSystemMemory

Write-Host ("系统总内存: {0} GB" -f [math]::Round($totalSystemMemory, 2)) -ForegroundColor Cyan
Write-Host ("系统已用内存: {0} GB ({1}%)" -f `
    [math]::Round($usedSystemMemoryGB, 2), `
    [math]::Round(($usedSystemMemoryGB / $totalSystemMemory) * 100, 2)) -ForegroundColor Cyan

Write-Host ""
Write-Host "提示: 使用 'jstat -gc <pid> 1000' 查看详细GC信息" -ForegroundColor Gray
Write-Host "提示: 使用 'jmap -heap <pid>' 查看堆内存详情" -ForegroundColor Gray

