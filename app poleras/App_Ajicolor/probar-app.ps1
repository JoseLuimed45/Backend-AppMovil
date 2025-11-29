# Script para probar la aplicacion despues de la correccion
# Ejecutar con PowerShell

Write-Host "=== PROBANDO APP AJI COLOR (CORRECCION APLICADA) ===" -ForegroundColor Green
Write-Host ""

# 1. Verificar dispositivo
Write-Host "1. Verificando dispositivo conectado..." -ForegroundColor Yellow
$devices = adb devices
if ($devices -match "device") {
    Write-Host "Dispositivo conectado OK" -ForegroundColor Green
} else {
    Write-Host "No hay dispositivo conectado" -ForegroundColor Red
    Write-Host "Conecta tu dispositivo Samsung y habilita la depuracion USB" -ForegroundColor Yellow
    exit 1
}
Write-Host ""

# 2. Limpiar logcat
Write-Host "2. Limpiando logcat..." -ForegroundColor Yellow
adb logcat -c
Write-Host "Logcat limpio OK" -ForegroundColor Green
Write-Host ""

# 3. Iniciar la app
Write-Host "3. Iniciando la aplicacion..." -ForegroundColor Yellow
adb shell am start -S -n com.example.appajicolorgrupo4/.MainActivity -a android.intent.action.MAIN -c android.intent.category.LAUNCHER
Start-Sleep -Seconds 2
Write-Host "App iniciada OK" -ForegroundColor Green
Write-Host ""

# 4. Capturar logs en tiempo real (primeros 10 segundos)
Write-Host "4. Monitoreando logs por 10 segundos..." -ForegroundColor Yellow
Write-Host "(Buscando errores FATAL...)" -ForegroundColor Cyan
Write-Host ""

$startTime = Get-Date
$endTime = $startTime.AddSeconds(10)
$foundFatal = $false

while ((Get-Date) -lt $endTime) {
    $logs = adb logcat -d -v brief | Select-String -Pattern "FATAL|AndroidRuntime.*com.example.appajicolorgrupo4" -Context 0,5
    if ($logs) {
        Write-Host "ERROR FATAL DETECTADO:" -ForegroundColor Red
        Write-Host $logs -ForegroundColor Red
        $foundFatal = $true
        break
    }
    Start-Sleep -Milliseconds 500
}

Write-Host ""

if (-not $foundFatal) {
    Write-Host "=== EXITO: NO SE DETECTARON CRASHES ===" -ForegroundColor Green
    Write-Host ""
    Write-Host "La aplicacion esta funcionando correctamente." -ForegroundColor Green
    Write-Host "Puedes probar navegar por las pantallas:" -ForegroundColor Cyan
    Write-Host "  - Pantalla de inicio (logo)" -ForegroundColor White
    Write-Host "  - Botones de Inicio Sesion y Crear Cuenta" -ForegroundColor White
    Write-Host "  - Navegacion general" -ForegroundColor White
} else {
    Write-Host "=== ERROR: LA APP SIGUE CRASHEANDO ===" -ForegroundColor Red
    Write-Host "Por favor, comparte el error mostrado arriba" -ForegroundColor Yellow
}

Write-Host ""
Write-Host "Presiona cualquier tecla para salir..." -ForegroundColor Gray
$null = $Host.UI.RawUI.ReadKey('NoEcho,IncludeKeyDown')


