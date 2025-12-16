# Script para ejecutar pruebas unitarias en Windows

Write-Host "======================================" -ForegroundColor Cyan
Write-Host "EJECUCIÓN DE PRUEBAS UNITARIAS" -ForegroundColor Cyan
Write-Host "======================================" -ForegroundColor Cyan
Write-Host ""

# Paso 1: Limpiar build anterior
Write-Host "[1/4] Limpiando build anterior..." -ForegroundColor Yellow
& .\gradlew.bat clean --quiet 2>&1 | Out-Null

# Paso 2: Compilar el proyecto
Write-Host "[2/4] Compilando proyecto..." -ForegroundColor Yellow
& .\gradlew.bat compileDebugUnitTestKotlin --quiet 2>&1 | Out-Null

if ($LASTEXITCODE -ne 0) {
    Write-Host "❌ Error en compilación" -ForegroundColor Red
    exit 1
}

# Paso 3: Ejecutar tests unitarios
Write-Host "[3/4] Ejecutando pruebas unitarias..." -ForegroundColor Yellow
& .\gradlew.bat testDebugUnitTest --no-daemon 2>&1 | ForEach-Object {
    if ($_ -match "FAILED|PASSED|SKIPPED") {
        Write-Host $_
    }
}

if ($LASTEXITCODE -ne 0) {
    Write-Host "❌ Algunas pruebas fallaron" -ForegroundColor Red
    exit 1
}

# Paso 4: Mostrar resultado
Write-Host ""
Write-Host "[4/4] Generando reporte..." -ForegroundColor Yellow
Write-Host ""

Write-Host "======================================" -ForegroundColor Green
Write-Host "✅ TODAS LAS PRUEBAS COMPLETADAS" -ForegroundColor Green
Write-Host "======================================" -ForegroundColor Green
Write-Host ""
Write-Host "Reportes disponibles en:" -ForegroundColor Cyan
Write-Host "  - app/build/reports/tests/testDebugUnitTest/" -ForegroundColor White
Write-Host "  - Abre: build/reports/tests/testDebugUnitTest/index.html en navegador" -ForegroundColor White

