@echo off
setlocal enabledelayedexpansion

REM Desactivar JAVA_TOOL_OPTIONS si existe
set JAVA_TOOL_OPTIONS=

REM Configurar JAVA_HOME a Android Studio JBR
set JAVA_HOME=C:\Program Files\Android\Android Studio\jbr

REM Verificar que java existe
if not exist "%JAVA_HOME%\bin\java.exe" (
    echo ERROR: No se encontró java en %JAVA_HOME%\bin\java.exe
    exit /b 1
)

echo Usando JAVA_HOME: %JAVA_HOME%
"%JAVA_HOME%\bin\java.exe" -version

REM Compilar con gradlew
call gradlew.bat assembleDebug -x test --stacktrace

endlocal
