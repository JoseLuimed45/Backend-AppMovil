# 🎯 FASE 4: COMANDOS EXACTOS PARA GENERAR APK

## 📍 ANTES DE EMPEZAR

Asegúrate de estar en la carpeta correcta. Abre Terminal en VS Code:
- **Windows (CMD):** `Ctrl + ~` o Terminal → New Terminal
- **Copia esta ruta exacta:**
```
c:\Users\josel\AndroidStudioProjects\AppMovil\app_ajicolor_backend_node
```

---

## 🚀 OPCIÓN 1: BUILD DEBUG (Testing en Emulador)

**¿Cuándo usarlo?** Para testing local, desarrollo, emulador  
**Tamaño:** 40-45 MB  
**Firma:** Debug key  
**Tiempo:** 30-60 segundos  

### Paso 1: Abre terminal
```
Ctrl + ~ (en VS Code)
```

### Paso 2: Navega a la carpeta
```bash
cd c:\Users\josel\AndroidStudioProjects\AppMovil\app_ajicolor_backend_node
```

### Paso 3: Ejecuta el comando
```bash
gradlew clean assembleDebug
```

### Paso 4: Espera a que termine
```
...compilando...
BUILD SUCCESSFUL in 45s
```

### Paso 5: Ubica el APK
```
c:\Users\josel\AndroidStudioProjects\AppMovil\app_ajicolor_backend_node\app\build\outputs\apk\debug\app-debug.apk
```

---

## 🚀 OPCIÓN 2: BUILD RELEASE (Producción/Google Play)

**¿Cuándo usarlo?** Para publicar en Google Play  
**Tamaño:** 28-32 MB (ofuscado)  
**Firma:** alejandro-key.jks  
**Tiempo:** 60-120 segundos  

### Paso 1: Abre terminal
```
Ctrl + ~ (en VS Code)
```

### Paso 2: Navega a la carpeta
```bash
cd c:\Users\josel\AndroidStudioProjects\AppMovil\app_ajicolor_backend_node
```

### Paso 3: Ejecuta el comando
```bash
gradlew clean assembleRelease
```

### Paso 4: Espera a que termine
```
...compilando y ofuscando...
BUILD SUCCESSFUL in 90s
```

### Paso 5: Ubica el APK
```
c:\Users\josel\AndroidStudioProjects\AppMovil\app_ajicolor_backend_node\app\build\outputs\apk\release\app-release.apk
```

---

## ⚡ COMANDO COMPLETO (COPY-PASTE)

Puedes copiar y pegar esto directamente en terminal:

### Para Debug:
```bash
cd c:\Users\josel\AndroidStudioProjects\AppMovil\app_ajicolor_backend_node && gradlew clean assembleDebug
```

### Para Release:
```bash
cd c:\Users\josel\AndroidStudioProjects\AppMovil\app_ajicolor_backend_node && gradlew clean assembleRelease
```

---

## 📦 RUTAS EXACTAS DE SALIDA

### APK Debug
```
c:\Users\josel\AndroidStudioProjects\AppMovil\app_ajicolor_backend_node\app\build\outputs\apk\debug\app-debug.apk
```

### APK Release
```
c:\Users\josel\AndroidStudioProjects\AppMovil\app_ajicolor_backend_node\app\build\outputs\apk\release\app-release.apk
```

---

## 🔍 VERIFICAR QUE GRADLE FUNCIONE

**Antes de compilar, verifica tu JDK:**

```bash
cd c:\Users\josel\AndroidStudioProjects\AppMovil\app_ajicolor_backend_node
gradlew --version
```

**Debe mostrar:**
```
Gradle 8.13
...
Java version: 17.X.X
```

Si ve un error, revisa `gradle.properties` que tenga:
```properties
org.gradle.java.home=C:\\Program Files\\Java\\jdk-17.X.X
```

---

## ❓ PREGUNTAS FRECUENTES

### P: "¿Cuánto tarda la compilación?"
**R:** Típicamente 30-60 segundos para debug, 60-120 segundos para release

### P: "¿Dónde está mi APK?"
**R:** En `app/build/outputs/apk/debug/` o `app/build/outputs/apk/release/`

### P: "¿Puedo instalar ambos (debug y release)?"
**R:** Sí, tienen la misma firma diferente, no conflictúan

### P: "¿Debo usar debug o release?"
**R:** Debug para testing, Release para Google Play

### P: "¿Por qué el release es más pequeño?"
**R:** R8 ofusca y elimina código no usado (28-32 MB vs 40-45 MB)

---

## 📋 CHECKLIST ANTES DE COMPILAR

- [ ] ¿Estoy en la carpeta correcta? `c:\Users\josel\AndroidStudioProjects\AppMovil\app_ajicolor_backend_node`
- [ ] ¿Tengo terminal abierto?
- [ ] ¿JDK 17 está configurado? (`gradlew --version`)
- [ ] ¿He guardado los cambios en VS Code?

---

## 🛠️ SI ALGO FALLA

### Error: "Gradle no reconocido"
```bash
# Intenta con la extensión
gradlew.bat clean assembleDebug
```

### Error: "Java version mismatch"
```bash
# Verifica JDK
gradlew --version

# Si no es 17, revisa gradle.properties
```

### Error: "BUILD FAILED"
```bash
# Limpia completamente
gradlew clean

# Luego intenta de nuevo
gradlew assembleDebug
```

---

## 📞 PRÓXIMOS PASOS

### Después de Build Debug:
1. ✅ APK compilado exitosamente
2. 📱 Instalar en emulador: `adb install -r app\build\outputs\apk\debug\app-debug.apk`
3. 🧪 Hacer FASE 2B: Testing en emulador
4. ✅ Si todo funciona → Build Release

### Después de Build Release:
1. ✅ APK compilado y ofuscado
2. 📦 Subir a Google Play Console
3. 🎉 ¡Publicado!

---

**Documento preparado:** 15 Dic 2025  
**Estado:** ✅ LISTO PARA USAR  
**Soporte:** Consulta REPORTE_FASE4_AUDITORIA_COMPLETA.md para más detalles

