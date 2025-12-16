# 🎬 GUÍA RÁPIDA: Próximos Pasos (FASE 2)

**Tu APK está listo:** `app/build/outputs/apk/debug/app-debug.apk`

---

## OPCIÓN A: Instalar en Emulador (Recomendado)

### Paso 1: Verificar si tienes emulador
```cmd
emulator -list-avds
```

**Si sí tienes (copiar nombre):**
```cmd
emulator -avd TU_AVD_NAME
```

**Si NO tienes, crear uno:**
```cmd
:: Crear emulador Pixel 4 con API 34 (Android 14)
avdmanager create avd ^
  -n Pixel4_API34 ^
  -k "system-images;android-34;default;arm64-v8a" ^
  -d pixel_4
```

### Paso 2: Lanzar emulador (en terminal separada)
```cmd
emulator -avd Pixel4_API34
```

(Espera 30-60 segundos hasta que inicie completamente)

### Paso 3: Instalar APK
```cmd
cd c:\Users\josel\AndroidStudioProjects\AppMovil\app_ajicolor_backend_node
adb install -r app\build\outputs\apk\debug\app-debug.apk
```

**Resultado esperado:**
```
Success
```

### Paso 4: Lanzar app
```cmd
adb shell am start -n com.example.appajicolorgrupo4/.ui.MainActivity
```

### Paso 5: Ver logs
```cmd
adb logcat | grep -i "appajicolor"
```

---

## OPCIÓN B: Instalar en Dispositivo Físico

### Paso 1: Conectar por USB
```cmd
:: Verificar conexión
adb devices

:: Resultado esperado:
:: List of attached devices
:: emulator-XXXX device  (o tu teléfono listado)
```

### Paso 2: Habilitar USB Debugging (en teléfono)
Settings → Developer Options → USB Debugging (ON)

### Paso 3: Instalar APK
```cmd
cd c:\Users\josel\AndroidStudioProjects\AppMovil\app_ajicolor_backend_node
adb install -r app\build\outputs\apk\debug\app-debug.apk
```

---

## Test Rápido: Login

### Test 1: Credenciales Admin
```
Email:    admin@ajicolor.com
Password: Admin123
```

**Resultado esperado:**
- ✅ Pantalla de login desaparece
- ✅ Pantalla de inicio (Home) aparece
- ✅ Producto listados en catálogo

### Test 2: Credenciales Inválidas
```
Email:    admin@ajicolor.com
Password: WRONG123
```

**Resultado esperado:**
- ❌ Mensaje de error: "Credenciales inválidas"
- ❌ Sin navegación

### Test 3: Sin Conexión
```cmd
:: En Android Studio Emulator: Menu → Extended controls → Cellular
:: Seleccionar "Off"
```

**Resultado esperado:**
- ❌ Timeout o error de conexión
- ❌ Toast con "Sin conexión"

---

## Si Hay Crashes

### Ver el error completo
```cmd
adb logcat > error.log
```
(Luego abre error.log y busca "Exception")

### Limpiar y recompilar
```cmd
cd c:\Users\josel\AndroidStudioProjects\AppMovil\app_ajicolor_backend_node
gradlew.bat clean assembleDebug
adb install -r app\build\outputs\apk\debug\app-debug.apk
```

### Limpiar datos de la app
```cmd
adb shell pm clear com.example.appajicolorgrupo4
```

---

## Verificar Backend

### Health Check
```cmd
curl https://backend-app-movil.vercel.app/health
```

**Resultado esperado:**
```json
{
  "status": "ok",
  "database": "connected"
}
```

### Test Login (cURL)
```cmd
curl -X POST https://backend-app-movil.vercel.app/api/v1/auth/login ^
  -H "Content-Type: application/json" ^
  -d "{\"correo\": \"admin@ajicolor.com\", \"contrasena\": \"Admin123\"}"
```

---

## Checklist Antes de Proceder

- [ ] APK generado (40.2 MB)
- [ ] Emulador instalado o teléfono conectado
- [ ] ADB funcionando (`adb devices`)
- [ ] Backend disponible (health check OK)
- [ ] Database MongoDB conectada

---

## Próximo Hito: FASE 2 Completa

Una vez instales y pruebes login:

1. **Catálogo:**
   - Abre pantalla de productos
   - Verifica que cargan desde API
   - Prueba búsqueda

2. **Carrito:**
   - Agrega productos
   - Verifica que se guardan en Room
   - Prueba checkout

3. **Perfil:**
   - Ve tus datos
   - Edita y guarda
   - Logout

4. **Admin** (si eres admin):
   - Panel de usuarios
   - Panel de pedidos
   - Ver estadísticas

---

## Documentos Disponibles

| Documento | Propósito |
|-----------|-----------|
| `FASE1_COMPLETADO.md` | Resumen de lo realizado |
| `FASE2_PLAN.md` | Plan detallado de testing |
| `RESUMEN_EJECUTIVO.md` | Overview ejecutivo |
| `README.md` | Setup general |

---

## SOS: Comandos Útiles

```cmd
:: Ver todos los AVD disponibles
emulator -list-avds

:: Resetear emulador
emulator -avd Pixel4_API34 -wipe-data

:: Ver todos los dispositivos/emuladores
adb devices

:: Ver tamaño de APK
dir app\build\outputs\apk\debug\app-debug.apk

:: Desinstalar app
adb uninstall com.example.appajicolorgrupo4

:: Mostrar última línea de logs
adb logcat -t 100

:: Filtrar solo errores
adb logcat -s "*:E"

:: Conectar a emulador shell
adb shell

:: Ver archivos de app
adb shell ls /data/data/com.example.appajicolorgrupo4
```

---

**¿Listo? ¡Comienza por el Paso 1! 🚀**

Cualquier error, copia el log completo (`adb logcat > log.txt`) y compártelo.
