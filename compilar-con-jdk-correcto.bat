@echo off
echo ========================================
echo Compilando con JDK de Android Studio
echo ========================================

set "JAVA_HOME=C:\Program Files\Android\Android Studio\jbr"
set "PATH=%JAVA_HOME%\bin;%PATH%"

echo JDK configurado: %JAVA_HOME%
echo.

gradlew.bat clean assembleDebug

echo.
echo ========================================
echo Compilación finalizada
echo ========================================
pause
