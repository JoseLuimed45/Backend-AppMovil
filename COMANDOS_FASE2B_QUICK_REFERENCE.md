# 🚀 FASE 2B: COMANDOS RÁPIDOS PARA TESTING EN EMULADOR

Copia y pega estos comandos en tu terminal para ejecutar los tests de FASE 2B.

---

## 🔧 PREPARACIÓN DEL EMULADOR

### 1. Listar emuladores disponibles
```bash
emulator -list-avds
```

### 2. Iniciar emulador (reemplaza "Pixel_7_API_35" por tu emulador)
```bash
emulator -avd Pixel_7_API_35 &
```

### 3. Esperar a que boote completamente (verifica boot_completed = 1)
```bash
adb shell getprop sys.boot_completed
```

### 4. Verificar que ADB ve el emulador
```bash
adb devices
```

Deberías ver:
```
List of attached devices
emulator-5554           device
```

---

## 📦 INSTALAR APK

### 1. Instalar APK de debug
```bash
cd c:\Users\josel\AndroidStudioProjects\AppMovil\app_ajicolor_backend_node
adb install -r app\build\outputs\apk\debug\app-debug.apk
```

Espera a ver:
```
Success
```

---

## 🧪 TEST 1: LOGIN BÁSICO

### En Emulador:
1. Abre la app (ícono "AjiColor")
2. Ingresa credenciales:
   - Email: `admin@ajicolor.com`
   - Contraseña: `Admin123`
3. Presiona "Login"

### En Terminal (Verificar Token):
```bash
adb shell
cat /data/data/com.example.appajicolorgrupo4/shared_prefs/session_prefs.xml
```

Deberías ver algo como:
```xml
<string name="token">eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...</string>
```

---

## 🛍️ TEST 2: CARGAR PRODUCTOS

### En Emulador:
1. Si login fue exitoso, estás en HomeScreen
2. Presiona pestaña "Catálogo"
3. Debe cargar lista de productos (10-20 items)

### En Terminal (Verificar BD Local):
```bash
adb shell
sqlite3 /data/data/com.example.appajicolorgrupo4/databases/app_database.db "SELECT COUNT(*) FROM products;"
```

Deberías ver un número > 0.

---

## 🛒 TEST 3: CREAR PEDIDO

### En Emulador:
1. En Catálogo, agregar 2-3 productos al carrito (presiona "+")
2. Navega a "Carrito"
3. Presiona "Comprar"
4. Llena datos de envío:
   - Dirección: `Calle Principal 123`
   - Ciudad: `Lima`
   - CP: `15001`
5. Presiona "Confirmar Compra"

### En Terminal (Verificar Pedido en BD):
```bash
adb shell
sqlite3 /data/data/com.example.appajicolorgrupo4/databases/app_database.db "SELECT * FROM pedidos;"
```

Deberías ver el pedido que acabas de crear.

---

## 👤 TEST 4: ACTUALIZAR PERFIL

### En Emulador:
1. Presiona menú hamburguesa (≡)
2. Presiona "Mi Perfil"
3. Presiona "Editar Perfil"
4. Cambia datos:
   - Nombre: `Juan Pérez Actualizado`
   - Teléfono: `+51 999 888 777`
   - Dirección: `Av. Secundaria 456`
5. Presiona "Guardar"

### En Terminal (Verificar en BD):
```bash
adb shell
sqlite3 /data/data/com.example.appajicolorgrupo4/databases/app_database.db "SELECT nombre, telefono FROM users LIMIT 1;"
```

Deberías ver los datos actualizados.

---

## 📡 TEST 5: MODO OFFLINE

### Desconectar Internet:
```bash
adb shell
svc wifi disable
svc data disable
exit
```

### En Emulador:
- Navega a HomeScreen (debe mostrar productos cacheados)
- Intenta ir a Catálogo (debe mostrar productos cacheados)
- Intenta crear pedido (debe mostrar error de conexión)

### Reconectar Internet:
```bash
adb shell
svc wifi enable
svc data enable
exit
```

### En Emulador:
- Intenta crear pedido nuevamente
- Debe funcionar (si hay sincronización)

---

## 👥 TEST 6: REGISTRO NUEVO USUARIO

### En Emulador:
1. Logout (si está logeado)
   - Menú hamburguesa > Logout
2. En pantalla de Login, presiona "¿No tienes cuenta? Registrarse"
3. Llena formulario:
   - Nombre: `Juan Test`
   - Email: `juan.test@gmail.com`
   - Teléfono: `999 777 666`
   - Contraseña: `Test123!`
   - Dirección: `Calle Test 123`
4. Presiona "Registrarse"

### En Terminal (Verificar en MongoDB):
```bash
# Via MongoDB Atlas Web Interface:
# Database: ajicolor_db
# Collection: usuarios
# Find: { "email": "juan.test@gmail.com" }
```

---

## 👨‍💼 TEST 7: ADMIN PANEL

### En Emulador:
1. Login como admin (admin@ajicolor.com / Admin123)
2. Menú hamburguesa > "Admin Panel"
3. Presiona "Listar Usuarios" (debe mostrar todos los usuarios)
4. Presiona "Listar Pedidos" (debe mostrar todos los pedidos)
5. Presiona "Estadísticas" (debe mostrar totales y gráficos)

---

## 📊 VER LOGS EN TIEMPO REAL

### Logs generales:
```bash
adb logcat
```

### Logs filtrados (solo nuestra app):
```bash
adb logcat *:S UserRepository:D ApiService:D SafeApiCall:D
```

### Logs de HTTP:
```bash
adb logcat *:S okhttp3:D
```

### Logs de errores:
```bash
adb logcat *:E
```

### Limpiar logs:
```bash
adb logcat -c
```

---

## 🗄️ INSPECCIONAR BD LOCAL COMPLETA

### Acceder a SQLite:
```bash
adb shell
sqlite3 /data/data/com.example.appajicolorgrupo4/databases/app_database.db
```

### Dentro de SQLite, comandos útiles:
```sql
-- Ver todas las tablas
.schema

-- Ver usuarios
SELECT * FROM users;

-- Ver productos
SELECT COUNT(*) FROM products;

-- Ver pedidos
SELECT * FROM pedidos;

-- Ver items de pedidos
SELECT * FROM pedido_items;

-- Salir
.exit
```

---

## 🔐 VERIFICAR SESSION Y TOKENS

### Ver SharedPreferences (session):
```bash
adb shell
cat /data/data/com.example.appajicolorgrupo4/shared_prefs/session_prefs.xml
```

### Ver DataStore (si la app lo usa):
```bash
adb shell
cat /data/data/com.example.appajicolorgrupo4/files/datastore/preferences.pb
```

(DataStore está en binario, pero el archivo anterior muestra el token en texto)

---

## 🐛 TROUBLESHOOTING RÁPIDO

### App crashea al iniciar
```bash
adb logcat | grep FATAL
```

### Login no funciona
1. Verifica que Vercel está up:
   ```bash
   curl https://backend-app-movil.vercel.app/api/v1/usuarios/login -X POST -H "Content-Type: application/json" -d "{\"email\":\"admin@ajicolor.com\",\"password\":\"Admin123\"}"
   ```

2. Verifica credenciales en MongoDB

### Productos no cargan
```bash
curl https://backend-app-movil.vercel.app/api/v1/productos
```

### Pedido falla
```bash
adb logcat | grep "UserRepository\|SafeApiCall"
```

---

## 📝 CREAR REPORTE

Después de completar los tests, copia esto en un archivo:

```markdown
# Reporte de Testing FASE 2B

**Fecha:** [Hoy]
**Dispositivo:** Emulador Pixel 7 API 35
**APK:** app-debug.apk (v1.0.0)

## Resultados

| Test | Resultado | Duración | Notas |
|------|-----------|----------|-------|
| 1. Login | ✅ | 2.5s | Token guardado correctamente |
| 2. Productos | ✅ | 1.2s | 15 productos cargados |
| 3. Pedido | ✅ | 3.1s | Guardado en BD local |
| 4. Perfil | ✅ | 2.8s | Datos actualizados |
| 5. Offline | ✅ | N/A | Cachéa correctamente |
| 6. Registro | ✅ | 4.2s | Usuario creado en MongoDB |
| 7. Admin | ✅ | 2.0s | Listar usuarios y pedidos OK |

## Conclusión

✅ **FASE 2B COMPLETADA EXITOSAMENTE**

La app está lista para:
- Instalar en dispositivos reales
- Generar APK release
- Subir a Google Play (si aplica)
```

---

## ✨ CHECKLIST FINAL

- [ ] Emulador está corriendo
- [ ] APK instalado sin errores
- [ ] Test 1 (Login) ✅
- [ ] Test 2 (Productos) ✅
- [ ] Test 3 (Pedido) ✅
- [ ] Test 4 (Perfil) ✅
- [ ] Test 5 (Offline) ✅
- [ ] Test 6 (Registro) ✅
- [ ] Test 7 (Admin) ✅
- [ ] Reporte creado ✅
- [ ] Todos los tests en < 5s ✅

---

## 🎯 PRÓXIMO PASO

Una vez completada FASE 2B:

**FASE 3: Generación de APK Release (sin debug)**

```bash
cd c:\Users\josel\AndroidStudioProjects\AppMovil\app_ajicolor_backend_node
gradlew assembleRelease
# APK estará en: app/build/outputs/apk/release/app-release.apk
```

---

## 💡 TIPS

1. **Para tests rápidos**, crea 2-3 usuarios de prueba en MongoDB
2. **Antes de cada test**, limpia logs: `adb logcat -c`
3. **Si algo falla**, siempre verifica: logs → BD → API (en ese orden)
4. **Timeouts**, no esperes > 10s en emulador (probablemente es un cold start)
5. **Offline**, la app debe al menos no crashear (sincronización es nice-to-have)

