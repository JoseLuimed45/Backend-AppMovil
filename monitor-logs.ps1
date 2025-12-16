# Script para monitorear logs de la aplicación
# Guarda los logs en un archivo y los muestra en tiempo real

$adbPath = "$env:LOCALAPPDATA\Android\Sdk\platform-tools\adb.exe"
$logFile = "app-logcat.txt"

Write-Host "🔍 Monitoreando logs de la aplicación..." -ForegroundColor Cyan
Write-Host "📝 Guardando en: $logFile" -ForegroundColor Yellow
Write-Host "⏹️  Presiona Ctrl+C para detener" -ForegroundColor Gray
Write-Host ""

# Limpiar logs anteriores
& $adbPath logcat -c

# Capturar logs filtrados
& $adbPath logcat | ForEach-Object {
    $line = $_

    # Filtrar por paquete y palabras clave
    if ($line -match "appajicolorgrupo4|UserRepository|AuthViewModel|Login|Register|NetworkResult|Error|Exception") {
        # Colorear según el tipo
        if ($line -match "Error|Exception|FATAL") {
            Write-Host $line -ForegroundColor Red
        }
        elseif ($line -match "Warning|WARN") {
            Write-Host $line -ForegroundColor Yellow
        }
        elseif ($line -match "Login|Register|Auth") {
            Write-Host $line -ForegroundColor Green
        }
        else {
            Write-Host $line -ForegroundColor White
        }

        # Guardar en archivo
        Add-Content -Path $logFile -Value $line
    }
}

