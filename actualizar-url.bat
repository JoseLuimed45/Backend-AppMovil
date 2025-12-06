@echo off
echo ===============================================
echo Actualizando URL del backend y recompilando
echo ===============================================
set JAVA_HOME=C:\Program Files\Android\Android Studio\jbr
echo JAVA_HOME: %JAVA_HOME%
echo.
echo Limpiando build anterior...
call gradlew.bat clean
echo.
echo Compilando con nueva URL...
call gradlew.bat assembleDebug
echo.
echo Instalando en dispositivo...
call gradlew.bat installDebug
echo.
echo Lanzando aplicacion...
adb shell am start -n com.example.appajicolorgrupo4/.MainActivity
echo.
echo ===============================================
echo App actualizada y ejecutada!
echo BASE_URL: https://app-poleras-backend.vercel.app/
echo ===============================================
pause
