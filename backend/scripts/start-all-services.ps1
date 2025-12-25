# 启动所有微服务的脚本
# 注意: 在Windows上，建议使用Start-Process在后台启动服务

$ScriptPath = Split-Path -Parent $MyInvocation.MyCommand.Path
$Services = @(
    @{Name="eats-gateway"; Script="start-gateway.ps1"; Port=8080},
    @{Name="eats-server"; Script="start-server.ps1"; Port=8080},
    @{Name="user-service"; Script="start-user-service.ps1"; Port=8082},
    @{Name="cart-service"; Script="start-cart-service.ps1"; Port=8081},
    @{Name="order-service"; Script="start-order-service.ps1"; Port=8083},
    @{Name="dish-service"; Script="start-dish-service.ps1"; Port=8084},
    @{Name="shop-service"; Script="start-shop-service.ps1"; Port=8085},
    @{Name="pay-service"; Script="start-pay-service.ps1"; Port=8086},
    @{Name="report-service"; Script="start-report-service.ps1"; Port=8087}
)

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "启动所有微服务" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

foreach ($service in $Services) {
    $scriptFile = Join-Path $ScriptPath $service.Script
    if (Test-Path $scriptFile) {
        Write-Host "启动服务: $($service.Name) (端口: $($service.Port))" -ForegroundColor Yellow
        Start-Process powershell -ArgumentList "-NoExit", "-File", $scriptFile -WindowStyle Minimized
        Start-Sleep -Seconds 3
    } else {
        Write-Host "警告: 找不到启动脚本 $($service.Script)" -ForegroundColor Red
    }
}

Write-Host ""
Write-Host "所有服务启动完成！" -ForegroundColor Green
Write-Host "提示: 使用 monitor-memory.ps1 监控内存使用情况" -ForegroundColor Yellow

