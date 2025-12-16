#!/bin/bash

# Script para ejecutar todas las pruebas unitarias y generar reporte

echo "======================================"
echo "EJECUCIÓN DE PRUEBAS UNITARIAS"
echo "======================================"
echo ""

# Paso 1: Limpiar build anterior
echo "[1/4] Limpiando build anterior..."
./gradlew clean --quiet

# Paso 2: Compilar el proyecto
echo "[2/4] Compilando proyecto..."
./gradlew compileDebugUnitTestKotlin --quiet

if [ $? -ne 0 ]; then
    echo "❌ Error en compilación"
    exit 1
fi

# Paso 3: Ejecutar tests unitarios
echo "[3/4] Ejecutando pruebas unitarias..."
./gradlew testDebugUnitTest --no-daemon --info

if [ $? -ne 0 ]; then
    echo "❌ Algunas pruebas fallaron"
    exit 1
fi

# Paso 4: Mostrar resultado
echo ""
echo "[4/4] Generando reporte..."
echo ""

echo "======================================"
echo "✅ TODAS LAS PRUEBAS COMPLETADAS"
echo "======================================"
echo ""
echo "Reportes disponibles en:"
echo "  - app/build/reports/tests/testDebugUnitTest/"
echo "  - Abre: build/reports/tests/testDebugUnitTest/index.html en navegador"

