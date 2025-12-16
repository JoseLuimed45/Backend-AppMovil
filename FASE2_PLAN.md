# 🚀 FASE 2: Testing de Capa de Datos - Plan Detallado

**Objetivo:** Validar Retrofit + MongoDB + Room + Sincronización Offline

## Arquitectura de Pruebas

```
┌─────────────────────────────────────────────────────────────┐
│                    Android App (APK)                         │
├─────────────────────────────────────────────────────────────┤
│  UI Layer (Compose)                                          │
│    ↓                                                          │
│  ViewModel Layer                                              │
│    ↓                                                          │
│  Repository Pattern (con sincronización)                     │
│    ├─→ Retrofit (Remote: Vercel/Node.js)                    │
│    └─→ Room (Local: SQLite)                                 │
│         ↓                                                     │
│  DataStore (SessionManager)                                  │
├─────────────────────────────────────────────────────────────┤
│  Red                   MongoDB Atlas (Vercel Backend)        │
│  HTTPS →  https://backend-app-movil.vercel.app/api/v1/  → MongoDB
└─────────────────────────────────────────────────────────────┘
```

## Test Suite Propuesto

### A. Pruebas de Conectividad Retrofit

#### Test 1: Health Check
```bash
GET https://backend-app-movil.vercel.app/health
Response:
{
  "status": "ok",
  "database": "connected",
  "timestamp": "2025-12-15T21:00:00.000Z"
}
```
**Objetivo:** Validar que backend está disponible

#### Test 2: Login Válido
```bash
POST https://backend-app-movil.vercel.app/api/v1/auth/login
{
  "correo": "admin@ajicolor.com",
  "contrasena": "Admin123"
}
Response:
{
  "token": "eyJhbGc...",
  "usuario": {
    "id": "...",
    "nombre": "Admin",
    "correo": "admin@ajicolor.com",
    "rol": "admin",
    ...
  }
}
```
**Objetivo:** Validar AuthInterceptor + JWT storage

#### Test 3: Login Inválido
```bash
POST https://backend-app-movil.vercel.app/api/v1/auth/login
{
  "correo": "admin@ajicolor.com",
  "contrasena": "WRONG_PASSWORD"
}
Response: 401 Unauthorized
{
  "error": "Credenciales inválidas"
}
```
**Objetivo:** Validar error handling

#### Test 4: Obtener Perfil (Autenticado)
```bash
GET https://backend-app-movil.vercel.app/api/v1/usuarios/<ID>
Headers: Authorization: Bearer <TOKEN>
Response: 200 User DTO
```
**Objetivo:** Validar JWT en headers + resolver referencias

---

### B. Pruebas de Room Database (Local)

#### Test 5: CRUD de Usuarios
```kotlin
// Insert
userDao.insert(UserEntity(
    id = "test-1",
    nombre = "Test User",
    correo = "test@mail.com",
    ...
))
// Expected: Insert exitoso, id único

// Read
val user = userDao.getById("test-1")
// Expected: UserEntity con datos completos

// Update
userDao.update(user.copy(nombre = "Updated Name"))
// Expected: Campo actualizado

// Delete
userDao.delete(user)
// Expected: Registro eliminado

// Query
val users = userDao.getAll()
// Expected: List<UserEntity>
```

#### Test 6: Relaciones Pedido-Usuario
```kotlin
// PedidoEntity con referencia a User
val pedido = PedidoEntity(
    id = "order-1",
    usuarioId = "user-1", // FK
    numeroPedido = "ORD-001",
    total = 150.0,
    estado = "pendiente",
    ...
)

// Insert order
pedidoDao.insert(pedido)

// Recuperar con items
val pedidoConItems = pedidoDao.getPedidoWithItems("order-1")
// Expected: PedidoWithItems(pedido, items: List<PedidoItemEntity>)
```

---

### C. Pruebas de Sincronización

#### Test 7: Offline → Online Sync
```
Escenario:
1. App sin conexión (airplane mode)
2. Usuario actualiza perfil localmente (Room)
3. App se conecta a internet
4. Trigger automático de sincronización
5. Datos se propagan a MongoDB

Validar:
✅ LocalDb actualizada
✅ Retrofit call realizado
✅ MongoDB refleja cambios
✅ SessionManager tiene token válido
```

#### Test 8: Conflictos de Concurrencia
```
Escenario:
1. Usuario A modifica registro X en local
2. Usuario B modifica registro X en servidor
3. App intenta sync de Usuario A

Validar:
✅ Timestamp comparison (último gana)
✅ Log de conflicto en audit
✅ UI notifica al usuario
```

---

### D. Pruebas de SessionManager & AuthInterceptor

#### Test 9: Token Persistence
```kotlin
sessionManager.saveToken("token-123")
val token = sessionManager.getToken()
// Expected: "token-123"

sessionManager.saveUserId("user-456")
val userId = sessionManager.getUserId()
// Expected: "user-456"
```

#### Test 10: Token en Headers
```
GET /api/v1/usuarios/<ID>
Interceptor debe agregar automáticamente:
Authorization: Bearer <TOKEN>

Validar:
✅ Token incluido en header
✅ Si token vacío → error 401
✅ Si token expirado → error 401 + clear token
```

---

## Test Cases por Pantalla

### Pantalla de Login (InitScreen)
```
PASO 1: Input válido
Input: admin@ajicolor.com / Admin123
Expected: 
  ✅ Llamada a POST /auth/login
  ✅ Token guardado en SessionManager
  ✅ Usuario guardado en Room
  ✅ Navegación a HomeScreen

PASO 2: Input inválido
Input: admin@ajicolor.com / WRONG
Expected:
  ❌ Error message mostrado
  ❌ Token no guardado
  ❌ Usuario no guardado
  ❌ Sin navegación

PASO 3: Sin conexión
Setup: Airplane mode
Input: admin@ajicolor.com / Admin123
Expected:
  ❌ Timeout error
  ❌ Snackbar con "Sin conexión"
```

### Pantalla de Catálogo (CatalogoProductosScreen)
```
PASO 1: Cargar productos
Setup: Conectado
Expected:
  ✅ GET /productos llamado
  ✅ Productos mostrados en LazyColumn
  ✅ Imágenes cargan vía Coil

PASO 2: Buscar producto
Input: "ají rojo"
Expected:
  ✅ Lista filtrada por nombre/descripción
  ✅ SearchBar funciona en tiempo real

PASO 3: Agregar al carrito
Input: Click en + de producto
Expected:
  ✅ Producto agregado a carrito (Room)
  ✅ Badge de cantidad actualizado
  ✅ Confirmación visual
```

### Pantalla de Carrito (CartScreen)
```
PASO 1: Ver artículos
Expected:
  ✅ Productos del carrito listados
  ✅ Cantidades correctas
  ✅ Total calculado correctamente

PASO 2: Modificar cantidad
Input: Incrementar un producto
Expected:
  ✅ Room actualizado
  ✅ Total recalculado
  ✅ UI refleja cambios

PASO 3: Checkout
Input: Click en "Pagar"
Expected:
  ✅ Crear PedidoEntity en Room
  ✅ POST /pedidos enviado a MongoDB
  ✅ Clear carrito local
  ✅ Navegación a confirmación
```

### Pantalla de Perfil (ProfileScreen)
```
PASO 1: Cargar datos
Expected:
  ✅ Datos del usuario traídos de Room/API
  ✅ Mostrados en formulario

PASO 2: Editar perfil
Input: Modificar nombre, teléfono
Expected:
  ✅ Cambios guardados en Room
  ✅ PUT /usuarios/<id> enviado
  ✅ Confirmación: "Perfil actualizado"

PASO 3: Logout
Input: Click en "Cerrar sesión"
Expected:
  ✅ Token eliminado de SessionManager
  ✅ Datos locales mantenidos (por si login rápido)
  ✅ Navegación a InitScreen
```

---

## Herramientas de Testing

### 1. Emulador Android
```bash
# Listar AVDs disponibles
emulator -list-avds

# Crear si no existe
avdmanager create avd -n Pixel4_API30 -k "system-images;android-30;default;arm64-v8a"

# Lanzar
emulator -avd Pixel4_API30 -writable-system

# Instalar APK
adb install -r app/build/outputs/apk/debug/app-debug.apk

# Activar airplane mode (pruebas offline)
adb shell settings put global airplane_mode_on 1
adb shell am broadcast -a android.intent.action.AIRPLANE_MODE --ez state true

# Ver logs
adb logcat -s "AppMovil:I"
```

### 2. API Testing (Postman / cURL)
```bash
# Test login desde terminal
curl -X POST https://backend-app-movil.vercel.app/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "correo": "admin@ajicolor.com",
    "contrasena": "Admin123"
  }'

# Test con token
curl https://backend-app-movil.vercel.app/api/v1/usuarios/123 \
  -H "Authorization: Bearer YOUR_TOKEN"
```

### 3. MongoDB Atlas Monitoring
```
Dashboard: https://cloud.mongodb.com
Colecciones a validar:
  ✅ users (después de login/register)
  ✅ products (después de GET /productos)
  ✅ pedidos (después de checkout)
  ✅ audit_logs (rastreo de actividades)
```

---

## Checklist de FASE 2

### Semana 1: Setup & Conectividad
- [ ] Emulador instalado y funcionando
- [ ] APK instalado en emulador
- [ ] App abre sin crashes
- [ ] Health check pasa (backend disponible)
- [ ] Login funciona con admin@ajicolor.com

### Semana 2: Room CRUD & Sincronización
- [ ] UserDao CRUD completo
- [ ] PedidoDao con relaciones
- [ ] Inserción de usuarios desde API
- [ ] Sincronización offline → online

### Semana 3: Features de Usuario
- [ ] Catálogo de productos carga
- [ ] Búsqueda de productos funciona
- [ ] Carrito persiste en Room
- [ ] Checkout crea orden en MongoDB

### Semana 4: Admin & Refinamiento
- [ ] Panel admin lista usuarios
- [ ] Panel admin lista pedidos
- [ ] Panel admin ver estadísticas
- [ ] Bugs & optimizaciones

---

## Métricas de Éxito

| Métrica | Objetivo | Status |
|---------|----------|--------|
| API Response Time | < 500ms | ⏳ |
| DB Query Time | < 100ms | ⏳ |
| Sync Success Rate | > 99% | ⏳ |
| Crash-free Rate | > 99% | ⏳ |
| Offline Capability | 100% CRUD | ⏳ |

---

## Documentación Generada

- [x] FASE1_COMPLETADO.md ← Estás aquí
- [ ] FASE2_TESTING.md (Tests detallados)
- [ ] FASE3_OPTIMIZATION.md (Performance)
- [ ] API_CONTRACTS.md (Endpoints validados)

---

**Próximo Paso:**
```bash
# 1. Instalar emulador
emulator -list-avds

# 2. Lanzar emulador
emulator -avd <AVD_NAME>

# 3. Instalar APK
adb install -r app/build/outputs/apk/debug/app-debug.apk

# 4. Ejecutar app
adb shell am start -n com.example.appajicolorgrupo4/.ui.MainActivity
```

**Backend Status:** https://backend-app-movil.vercel.app/health
