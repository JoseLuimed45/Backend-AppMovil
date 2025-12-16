# 🧪 GUÍA DE TESTS END-TO-END - Vercel Production

## 📋 RESUMEN

Script completo para validar todos los endpoints en producción (Vercel) con tests automatizados.

---

## 🚀 COMANDOS DE EJECUCIÓN

### 1. Test Completo con URL por Defecto

```powershell
cd C:\Users\josel\AndroidStudioProjects\AppMovil\app_ajicolor_backend_node
.\test-vercel-complete.ps1
```

**URL por defecto:** `https://backend-app-movil.vercel.app`

### 2. Test con URL Personalizada

```powershell
.\test-vercel-complete.ps1 -BaseUrl "https://tu-dominio-custom.vercel.app"
```

### 3. Test y Guardar Log

```powershell
.\test-vercel-complete.ps1 | Tee-Object -FilePath "test-results-$(Get-Date -Format 'yyyyMMdd-HHmmss').log"
```

---

## ✅ TESTS EJECUTADOS (10 TOTAL)

| # | Test | Endpoint | Método | Auth |
|---|------|----------|--------|------|
| 1 | Health Check | `/api/health/status` | GET | No |
| 2 | Login Admin | `/api/v1/usuarios/login` | POST | No |
| 3 | Listar Productos | `/api/v1/productos` | GET | No |
| 4 | Actualizar Perfil | `/api/v1/usuarios/:id` | PUT | Sí |
| 5 | Crear Pedido | `/api/v1/pedidos` | POST | Sí |
| 6 | Pedidos Usuario | `/api/v1/pedidos/usuario/:id` | GET | Sí |
| 7 | Admin: Usuarios | `/api/v1/admin/usuarios` | GET | Admin |
| 8 | Admin: Pedidos | `/api/v1/admin/pedidos` | GET | Admin |
| 9 | Admin: Estadísticas | `/api/v1/admin/estadisticas` | GET | Admin |
| 10 | Rate Limiting | `/api/v1/productos` | GET | No |

---

## 📊 RESULTADO ESPERADO

### Salida Exitosa (≥90%)

```
========================================
   RESUMEN DE TESTS
========================================

Tests Ejecutados:  10
Tests Exitosos:    10
Tests Fallidos:    0
Tasa de Éxito:     100%

========================================
✓ SISTEMA OPERATIVO - Todos los tests críticos pasaron
```

### Códigos de Salida

- **0**: Éxito ≥90%
- **1**: Parcial 70-89%
- **2**: Fallo <70%

---

## 🔍 INTERPRETACIÓN DE RESULTADOS

### ✅ Test 1: Health Check

**Esperado:**
```json
{
  "status": "OK",
  "db": "connected",
  "jwtSecretValid": true
}
```

**Si falla:**
- ❌ Backend no desplegado
- ❌ MongoDB no conecta
- ❌ JWT_SECRET inválido

### ✅ Test 2: Login

**Esperado:**
```json
{
  "_id": "675...",
  "token": "eyJhbGci...",
  "rol": "ADMIN"
}
```

**Si falla:**
- ❌ Usuario admin no existe
- ❌ Password incorrecta
- ❌ Base de datos incorrecta

### ✅ Test 3: Productos

**Esperado:** Array con productos `[{...}, {...}]`

**Si falla:**
- ❌ Colección products vacía
- ❌ Permisos incorrectos

### ✅ Test 4: Actualizar Perfil

**Esperado:**
```json
{
  "token": "nuevo_token...",
  "user": { "nombre": "...", ... }
}
```

**Si falla:**
- ❌ mongoId incorrecto
- ❌ Token inválido
- ❌ PUT no implementado

### ✅ Test 5: Crear Pedido

**Esperado:**
```json
{
  "numeroPedido": "TEST-...",
  "total": 16900,
  "estado": "CONFIRMADO"
}
```

**Si falla:**
- ❌ Validación de datos
- ❌ Usuario no válido
- ❌ Productos no existen

### ✅ Tests 7-9: Admin Endpoints

**Si fallan:**
- ❌ Usuario no es ADMIN
- ❌ Middleware admin no funciona
- ❌ Token expirado

---

## 🔧 TROUBLESHOOTING

### Error: "Login falló - Status 404"

**Causa:** Vercel no actualizado

**Solución:**
```
1. Vercel Dashboard → Redeploy
2. Esperar 2-3 minutos
3. Re-ejecutar tests
```

### Error: "Health check falló"

**Causa:** Backend no accesible

**Solución:**
```
1. Verificar URL correcta
2. Verificar Vercel deployment activo
3. Check DNS propagation
```

### Error: "Perfil actualizado pero sin token"

**Causa:** Backend no devuelve token renovado

**Solución:**
```
1. Verificar authController.js actualizado
2. Verificar que PUT devuelve { token, user }
```

### Tests Fallan después de pasar

**Causa:** Rate limiting activo

**Solución:**
```
Esperar 10 minutos y re-ejecutar
```

---

## 🧪 TESTS ADICIONALES

### Test Manual con curl

```bash
# Health
curl https://backend-app-movil.vercel.app/api/health/status

# Login
curl -X POST https://backend-app-movil.vercel.app/api/v1/usuarios/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@ajicolor.com","password":"admin123"}'
```

### Test de Performance

```powershell
# Medir tiempo de respuesta
Measure-Command { .\test-vercel-complete.ps1 }
```

---

## 📋 CHECKLIST PRE-TEST

Antes de ejecutar tests, verificar:

- [ ] Vercel desplegado con código actualizado
- [ ] MongoDB URI correcta (BDAjicolor o DBAppMovil)
- [ ] Password MongoDB actualizada
- [ ] Variables de entorno configuradas:
  - [ ] MONGO_URI
  - [ ] JWT_SECRET (>=16 chars)
  - [ ] NODE_ENV=production
- [ ] Usuario admin existe en MongoDB
- [ ] Al menos 1 producto en la base de datos

---

## 📊 MÉTRICAS DE ÉXITO

| Métrica | Objetivo | Crítico |
|---------|----------|---------|
| Tasa de éxito | ≥90% | ≥70% |
| Health check | ✅ OK | ✅ |
| Login | ✅ 200 | ✅ |
| PUT perfil | ✅ 200 | ✅ |
| Crear pedido | ✅ 201 | ✅ |
| Admin endpoints | ✅ 200 | ⚠️ |

---

## 🔄 AUTOMATIZACIÓN CI/CD

### GitHub Actions

```yaml
name: E2E Tests

on:
  push:
    branches: [ main ]
  schedule:
    - cron: '0 */6 * * *'  # Cada 6 horas

jobs:
  test:
    runs-on: windows-latest
    steps:
      - uses: actions/checkout@v2
      - name: Run E2E Tests
        run: |
          cd app_ajicolor_backend_node
          .\test-vercel-complete.ps1
        env:
          BASE_URL: https://backend-app-movil.vercel.app
```

---

## 📝 LOGS Y DEBUGGING

### Ver logs detallados

```powershell
$VerbosePreference = "Continue"
.\test-vercel-complete.ps1
```

### Guardar logs con timestamp

```powershell
$logFile = "test-results-$(Get-Date -Format 'yyyyMMdd-HHmmss').log"
.\test-vercel-complete.ps1 *>&1 | Tee-Object -FilePath $logFile
Write-Host "`nLog guardado en: $logFile"
```

### Analizar logs

```powershell
# Ver solo errores
Get-Content $logFile | Select-String "✗"

# Ver resumen
Get-Content $logFile | Select-String "RESUMEN" -Context 0,10
```

---

## 🎯 PRÓXIMOS PASOS

Después de ejecutar tests:

### Si Tasa ≥90%:
```
✅ Sistema operativo
✅ Instalar APK
✅ Probar desde app móvil
✅ Crear pedido real
✅ Verificar en MongoDB
```

### Si Tasa <90%:
```
1. Ver logs de tests fallidos
2. Identificar patrón de fallos
3. Consultar documentación troubleshooting
4. Corregir problemas
5. Re-ejecutar tests
```

---

## 📚 ARCHIVOS RELACIONADOS

- `test-vercel-complete.ps1` - Script principal
- `CONFIGURACION_BASES_DATOS_AMBIENTE.md` - Config DB
- `INSTRUCCIONES_VERCEL_MANUAL.md` - Deploy Vercel
- `LIMPIEZA_CODIGO_COMPLETADA.md` - Arquitectura

---

**Última actualización:** 15 Diciembre 2025  
**Versión del script:** 1.0  
**Compatible con:** PowerShell 5.1+

