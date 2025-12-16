@echo off
echo === Verificando despliegue en Vercel ===
echo.

echo 1. Probando endpoint raiz...
curl.exe -i https://backend-app-movil.vercel.app/
echo.

echo 2. Probando POST /api/v1/usuarios/login...
curl.exe -X POST https://backend-app-movil.vercel.app/api/v1/usuarios/login -H "Content-Type: application/json" -d "{\"email\":\"admin@ajicolor.com\",\"password\":\"admin123\"}"
echo.

echo 3. Probando si la ruta PUT existe (debe dar 401 sin auth)...
curl.exe -i -X PUT https://backend-app-movil.vercel.app/api/v1/usuarios/test123 -H "Content-Type: application/json" -d "{\"nombre\":\"Test\",\"telefono\":\"123\",\"direccion\":\"Test\"}"
echo.

echo === Verificacion completada ===
pause

