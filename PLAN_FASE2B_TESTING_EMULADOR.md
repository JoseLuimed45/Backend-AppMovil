# 🧪 PLAN FASE 2B: Testing en Emulador (Retrofit + API + Base Datos Local)

**Objetivo:** Validar que la conexión a Vercel funciona correctamente, que los datos se sincronizan con la BD local, y que el flujo de autenticación es seguro.

---

## 📋 PRE-REQUISITOS

### Antes de Empezar

- ✅ APK compilado: `app/build/outputs/apk/debug/app-debug.apk` (40 MB)
- ✅ Emulador instalado (Android 14 o superior recomendado)
- ✅ Backend Vercel corriendo: `https://backend-app-movil.vercel.app`
- ✅ MongoDB Atlas activo (datos de prueba cargados)

---

## 1️⃣ PREPARAR EMULADOR

### Paso 1: Crear/Iniciar Emulador

```bash
# Listar dispositivos virtuales
emulator -list-avds

# Iniciar emulador (ej: Pixel 7)
emulator -avd Pixel_7_API_35 &

# Esperar a que boote completamente (2-3 minutos)
adb shell getprop sys.boot_completed
# Cuando salga "1", está listo
```

### Paso 2: Verificar Conectividad

```bash
# Verificar que adb ve el emulador
adb devices

# Debería mostrar:
# List of attached devices
# emulator-5554           device
```

### Paso 3: Instalar APK

```bash
# Instalar app de debug
adb install -r app\build\outputs\apk\debug\app-debug.apk

# Esperar mensaje:
# Success
```

---

## 2️⃣ TEST 1: LOGIN BÁSICO

**Objetivo:** Verificar que login funciona y guarda token en SharedPreferences.

### Pasos en Emulador:

1. **Abrir la app**
   - La app mostrará pantalla de login
   - Debe verse la UI correctamente

2. **Ingresar credenciales de prueba**
   ```
   Email: admin@ajicolor.com
   Contraseña: Admin123
   ```

3. **Presionar "Login"**
   - El botón debe mostrar spinner (cargando)
   - Después de 2-5 segundos debe navegar a HomeScreen

4. **Verificar que token se guardó**
   ```bash
   adb shell
   # Dentro del shell del emulador:
   cat /data/data/com.example.appajicolorgrupo4/shared_prefs/session_prefs.xml
   ```
   Deberías ver:
   ```xml
   <string name="token">eyJhbGc...</string>
   ```

### ✅ Test Exitoso Si:
- Login responde en < 5 segundos
- Navega a HomeScreen
- Token se guarda en SharedPreferences
- No hay crashes

### 🔴 Troubleshooting:

| Error | Solución |
|-------|----------|
| "Error de conexión" | Verificar que Vercel está up: `curl https://backend-app-movil.vercel.app/health` |
| "Email o contraseña incorrecta" | Verificar credenciales en MongoDB |
| Timeout después de 90s | Vercel cold start, reintentar |

---

## 3️⃣ TEST 2: CARGAR PRODUCTOS

**Objetivo:** Verificar que la API de productos funciona y se cachea localmente.

### Pasos en Emulador:

1. **En HomeScreen, navegar a Catálogo**
   - Presionar pestaña "Catálogo" o botón de productos
   - Debe mostrar lista de productos

2. **Verificar que se cargan en < 3 segundos**
   - Si tarda > 5s, es probablemente un cold start en Vercel
   - Usa emulador con conexión a internet estable

3. **Verificar contenido de productos**
   - Nombre ✅
   - Precio ✅
   - Imagen ✅
   - Stock ✅

4. **Verificar que se cachean localmente**
   ```bash
   adb shell
   sqlite3 /data/data/com.example.appajicolorgrupo4/databases/app_database.db
   
   # Dentro de sqlite3:
   SELECT * FROM products LIMIT 5;
   ```

   Deberías ver productos guardados en BD local.

### ✅ Test Exitoso Si:
- Lista de productos carga en < 5 segundos
- Se muestran al menos 10 productos
- Imágenes se cargan correctamente
- Si das refresh, se actualiza desde API

### 🔴 Troubleshooting:

| Error | Solución |
|-------|----------|
| "No hay productos" | Verificar que existen en MongoDB: `curl https://backend-app-movil.vercel.app/api/v1/productos` |
| Imágenes no cargan | Verificar URLs de Cloudinary en MongoDB |
| Muy lento (> 10s) | Vercel cold start, o emulador lento |

---

## 4️⃣ TEST 3: CREAR CARRITO Y PEDIDO

**Objetivo:** Verificar que se puede crear un pedido y guardarlo en BD local.

### Pasos en Emulador:

1. **En Catálogo, agregar 2-3 productos al carrito**
   - Presionar botón "+" en cada producto
   - Carrito debe actualizarse (mostrar cantidad)

2. **Navegar a Carrito**
   - Ver resumen de productos
   - Ver total calculado correctamente

3. **Proceder a checkout**
   - Presionar "Comprar" o "Checkout"
   - Debe ir a formulario de datos de envío

4. **Llenar datos de envío**
   ```
   Dirección: Calle Principal 123
   Ciudad: Lima
   Código Postal: 15001
   ```

5. **Confirmar compra**
   - Debe enviar POST a `/api/v1/pedidos`
   - Esperar respuesta del backend (2-5s)
   - Debe mostrar confirmación

6. **Verificar en BD local**
   ```bash
   adb shell
   sqlite3 /data/data/com.example.appajicolorgrupo4/databases/app_database.db
   
   # Dentro de sqlite3:
   SELECT * FROM pedidos;
   SELECT * FROM pedido_items;
   ```

### ✅ Test Exitoso Si:
- Pedido se crea sin errores
- Total se calcula correctamente
- Respuesta llega en < 5 segundos
- Pedido se guarda en BD local
- Obtiene número de pedido de confirmación

### 🔴 Troubleshooting:

| Error | Solución |
|-------|----------|
| "Error al crear pedido" | Ver logs: `adb logcat \| grep UserRepository` |
| Pedido no guarda en BD local | Verificar que UserRepository llama a `pedidoDao.insert()` |
| No obtiene número de pedido | Verificar respuesta del backend en logs |

---

## 5️⃣ TEST 4: PERFIL Y ACTUALIZACIÓN

**Objetivo:** Verificar que se puede actualizar perfil de usuario.

### Pasos en Emulador:

1. **Navegar a Perfil (hamburguesa > Perfil)**
   - Debe mostrar datos del usuario logeado

2. **Presionar "Editar Perfil"**
   - Campos deben ser editables

3. **Cambiar datos**
   ```
   Nombre: Juan Pérez Actualizado
   Teléfono: +51 999 888 777
   Dirección: Av. Secundaria 456, Lima
   ```

4. **Presionar "Guardar"**
   - Debe enviar PUT a `/api/v1/usuarios/{id}`
   - Esperar 2-3 segundos
   - Debe mostrar "Perfil actualizado"

5. **Verificar en BD local**
   ```bash
   adb shell
   sqlite3 /data/data/com.example.appajicolorgrupo4/databases/app_database.db
   
   SELECT * FROM users;
   ```

### ✅ Test Exitoso Si:
- Datos se actualizan sin errores
- Respuesta llega en < 5 segundos
- Token se renueva (si el backend lo retorna)
- Datos persistidos en BD local
- Cambios visibles al cerrar y reabrir app

### 🔴 Troubleshooting:

| Error | Solución |
|-------|----------|
| "Error al actualizar" | Ver respuesta del servidor en logs |
| Datos no se actualizan localmente | Verificar que `userDao.update()` se llama |
| Token expirado | Token tiene expiration, verificar que se renueva |

---

## 6️⃣ TEST 5: FLUJO OFFLINE

**Objetivo:** Verificar que la app funciona sin internet (datos cacheados).

### Pasos:

1. **Con app funcionando normalmente, desconectar internet**
   ```bash
   adb shell
   svc wifi disable
   svc data disable
   ```

2. **Navegar en la app**
   - HomeScreen debe mostrar productos cacheados ✅
   - Perfil debe mostrar datos del usuario ✅
   - Carrito debe funcionar ✅

3. **Intentar crear pedido**
   - Debe mostrar error: "Sin conexión"
   - O permitir crear offline y sincronizar después

4. **Volver a conectar**
   ```bash
   svc wifi enable
   svc data enable
   ```

5. **Verificar que se sincroniza**
   - Si hay pedidos pendientes, deben enviarse a backend
   - Respuesta debe confirmarse

### ✅ Test Exitoso Si:
- App no crashea sin conexión
- Datos cacheados se muestran
- Intentar comprar muestra error amigable
- Al volver online, se sincroniza

---

## 7️⃣ TEST 6: REGISTRO DE NUEVO USUARIO

**Objetivo:** Verificar que registro funciona con sincronización a BD local.

### Pasos:

1. **En Login, presionar "Registrarse"**
   - Debe ir a RegistroScreen

2. **Llenar formulario**
   ```
   Nombre: Juan Nuevo
   Email: juan.nuevo@gmail.com
   Teléfono: +51 999 777 666
   Contraseña: Temporal123!
   Dirección: Calle Test 789
   ```

3. **Presionar "Registrarse"**
   - Debe enviar POST a `/api/v1/usuarios/register`
   - Esperar 3-5 segundos
   - Debe guardar usuario localmente
   - Debe logearlo automáticamente

4. **Verificar en MongoDB**
   ```bash
   # En MongoDB Atlas (Web):
   # db.usuarios.findOne({ email: "juan.nuevo@gmail.com" })
   ```

5. **Verificar en BD local**
   ```bash
   adb shell
   sqlite3 /data/data/com.example.appajicolorgrupo4/databases/app_database.db
   
   SELECT * FROM users WHERE correo = 'juan.nuevo@gmail.com';
   ```

### ✅ Test Exitoso Si:
- Registro responde en < 5 segundos
- Usuario aparece en MongoDB ✅
- Usuario aparece en BD local ✅
- App loguea automáticamente el nuevo usuario
- Puede navegar a HomeScreen

---

## 8️⃣ TEST 7: ADMIN PANEL

**Objetivo:** Verificar que admin puede listar usuarios y pedidos.

### Pasos:

1. **Logearse con usuario admin**
   ```
   Email: admin@ajicolor.com
   Contraseña: Admin123
   ```

2. **Navegar a Admin Panel**
   - Si el rol es "admin", debe mostrar menú admin
   - Presionar "Listar Usuarios"

3. **Verificar listado de usuarios**
   - Debe cargar todos los usuarios de `/api/v1/admin/usuarios`
   - Mostrar nombre, email, teléfono

4. **Presionar "Listar Pedidos"**
   - Debe cargar todos los pedidos de `/api/v1/admin/pedidos`
   - Mostrar número, usuario, total, estado

5. **Presionar "Estadísticas"**
   - Debe cargar de `/api/v1/admin/estadisticas`
   - Mostrar: total usuarios, total pedidos, ingresos, etc.

### ✅ Test Exitoso Si:
- Admin panel es accesible ✅
- Usuarios cargan sin errores ✅
- Pedidos cargan sin errores ✅
- Estadísticas se calculan correctamente ✅

---

## 📊 MATRIZ DE TESTING

| Test | Endpoint | Esperado | Status |
|------|----------|----------|--------|
| 1. Login | POST `/api/v1/usuarios/login` | 200 + token | ⬜ |
| 2. Productos | GET `/api/v1/productos` | 200 + lista | ⬜ |
| 3. Crear Pedido | POST `/api/v1/pedidos` | 201 + id | ⬜ |
| 4. Actualizar Perfil | PUT `/api/v1/usuarios/{id}` | 200 + token | ⬜ |
| 5. Offline | (Sin conexión) | Sin error | ⬜ |
| 6. Registro | POST `/api/v1/usuarios/register` | 201 + usuario | ⬜ |
| 7. Admin | GET `/api/v1/admin/*` | 200 + datos | ⬜ |

---

## 🔍 COMANDOS ÚTILES DURANTE TESTING

### Ver Logs en Tiempo Real

```bash
# Logs de toda la app
adb logcat

# Logs filtrados por tag
adb logcat *:S UserRepository:D ApiService:D SafeApiCall:D

# Logs de crashes
adb logcat *:E
```

### Inspeccionar BD Local

```bash
adb shell
sqlite3 /data/data/com.example.appajicolorgrupo4/databases/app_database.db

# Dentro de sqlite3:
.schema users
SELECT * FROM users;
SELECT * FROM products;
SELECT * FROM pedidos;
SELECT * FROM pedido_items;
.exit
```

### Inspeccionar SharedPreferences

```bash
adb shell
cat /data/data/com.example.appajicolorgrupo4/shared_prefs/session_prefs.xml

# Para verificar que token está guardado
grep -o "token.*<" session_prefs.xml
```

### Inspeccionar Tráfico HTTP

```bash
# En RetrofitInstance, ya hay HttpLoggingInterceptor
# Los logs mostrarán todas las peticiones en BODY level:
adb logcat *:S okhttp3:D
```

---

## ⚠️ NOTAS IMPORTANTES

1. **Cold Starts en Vercel**
   - Primera petición a un endpoint sin tráfico tardará 2-5 segundos
   - RetrofitInstance hace reintentos automáticos
   - Esto es normal, no es un error

2. **Modo Offline**
   - No todas las apps soportan offline perfecto
   - Verifica que la app al menos no crashea
   - Los datos cacheados deben mostrarse

3. **Timeouts**
   - Configurados a 90 segundos (muy generosos)
   - En emulador, si falla, espera a retry automático
   - Si persiste > 3 intentos, hay problema real

4. **Performance**
   - En emulador pueden ser lentos
   - En dispositivo real será más rápido (10-20% más)
   - No juzgues performance en emulador

---

## 📝 REPORTE DE TESTING

Crea un archivo `REPORTE_TESTING_EMULADOR.md` con estos datos:

```markdown
# Reporte de Testing en Emulador

| Test | Resultado | Duración | Notas |
|------|-----------|----------|-------|
| 1. Login | ✅/❌ | 2.5s | OK |
| 2. Productos | ✅/❌ | 1.2s | OK |
| 3. Pedido | ✅/❌ | 3.1s | OK |
| 4. Perfil | ✅/❌ | 2.8s | OK |
| 5. Offline | ✅/❌ | N/A | OK |
| 6. Registro | ✅/❌ | 4.2s | OK |
| 7. Admin | ✅/❌ | 2.0s | OK |

**Conclusión:** ✅ LISTO PARA PRODUCCIÓN
```

---

## 🎯 CHECKSUM FINAL

Antes de dar por completada FASE 2B, verifica:

- [ ] APK instalado en emulador sin errores
- [ ] Login funciona con credenciales válidas
- [ ] Productos cargan desde API
- [ ] Pedido se crea y guarda localmente
- [ ] Perfil se actualiza correctamente
- [ ] App no crashea sin conexión
- [ ] Registro de nuevo usuario funciona
- [ ] Admin panel accesible para admin
- [ ] Todos los tests en < 5 segundos
- [ ] Logs muestran llamadas correctas a API

Si todo ✅, **FASE 2B completada. Proceder a FASE 3: Generación de APK Release.**

---

## ✨ RESUMEN

**FASE 2B es el puente entre código compilado y app funcionando.**

Aquí validamos:
- Conectividad a Vercel ✅
- Sincronización con BD local ✅
- Manejo de errores ✅
- Flujos de usuario completos ✅

**Duración estimada:** 1-2 horas  
**Complejidad:** Media (requiere emulador y paciencia)  
**Riesgo:** Bajo (no modifica código, solo verifica)

