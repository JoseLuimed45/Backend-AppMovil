# Script para copiar la firma JKS al directorio del proyecto
Write-Host "=== COPIAR FIRMA JKS AL PROYECTO ===" -ForegroundColor Cyan

$origenDir = "C:\Users\josel\AndroidStudioProjects\FimasJKS\firmaDevloper"
$destinoDir = "C:\Users\josel\AndroidStudioProjects\app_ajicolor_backend_node\app poleras\App_Ajicolor\app\keystore"

Write-Host "`nBuscando archivos de firma en: $origenDir" -ForegroundColor Yellow

if (Test-Path $origenDir) {
    $firmas = Get-ChildItem -Path $origenDir -Include "*.jks", "*.keystore" -Recurse -ErrorAction SilentlyContinue

    if ($firmas.Count -gt 0) {
        Write-Host "`nArchivos de firma encontrados:" -ForegroundColor Green
        $firmas | ForEach-Object { Write-Host "  - $($_.Name)" -ForegroundColor White }

        Write-Host "`n¿Cuál archivo deseas copiar?" -ForegroundColor Yellow
        for ($i = 0; $i -lt $firmas.Count; $i++) {
            Write-Host "  [$i] $($firmas[$i].Name)"
        }

        $seleccion = Read-Host "`nIngresa el número"

        if ($seleccion -match '^\d+$' -and [int]$seleccion -lt $firmas.Count) {
            $archivoSeleccionado = $firmas[[int]$seleccion]

            # Copiar archivo
            Copy-Item -Path $archivoSeleccionado.FullName -Destination "$destinoDir\alejandro-key.jks" -Force

            Write-Host "`n✓ Archivo copiado exitosamente a:" -ForegroundColor Green
            Write-Host "  $destinoDir\alejandro-key.jks" -ForegroundColor White

            # Verificar información del keystore
            Write-Host "`n=== VERIFICANDO INFORMACIÓN DEL KEYSTORE ===" -ForegroundColor Cyan
            Write-Host "Ejecutando: keytool -list -v -keystore alejandro-key.jks -storepass 35203520" -ForegroundColor Gray

            Push-Location $destinoDir
            keytool -list -v -keystore "alejandro-key.jks" -storepass "35203520" 2>$null | Select-String "Alias name:|Owner:"
            Pop-Location

            Write-Host "`n✓ Configuración completada!" -ForegroundColor Green
            Write-Host "  - Archivo: app/keystore/alejandro-key.jks" -ForegroundColor White
            Write-Host "  - Password: 35203520" -ForegroundColor White
            Write-Host "  - Alias: alejandro_placencia" -ForegroundColor White

        } else {
            Write-Host "`n✗ Selección inválida" -ForegroundColor Red
        }
    } else {
        Write-Host "`n✗ No se encontraron archivos de firma (.jks o .keystore)" -ForegroundColor Red
        Write-Host "`nPor favor, copia manualmente tu archivo .jks a:" -ForegroundColor Yellow
        Write-Host "  $destinoDir\alejandro-key.jks" -ForegroundColor White
    }
} else {
    Write-Host "`n✗ No se encontró el directorio: $origenDir" -ForegroundColor Red
    Write-Host "`nPor favor, copia manualmente tu archivo .jks a:" -ForegroundColor Yellow
    Write-Host "  $destinoDir\alejandro-key.jks" -ForegroundColor White
}

Write-Host "`nPresiona cualquier tecla para continuar..."
$null = $Host.UI.RawUI.ReadKey("NoEcho,IncludeKeyDown")

