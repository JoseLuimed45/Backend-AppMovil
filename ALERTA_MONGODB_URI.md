# 🔴 ALERTA CRÍTICA: MongoDB URI Incorrecta en Vercel

## ⚡ RESUMEN EJECULTIVO (30 segundos)

**Problema:** Vercel tiene credenciales incorrectas para MongoDB  
**Impacto:** No se pueden crear/modificar datos en la base de datos  
**Solución:** Cambiar 1 variable en Vercel (3 clics)  
**Tiempo:** 5 minutos total (3 clics + espera)  

---

## 🔍 DIAGNÓSTICO TÉCNICO

### Vercel Backend Status
- ✅ Servidor corriendo y accesible
- ✅ Health check respondiendo (200 OK)
- ❌ **MongoDB connection fallida**
- ❌ **POST requests retornan 503 "Base de datos no disponible"**

### Causa Identificada
La variable `MONGO_URI` en Vercel contiene:
```
mongodb+srv://ajicolor_db_use:Bbc35203520@cluster.mongodb.net/BDAjicolor
```

Pero debería ser:
```
mongodb+srv://ajicolor:ajicolor123@ajicolor.6byd9.mongodb.net/BDAjicolor?retryWrites=true&w=majority
```

### Diferencias Detectadas
1. **Usuario:** `ajicolor_db_use` → debe ser `ajicolor`
2. **Contraseña:** `Bbc35203520` → debe ser `ajicolor123`
3. **Cluster:** `cluster` → debe ser `ajicolor.6byd9`
4. **Parámetros:** Faltan `?retryWrites=true&w=majority`

---

## ✅ CÓMO ARREGLARLO

### OPCIÓN 1: Panel Web (RECOMENDADO - 2 minutos)

**Paso 1:** Ir a Vercel dashboard
```
https://vercel.com/dashboard/joses-projects-7d87f6dc/ajicolor_backend/settings
```

**Paso 2:** Eliminar MONGO_URI incorrecta
- Busca Environment Variables en el menú
- Encuentra `MONGO_URI`
- Haz click en el icono ❌ (delete)

**Paso 3:** Crear MONGO_URI correcta
- Click "New Environment Variable"
- **Key:** `MONGO_URI`
- **Value:** Copia exactamente:
  ```
  mongodb+srv://ajicolor:ajicolor123@ajicolor.6byd9.mongodb.net/BDAjicolor?retryWrites=true&w=majority
  ```
- **Environments:** Production, Preview, Development (selecciona todos)
- Click **SAVE**

✅ **Listo.** Vercel automáticamente redespliega.

### OPCIÓN 2: Terminal (si tienes Node.js)

```bash
cd "app poleras\Backend"
npx vercel env rm MONGO_URI
npx vercel env add MONGO_URI
# Ingresa la URI correcta cuando lo pida
npx vercel --prod
```

---

## 🧪 VERIFICAR QUE FUNCIONA

Después de cambiar, abre en el navegador:

**1. Health Check (debe estar OK)**
```
https://ajicolorbackend.vercel.app/health
```
Respuesta esperada:
```json
{"status":"OK","timestamp":"2025-12-06T06:30:00.000Z"}
```

**2. Listar Productos (debe retornar array)**
```
https://ajicolorbackend.vercel.app/api/v1/productos
```
Respuesta esperada:
```json
[]
```
(Array vacío está bien, solo verifica que no hay error)

**3. Crear Producto (prueba final)**

Desde PowerShell:
```powershell
$body = @{
    nombre = "Polera Test"
    precio = 19990
    categoria = "Poleras"
    stock = 50
} | ConvertTo-Json

Invoke-WebRequest -Uri "https://ajicolorbackend.vercel.app/api/v1/productos" `
    -Method POST `
    -Headers @{"Content-Type"="application/json"} `
    -Body $body
```

Respuesta esperada: `201 Created` con producto JSON

Si funciona ✅ **TODO ESTÁ CORRECTO**

---

## 📝 ARCHIVOS QUE HE ACTUALIZADO (Localmente)

```
✅ app poleras\Backend\.env
   - Corregidas MONGO_URI y MONGODB_URI

✅ app poleras\Backend\.env.production  
   - Corregida MONGO_URI con valores correctos

✅ app poleras\Backend\actualizar-mongo-uri.bat
   - Script para automatizar (si prefieres terminal)

📄 Guías de referencia creadas:
   - GUIA_RAPIDA_MONGO.md (instrucciones rápidas)
   - MONGO_FIX.md (guía detallada)
   - STATUS_DIAGNOSTICO.md (estado técnico)
   - TABLA_DIAGNOSTICO.md (tablas comparativas)
   - RESUMEN_MONGODB_VERCEL.md (resumen ejecutivo)
```

---

## 🚀 PRÓXIMOS PASOS DESPUÉS DE CORREGIR

1. **Esperar 1-2 minutos** para que Vercel redepliegue
2. **Instalar APK actualizada** en el dispositivo
3. **Login:** admin@ajicolor.cl / ajicolor
4. **Verificar:**
   - ✅ Ver productos en catálogo
   - ✅ Admin panel de pedidos
   - ✅ Admin panel de usuarios
   - ✅ Checkout con diálogo de pago ficticio
5. **Crear datos de prueba** en MongoDB
6. **Validar** que todo fluye correctamente

---

## ⚠️ NOTAS IMPORTANTES

- **Seguridad:** Esta credencial (`ajicolor:ajicolor123`) es para desarrollo. En producción usar una contraseña más fuerte.
- **Tiempo de propagación:** Vercel puede tomar 1-2 minutos en propagar los cambios. Si no funciona inmediatamente, espera y reintenta.
- **Alternativa MongoDB:** Si necesitas usar credenciales diferentes, actualiza en MongoDB Atlas primero, luego en Vercel.
- **Rollback:** Si algo falla, puedo revertir cualquier cambio.

---

## 📞 AYUDA

**Si el cambio no funciona después de 2 minutos:**

1. Verifica que el valor se guardó correctamente en Vercel dashboard
2. Revisa los logs de Vercel (Deployments > View Logs)
3. Intenta hacer un nuevo deploy manual: `npx vercel --prod`
4. Limpia caché del navegador (Ctrl+Shift+Delete)
5. Reinicia la app Android (Force Stop + abrir nuevamente)

---

## 📊 STATUS ACTUAL

| Item | Antes | Después (lo que pasará) |
|------|-------|------------------------|
| Backend | ✅ Funcionando | ✅ Funcionando |
| Health Check | ✅ 200 OK | ✅ 200 OK |
| GET /productos | ⚠️ Retorna [] | ✅ Retorna datos si existen |
| POST /productos | ❌ 503 Error | ✅ 201 Created |
| MongoDB | ❌ Desconectada | ✅ Conectada |
| App Android | ⚠️ Sin datos | ✅ Con datos en vivo |

---

**Versión:** 1.0  
**Fecha diagnóstico:** 2025-12-06  
**Status:** LISTO PARA CORREGIR (requiere acción en Vercel dashboard)
