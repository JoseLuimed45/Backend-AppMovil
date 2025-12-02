# Script de Verificacion del Proyecto Android
# Ejecutar desde: app poleras\App_Ajicolor

Write-Host "Verificacion del Proyecto Android - AJICOLOR" -ForegroundColor Cyan
Write-Host "================================================" -ForegroundColor Cyan
Write-Host ""

# 1. Verificar Gradle
Write-Host "Verificando Gradle..." -ForegroundColor Yellow
try {
    $gradleOutput = .\gradlew.bat --version 2>&1
    $gradleVersion = $gradleOutput | Select-String "Gradle"
    if ($gradleVersion) {
        Write-Host "OK Gradle instalado: $gradleVersion" -ForegroundColor Green
    }
} catch {
    Write-Host "ERROR al ejecutar Gradle" -ForegroundColor Red
}

# 2. Verificar JDK
Write-Host ""
Write-Host "Verificando JDK..." -ForegroundColor Yellow
try {
    $javaOutput = java -version 2>&1
    $javaVersion = $javaOutput | Select-String "version"
    if ($javaVersion) {
        Write-Host "OK $javaVersion" -ForegroundColor Green
    }
} catch {
    Write-Host "ERROR JDK no encontrado" -ForegroundColor Red
}

# 3. Verificar Android SDK
Write-Host ""
Write-Host "Verificando Android SDK..." -ForegroundColor Yellow
$sdkPaths = @(
    "$env:LOCALAPPDATA\Android\Sdk",
    "C:\Android\Sdk"
)

$sdkFound = $false
foreach ($path in $sdkPaths) {
    if ($path -and (Test-Path $path)) {
        Write-Host "OK SDK encontrado en: $path" -ForegroundColor Green
        $sdkFound = $true

        if (Test-Path "$path\platform-tools\adb.exe") {
            Write-Host "  OK Platform Tools instaladas" -ForegroundColor Green
        }

        if (Test-Path "$path\build-tools") {
            $buildTools = Get-ChildItem "$path\build-tools" -Directory -ErrorAction SilentlyContinue | Select-Object -Last 1
            if ($buildTools) {
                Write-Host "  OK Build Tools: $($buildTools.Name)" -ForegroundColor Green
            }
        }
        break
    }
}

if (-not $sdkFound) {
    Write-Host "ERROR Android SDK no encontrado" -ForegroundColor Red
    Write-Host "  INFO Instala Android Studio o descarga Command Line Tools" -ForegroundColor Yellow
}

# 4. Verificar archivo local.properties
Write-Host ""
Write-Host "Verificando configuracion local..." -ForegroundColor Yellow
if (Test-Path "local.properties") {
    Write-Host "OK local.properties existe" -ForegroundColor Green
    $content = Get-Content "local.properties" -ErrorAction SilentlyContinue | Select-String "sdk.dir"
    if ($content) {
        Write-Host "  OK SDK configurado: $content" -ForegroundColor Green
    }
} else {
    Write-Host "ERROR local.properties no existe" -ForegroundColor Red
    Write-Host "  INFO Crea el archivo con: sdk.dir=C:\\Users\\<usuario>\\AppData\\Local\\Android\\Sdk" -ForegroundColor Yellow
}

# 5. Verificar archivos de configuracion
Write-Host ""
Write-Host "Verificando archivos de configuracion..." -ForegroundColor Yellow
$configFiles = @(
    "build.gradle.kts",
    "app\build.gradle.kts",
    "settings.gradle.kts",
    "gradle.properties"
)

foreach ($file in $configFiles) {
    if (Test-Path $file) {
        Write-Host "OK $file" -ForegroundColor Green
    } else {
        Write-Host "ERROR $file no encontrado" -ForegroundColor Red
    }
}

# 6. Verificar dependencias criticas
Write-Host ""
Write-Host "Verificando dependencias en app/build.gradle.kts..." -ForegroundColor Yellow
if (Test-Path "app\build.gradle.kts") {
    $buildGradle = Get-Content "app\build.gradle.kts" -ErrorAction SilentlyContinue

    $dependencies = @{
        "Retrofit" = "com.squareup.retrofit2:retrofit"
        "Room" = "androidx.room:room-runtime"
        "Compose" = "androidx.compose.material3:material3"
        "Navigation" = "androidx.navigation:navigation-compose"
        "Coroutines" = "kotlinx-coroutines-android"
    }

    foreach ($dep in $dependencies.GetEnumerator()) {
        if ($buildGradle -match $dep.Value) {
            Write-Host "OK $($dep.Key)" -ForegroundColor Green
        } else {
            Write-Host "AVISO $($dep.Key) no detectada" -ForegroundColor Yellow
        }
    }
}

# 7. Verificar dispositivos conectados
Write-Host ""
Write-Host "Verificando dispositivos Android..." -ForegroundColor Yellow
$adbFound = $false
$sdkPaths | ForEach-Object {
    if ($_ -and (Test-Path "$_\platform-tools\adb.exe")) {
        $adbPath = "$_\platform-tools\adb.exe"
        $adbFound = $true
        try {
            $devicesOutput = & $adbPath devices 2>$null
            if ($devicesOutput) {
                $deviceLines = $devicesOutput | Select-String "device$"
                if ($deviceLines.Count -gt 0) {
                    Write-Host "OK Dispositivos conectados: $($deviceLines.Count)" -ForegroundColor Green
                } else {
                    Write-Host "INFO No hay dispositivos conectados" -ForegroundColor Yellow
                }
            }
        } catch {
            Write-Host "INFO No se pudo ejecutar ADB" -ForegroundColor Yellow
        }
    }
}

if (-not $adbFound) {
    Write-Host "INFO ADB no disponible" -ForegroundColor Yellow
}

# 8. Resumen final
Write-Host ""
Write-Host "================================================" -ForegroundColor Cyan
Write-Host "RESUMEN" -ForegroundColor Cyan
Write-Host "================================================" -ForegroundColor Cyan

if ($gradleVersion -and $javaVersion) {
    Write-Host "OK Entorno de desarrollo funcionando" -ForegroundColor Green
} else {
    Write-Host "ERROR Faltan herramientas de desarrollo" -ForegroundColor Red
}

if ($sdkFound -or (Test-Path "local.properties")) {
    Write-Host "OK Android SDK configurado" -ForegroundColor Green
} else {
    Write-Host "ERROR Android SDK requiere configuracion" -ForegroundColor Red
    Write-Host ""
    Write-Host "SIGUIENTES PASOS:" -ForegroundColor Yellow
    Write-Host "1. Abre el proyecto en Android Studio" -ForegroundColor White
    Write-Host "2. Ve a File > Project Structure > SDK Location" -ForegroundColor White
    Write-Host "3. Instala Android SDK 36 (API Level 36)" -ForegroundColor White
    Write-Host "4. Ejecuta: .\gradlew.bat assembleDebug" -ForegroundColor White
}

Write-Host ""
Write-Host "Para mas detalles, consulta: CONFIGURACION_SDK.md" -ForegroundColor Cyan
Write-Host ""

