# Script para probar login y acceso al panel de administración
# Ejecutar con PowerShell

Write-Host ""
Write-Host "=====================================================" -ForegroundColor Cyan
Write-Host "  PRUEBA DE PANEL DE ADMINISTRACION - AJI COLOR" -ForegroundColor Yellow
Write-Host "=====================================================" -ForegroundColor Cyan
Write-Host ""

Write-Host "Credenciales de Administrador:" -ForegroundColor Green
Write-Host "  Email:    admin@ajicolor.cl" -ForegroundColor White
Write-Host "  Password: ajicolor" -ForegroundColor White
Write-Host ""

Write-Host "Funcionalidades disponibles:" -ForegroundColor Green
Write-Host "  [+] Listar todos los productos" -ForegroundColor White
Write-Host "  [+] Agregar nuevo producto" -ForegroundColor White
Write-Host "  [+] Editar producto existente" -ForegroundColor White
Write-Host "  [+] Eliminar producto" -ForegroundColor White
Write-Host "  [+] Modificar precios" -ForegroundColor White
Write-Host "  [+] Actualizar stock" -ForegroundColor White
Write-Host ""

Write-Host "Pasos para probar:" -ForegroundColor Yellow
Write-Host "  1. Abre la aplicacion en tu dispositivo" -ForegroundColor White
Write-Host "  2. Click en 'Iniciar Sesion'" -ForegroundColor White
Write-Host "  3. Ingresa las credenciales de admin" -ForegroundColor White
Write-Host "  4. Seras redirigido al Panel de Administracion" -ForegroundColor White
Write-Host "  5. Usa el boton '+' para agregar productos" -ForegroundColor White
Write-Host "  6. Usa los iconos de editar/eliminar en cada producto" -ForegroundColor White
Write-Host ""

# Verificar dispositivo
Write-Host "Verificando dispositivo conectado..." -ForegroundColor Yellow
$devices = adb devices 2>&1
if ($devices -match "\tdevice") {
    Write-Host "[OK] Dispositivo conectado" -ForegroundColor Green
} else {
    Write-Host "[!] No hay dispositivo conectado" -ForegroundColor Red
    Write-Host "    Conecta tu dispositivo y habilita USB Debug" -ForegroundColor Yellow
    Write-Host ""
    Write-Host "Presiona cualquier tecla para salir..." -ForegroundColor Gray
    $null = $Host.UI.RawUI.ReadKey('NoEcho,IncludeKeyDown')
    exit
}

Write-Host ""
Write-Host "Deseas iniciar la app ahora? (S/N): " -ForegroundColor Cyan -NoNewline
$respuesta = Read-Host

if ($respuesta -eq "S" -or $respuesta -eq "s") {
    Write-Host ""
    Write-Host "Iniciando aplicacion..." -ForegroundColor Yellow
    adb shell am start -S -n com.example.appajicolorgrupo4/.MainActivity
    Start-Sleep -Seconds 2

    Write-Host "[OK] App iniciada!" -ForegroundColor Green
    Write-Host ""
    Write-Host "Ahora puedes:" -ForegroundColor Cyan
    Write-Host "  1. Hacer login con las credenciales de admin" -ForegroundColor White
    Write-Host "  2. Explorar el panel de administracion" -ForegroundColor White
    Write-Host "  3. Agregar/Editar/Eliminar productos" -ForegroundColor White
    Write-Host ""

    Write-Host "Monitoreando logs por errores..." -ForegroundColor Yellow
    Start-Sleep -Seconds 5

    $errors = adb logcat -d -s AndroidRuntime:E | Select-String "com.example.appajicolorgrupo4"
    if ($errors) {
        Write-Host "[!] Se detectaron errores:" -ForegroundColor Red
        Write-Host $errors -ForegroundColor Red
    } else {
        Write-Host "[OK] No se detectaron errores!" -ForegroundColor Green
    }
}

Write-Host ""
Write-Host "=====================================================" -ForegroundColor Cyan
Write-Host "Para mas informacion, lee: PANEL_ADMIN_PRODUCTOS.md" -ForegroundColor Yellow
Write-Host "=====================================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "Presiona cualquier tecla para salir..." -ForegroundColor Gray
$null = $Host.UI.RawUI.ReadKey('NoEcho,IncludeKeyDown')

