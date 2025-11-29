# Script de diagnóstico para detectar por qué la app se cae al arrancar
# Ejecutar con PowerShell cuando el dispositivo Samsung SM-S921B esté conectado

Write-Host "=== DIAGNÓSTICO DE CRASH - APP AJICOLOR ===" -ForegroundColor Cyan
Write-Host ""

# 1. Verificar dispositivos conectados
Write-Host "1. Verificando dispositivos conectados..." -ForegroundColor Yellow
adb devices
Write-Host ""

# 2. Limpiar el logcat anterior
Write-Host "2. Limpiando logcat anterior..." -ForegroundColor Yellow
adb logcat -c
Write-Host "Logcat limpio." -ForegroundColor Green
Write-Host ""

# 3. Desinstalar versión anterior (si existe)
Write-Host "3. Desinstalando versión anterior..." -ForegroundColor Yellow
adb uninstall com.example.appajicolorgrupo4 2>$null
Write-Host ""

# 4. Instalar nueva versión
Write-Host "4. Instalando nueva versión..." -ForegroundColor Yellow
.\gradlew.bat installDebug
if ($LASTEXITCODE -eq 0) {
    Write-Host "APK instalado correctamente." -ForegroundColor Green
} else {
    Write-Host "Error al instalar APK." -ForegroundColor Red
    exit 1
}
Write-Host ""

# 5. Iniciar la app y capturar logs
Write-Host "5. Iniciando la aplicación y capturando logs..." -ForegroundColor Yellow
Write-Host "Presiona Ctrl+C para detener la captura de logs cuando termines." -ForegroundColor Cyan
Write-Host ""

# Iniciar la app
adb shell am start -S -n com.example.appajicolorgrupo4/.MainActivity -a android.intent.action.MAIN -c android.intent.category.LAUNCHER

# Capturar logs filtrados por el paquete y errores
Write-Host "--- LOGS DE LA APLICACIÓN (filtrando por paquete y errores) ---" -ForegroundColor Magenta
adb logcat -v threadtime | Select-String -Pattern "com.example.appajicolorgrupo4|FATAL|AndroidRuntime|CRASH"

Write-Host ""
Write-Host "=== FIN DEL DIAGNÓSTICO ===" -ForegroundColor Cyan

