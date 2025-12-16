# 🎯 FASE 5: COMANDOS COPY-PASTE LISTOS

**Estado:** ✅ READY TO EXECUTE  
**Tiempo total:** ~10 minutos (generación) + 5-7 minutos (compilación)

---

## ⚡ OPCIÓN 1: BUILD COMPLETO (Recomendado)

Ejecuta esto para generar todo en un comando:

```bash
cd c:\Users\josel\AndroidStudioProjects\AppMovil\app_ajicolor_backend_node && gradlew clean bundleRelease assembleRelease
```

**Resultado esperado:**
```
BUILD SUCCESSFUL in Xm Ys

Generated:
- app-release.aab (~25 MB)
- app-release.apk (~30-35 MB)
```

**Ubicación:**
- AAB: `app\build\outputs\bundle\release\app-release.aab`
- APK: `app\build\outputs\apk\release\app-release.apk`

---

## ⚡ OPCIÓN 2: Solo AAB (Para Google Play)

```bash
cd c:\Users\josel\AndroidStudioProjects\AppMovil\app_ajicolor_backend_node && gradlew clean bundleRelease
```

**Tiempo:** 2-3 minutos  
**Salida:** `app\build\outputs\bundle\release\app-release.aab`

---

## ⚡ OPCIÓN 3: Solo APK (Para instalación manual)

```bash
cd c:\Users\josel\AndroidStudioProjects\AppMovil\app_ajicolor_backend_node && gradlew clean assembleRelease
```

**Tiempo:** 90-120 segundos  
**Salida:** `app\build\outputs\apk\release\app-release.apk`

---

## 🔐 KEYTOOL: CREAR KEYSTORE

Si necesitas crear una nueva keystore (keystore actual funciona):

```bash
keytool -genkey -v -keystore keystore\ajicolor-release-key.jks -keyalg RSA -keysize 2048 -validity 10000 -alias ajicolor_key
```

**Responde:**
- Keystore password: `[Contraseña fuerte]`
- First and last name: `[Tu nombre]`
- Organizational unit: `AjiColor Dev`
- Organization: `AjiColor`
- City: `Cochabamba`
- State: `Bolivia`
- Country: `BO`
- Key password: `[Presiona Enter = misma contraseña]`

---

## 🔍 VERIFICAR KEYSTORE

```bash
keytool -list -v -keystore keystore\alejandro-key.jks -storepass 35203520
```

---

## ✔️ VERIFICAR APK FIRMADO

```bash
jarsigner -verify -verbose app\build\outputs\apk\release\app-release.apk
```

Debe mostrar: `jar verified`

---

## 📱 INSTALAR APK EN CELULAR/EMULADOR

```bash
adb install -r app\build\outputs\apk\release\app-release.apk
```

---

## 📂 VER ARCHIVOS GENERADOS

### Ver AAB
```bash
dir app\build\outputs\bundle\release\
```

### Ver APK
```bash
dir app\build\outputs\apk\release\
```

### Ver ambos con tamaño
```bash
dir /s app\build\outputs\bundle\release\app-release.aab
dir /s app\build\outputs\apk\release\app-release.apk
```

---

## 🧪 EJECUTAR TESTS (Opcional)

```bash
cd c:\Users\josel\AndroidStudioProjects\AppMovil\app_ajicolor_backend_node && gradlew testDebugUnitTest
```

**Reporte:** `app\build\reports\tests\testDebugUnitTest\index.html`

---

## 📋 FLUJO RECOMENDADO

### Paso 1: Tests (Opcional, probablemente fallará sin tests)
```bash
cd c:\Users\josel\AndroidStudioProjects\AppMovil\app_ajicolor_backend_node
gradlew testDebugUnitTest
```

### Paso 2: Build Completo
```bash
cd c:\Users\josel\AndroidStudioProjects\AppMovil\app_ajicolor_backend_node
gradlew clean bundleRelease assembleRelease
```

### Paso 3: Verificar generados
```bash
dir app\build\outputs\bundle\release\
dir app\build\outputs\apk\release\
```

### Paso 4: Verificar firma (Opcional)
```bash
jarsigner -verify -verbose app\build\outputs\apk\release\app-release.apk
```

---

## 🆘 SI ALGO FALLA

### Error: "Compilation error"
```bash
# Limpiar y reintentar
gradlew clean bundleRelease assembleRelease
```

### Error: "Signing failed"
```bash
# Verifica que el keystore existe
dir keystore\alejandro-key.jks

# Verifica credenciales en build.gradle.kts líneas 30-41
```

### Error: "APK too large"
Edita `build.gradle.kts` línea 47:
```kotlin
isMinifyEnabled = true  // Cambiar de false a true
```

---

## ✅ CHECKLIST FINAL

- [ ] Ejecuté: `gradlew clean bundleRelease assembleRelease`
- [ ] Veo: `BUILD SUCCESSFUL`
- [ ] APK en: `app\build\outputs\apk\release\app-release.apk`
- [ ] AAB en: `app\build\outputs\bundle\release\app-release.aab`
- [ ] Firma verificada: `jarsigner -verify app\build\outputs\apk\release\app-release.apk`

---

## 📦 ARCHIVOS FINALES

```
Nombre de app: AjiColor
Versión: 1.0
Build: Release

Generados:
├─ app-release.aab (~25 MB) → Para Google Play
└─ app-release.apk (~30-35 MB) → Para instalación manual
```

---

## 🎉 PRÓXIMO PASO

Sube el `app-release.aab` a **Google Play Console** y ¡publicado!

Ver: `FASE5_TESTING_SIGNING_DEPLOYMENT.md` sección "Despliegue a Google Play"

---

Preparado por: **GitHub Copilot**  
Fecha: **15 Dic 2025**  
Status: ✅ **LISTO PARA EJECUTAR**

