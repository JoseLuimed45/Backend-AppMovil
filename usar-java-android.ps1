<#!
Script PowerShell para fijar JAVA_HOME al JDK de Android Studio de forma temporal.
Uso rápido:
  PS> .\usar-java-android.ps1 assembleDebug
  PS> .\usar-java-android.ps1 clean test --info
Si no pasas argumentos, abre una subshell con el entorno listo.
#>
param(
  [Parameter(ValueFromRemainingArguments = $true)]
  [string[]] $GradleArgs
)

$jdkPath = "C:\Program Files\Android\Android Studio\jbr"

if (-not (Test-Path $jdkPath)) {
  Write-Host "JDK no encontrado en $jdkPath" -ForegroundColor Red
  exit 1
}

$env:JAVA_HOME = $jdkPath
$env:Path = "$($env:JAVA_HOME)\bin;$env:Path"
Write-Host "JAVA_HOME configurado a: $env:JAVA_HOME" -ForegroundColor Cyan

if (-not $GradleArgs -or $GradleArgs.Count -eq 0) {
  Write-Host "Sin argumentos: abriendo una sesión interactiva (exit para salir)." -ForegroundColor Yellow
  powershell
  exit 0
}

Write-Host "Ejecutando gradlew $GradleArgs" -ForegroundColor Green
& .\gradlew.bat @GradleArgs
exit $LASTEXITCODE