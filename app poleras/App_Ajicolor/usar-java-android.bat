@echo off
:: Script para fijar JAVA_HOME al JDK de Android Studio y ejecutar un comando Gradle opcional
:: Uso básico:
::   1) Doble clic -> solo configura la consola para esta ventana
::   2) Desde cmd: usar-java-android.bat assembleDebug
:: Si pasas argumentos se envían a gradlew.

set "JAVA_HOME=C:\Program Files\Android\Android Studio\jbr"
set "PATH=%JAVA_HOME%\bin;%PATH%"

echo ===============================================
echo JAVA_HOME configurado a: %JAVA_HOME%
echo ===============================================

if "%~1"=="" (
  echo No diste comando Gradle. Ejemplos:
  echo   usar-java-android.bat clean assembleDebug
  echo   usar-java-android.bat installDebug
  echo Abriendo shell interactiva...
  cmd /k
  exit /b 0
)

echo Ejecutando: gradlew.bat %*
call gradlew.bat %*

exit /b %ERRORLEVEL%