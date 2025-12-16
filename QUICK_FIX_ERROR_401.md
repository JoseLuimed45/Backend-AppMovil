# ⚡ Guía Rápida - Corrección Error 401 Login

## 🐛 Error Original
```
Login failed. Code: 401, Body: {"message":"Invalid credentials"}
```

## ✅ Solución Implementada

### Cambios realizados:
1. ✅ Modelo User actualizado (agregados `telefono` y `direccion`)
2. ✅ Respuesta de login/register ahora es plana (no anidada)
3. ✅ Errores usan `{ message: "..." }` consistentemente

### Archivos modificados:
- `api/models/User.js`
- `api/routes/auth.js`
- `test-auth.js` (nuevo script de pruebas)

---

## 🚀 Comandos para Desplegar

### 1. Commit y Push al repositorio
```bash
cd c:\Users\josel\AndroidStudioProjects\AppMovil\ajicolor_backend
git add api/models/User.js api/routes/auth.js
git commit -m "fix: corregir estructura de respuesta login/register para Android"
git push origin main
```

### 2. Vercel desplegará automáticamente
- Monitorea en: https://vercel.com/dashboard
- Espera a que el deployment sea "Ready"

### 3. Probar en producción
```bash
# Opción 1: Con el script de test
cd c:\Users\josel\AndroidStudioProjects\AppMovil\app_ajicolor_backend_node
BASE_URL=https://backend-app-movil.vercel.app node test-auth.js

# Opción 2: Con cURL
curl -i -X POST https://backend-app-movil.vercel.app/api/auth/register \
  -H "Content-Type: application/json" \
  -d "{\"name\":\"Test\",\"email\":\"test@example.com\",\"password\":\"password123\"}"
```

---

## 📱 Probar en la App Android

### 1. Recompilar la app (opcional si no cambió nada en Android)
```bash
cd c:\Users\josel\AndroidStudioProjects\AppMovil\app_ajicolor_backend_node
.\gradlew.bat assembleDebug
```

### 2. Instalar en emulador/dispositivo
```bash
adb install -r app\build\outputs\apk\debug\app-debug.apk
```

### 3. Probar Login
- Abrir la app
- Ir a pantalla de Login
- Ingresar credenciales válidas
- **Resultado esperado**: Login exitoso, navega a Home

---

## ✅ Respuesta Correcta (200/201)

```json
{
  "_id": "675fdb123abc456def789012",
  "nombre": "Usuario Test",
  "email": "test@example.com",
  "telefono": "",
  "direccion": "",
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "rol": "user"
}
```

## ❌ Error Correctamente Formateado (401)

```json
{
  "message": "Credenciales incorrectas"
}
```

---

## 🔍 Verificación Rápida

```bash
# 1. Backend está vivo
curl https://backend-app-movil.vercel.app/api/health/status

# 2. Test de registro
curl -X POST https://backend-app-movil.vercel.app/api/auth/register \
  -H "Content-Type: application/json" \
  -d "{\"name\":\"Test\",\"email\":\"test_$(date +%s)@example.com\",\"password\":\"password123\"}"

# 3. Test de login
curl -X POST https://backend-app-movil.vercel.app/api/auth/login \
  -H "Content-Type: application/json" \
  -d "{\"email\":\"test@example.com\",\"password\":\"password123\"}"
```

---

## 📚 Documentación Completa

Ver: `CORRECCION_ERROR_401_LOGIN.md`

---

**Última actualización:** 2025-12-16  
**Estado:** ✅ Correcciones implementadas  
**Siguiente paso:** Desplegar a Vercel y probar

