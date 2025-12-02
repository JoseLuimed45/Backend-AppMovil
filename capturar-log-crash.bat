@echo off
:: Captura rápida de crash al abrir y seleccionar una polera.
:: Requisitos: tener adb en PATH y un dispositivo conectado.
:: Salida: log-crash.txt en esta carpeta.

set PKG=com.example.appajicolorgrupo4
set ACT=.MainActivity
set OUT=log-crash.txt

echo Verificando dispositivos...
adb devices

echo Limpiando logcat anterior...
adb logcat -c

echo Iniciando app limpia...
adb shell am force-stop %PKG% 2>nul
adb shell am start -n %PKG%/%ACT%
echo ===================================== > %OUT%
echo  LOG CRASH APP AJICOLOR              >> %OUT%
echo  Fecha: %DATE% %TIME%                >> %OUT%
echo ===================================== >> %OUT%
echo Capturando (pulsa Ctrl+C para terminar cuando se caiga)...
adb logcat -v time | findstr /R /C:"%PKG%" /C:"AndroidRuntime" /C:"FATAL" >> %OUT%
echo Logs guardados en %OUT%
