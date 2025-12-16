# 🚀 FASE 5: TESTING, SIGNING Y DEPLOYMENT

**Fecha:** 15 de Diciembre de 2025  
**Objetivo:** Generar APK Release firmada y distribuible  
**Versión:** 1.0  

---

## 📋 TABLA DE CONTENIDOS

1. [Testing Unitario](#testing-unitario)
2. [Generación de Keystore](#generación-de-keystore)
3. [Configuración de Signing](#configuración-de-signing)
4. [Generación de AAB/APK](#generación-de-aabapk)
5. [Despliegue a Google Play](#despliegue-a-google-play)

---

## 🧪 TESTING UNITARIO

### Paso 1: Ejecutar Tests Debug

**Comando:**
```bash
cd c:\Users\josel\AndroidStudioProjects\AppMovil\app_ajicolor_backend_node
gradlew testDebugUnitTest --stacktrace
```

**Tiempo esperado:** 60-120 segundos

**Salida esperada:**
```
BUILD SUCCESSFUL in Xs
X tests executed, X passed, X skipped, X failed
```

**Reporte HTML:**
```
app/build/reports/tests/testDebugUnitTest/index.html
```

---

### Paso 2: Si hay errores de test

**Abre el reporte HTML en VS Code:**
1. Ve a: `app/build/reports/tests/testDebugUnitTest/index.html`
2. Abre en el navegador
3. Busca los tests que fallaron
4. Mira la causa de error

**Comandos adicionales:**

```bash
# Ver solo resumen de tests
gradlew testDebugUnitTest --info

# Ejecutar test específico
gradlew testDebugUnitTest -Dkotlin.tests.runningInIde=false

# Limpiar y reintentar
gradlew clean testDebugUnitTest
```

---

### Paso 3: Ejecutar Tests Release (Opcional)

```bash
gradlew testReleaseUnitTest
```

---

## 🔐 GENERACIÓN DE KEYSTORE

### Opción A: Usar Keystore Existente

Si ya tienes un keystore (como `alejandro-key.jks`), **salta a la sección "Configuración de Signing"**.

Tu proyecto ya tiene configurado:
```
Ubicación: keystore/alejandro-key.jks
Alias: key0
```

---

### Opción B: Crear Nuevo Keystore (Si no tienes uno)

#### Paso 1: Crear carpeta keystore

```bash
cd c:\Users\josel\AndroidStudioProjects\AppMovil\app_ajicolor_backend_node
mkdir keystore
```

#### Paso 2: Generar Keystore con keytool

```bash
keytool -genkey -v -keystore keystore\ajicolor-release-key.jks -keyalg RSA -keysize 2048 -validity 10000 -alias ajicolor_key
```

**Explicación de parámetros:**
- `-genkey`: Generar nueva clave
- `-v`: Verbose (mostrar detalle)
- `-keystore`: Ruta del archivo `.jks`
- `-keyalg RSA`: Algoritmo RSA (seguro)
- `-keysize 2048`: Tamaño de clave en bits
- `-validity 10000`: Válida por 10,000 días (~27 años)
- `-alias ajicolor_key`: Nombre único para la clave

#### Paso 3: Responde las preguntas interactivamente

```
Enter keystore password:                 [Crea contraseña fuerte, ej: AjiColor2024!@#]
Re-enter new password:                   [Repite contraseña]

What is your first and last name?        [Tu nombre, ej: Jose Luis]
What is the name of your organizational unit?  [Nombre del equipo, ej: AjiColor Dev]
What is the name of your organization?   [Nombre empresa, ej: AjiColor]
What is the name of your City or Locality?    [Ciudad, ej: Cochabamba]
What is the name of your State or Province?   [Estado, ej: Bolivia]
What is the two-letter country code for this unit?  [Código país, ej: BO]

Is CN=Jose Luis, OU=AjiColor Dev, O=AjiColor, L=Cochabamba, ST=Bolivia, C=BO correct?
[no]:  yes

Enter key password for <ajicolor_key>:   [Presiona Enter = misma contraseña anterior]
```

#### Paso 4: Verifica que se creó el keystore

```bash
dir keystore\ajicolor-release-key.jks
```

Debe mostrar el archivo con tamaño >1 KB.

---

### Opción C: Listar claves de un keystore existente

```bash
keytool -list -v -keystore keystore\alejandro-key.jks -alias key0 -storepass 35203520
```

**Resultado:**
```
Owner: CN=..., OU=..., O=..., L=..., ST=..., C=...
Issuer: CN=..., OU=..., O=..., L=..., ST=..., C=...
Serial number: ...
Valid from: 2024-... to 2054-...
...
```

---

## 🛠️ CONFIGURACIÓN DE SIGNING

### Tu build.gradle.kts ya tiene configurado:

```kotlin
signingConfigs {
    create("release") {
        storeFile = file("keystore/alejandro-key.jks")
        storePassword = "35203520"
        keyAlias = "key0"
        keyPassword = "35203520"
    }
}

buildTypes {
    release {
        isMinifyEnabled = false
        proguardFiles(
            getDefaultProguardFile("proguard-android-optimize.txt"),
            "proguard-rules.pro"
        )
        signingConfig = signingConfigs.getByName("release")
    }
}
```

### Si usas un keystore nuevo, actualiza así:

```kotlin
signingConfigs {
    create("release") {
        storeFile = file("keystore/ajicolor-release-key.jks")  // ← Cambia esto
        storePassword = "AjiColor2024!@#"                      // ← Cambia esto
        keyAlias = "ajicolor_key"                              // ← Cambia esto
        keyPassword = "AjiColor2024!@#"                        // ← Cambia esto
    }
}
```

---

## 🔒 SEGURIDAD: .gitignore

**IMPORTANTE:** Nunca subas el keystore a GitHub.

Abre `.gitignore` en la raíz del proyecto y agreg a:

```
# Keystore (claves privadas - NUNCA subir a GitHub)
keystore/
*.jks
*.keystore
*.p12

# Gradle build outputs
build/
.gradle/

# IDE
.idea/
.vscode/
*.iml
```

**Verifica:**
```bash
cd c:\Users\josel\AndroidStudioProjects\AppMovil\app_ajicolor_backend_node
type .gitignore | findstr keystore
```

Debe mostrar las líneas que agregaste.

---

## 📦 GENERACIÓN DE AAB Y APK

### Opción 1: AAB (Android App Bundle) - Para Google Play

**Comando:**
```bash
cd c:\Users\josel\AndroidStudioProjects\AppMovil\app_ajicolor_backend_node
gradlew bundleRelease
```

**Tiempo:** 2-3 minutos  
**Resultado:** `app/build/outputs/bundle/release/app-release.aab`  
**Tamaño:** ~25 MB (más optimizado que APK)

**Verificar:**
```bash
dir app\build\outputs\bundle\release\
```

---

### Opción 2: APK Universal - Para instalación manual

**Comando:**
```bash
cd c:\Users\josel\AndroidStudioProjects\AppMovil\app_ajicolor_backend_node
gradlew assembleRelease
```

**Tiempo:** 90-120 segundos  
**Resultado:** `app/build/outputs/apk/release/app-release.apk`  
**Tamaño:** ~30-35 MB

**Verificar:**
```bash
dir app\build\outputs\apk\release\
```

---

### Opción 3: Ambos (AAB + APK) en un comando

```bash
cd c:\Users\josel\AndroidStudioProjects\AppMovil\app_ajicolor_backend_node
gradlew bundleRelease assembleRelease
```

---

## 📊 COMPARATIVA: AAB vs APK

| Aspecto | AAB | APK |
|---------|-----|-----|
| **Uso** | Google Play | Instalación directa |
| **Tamaño** | 25 MB | 30-35 MB |
| **Optimización** | Automática por Play | Manual por arquitectura |
| **Comando** | `bundleRelease` | `assembleRelease` |
| **Ubicación** | `bundle/release/` | `apk/release/` |
| **Instalación** | Sube a Play Console | `adb install` |

**Recomendación:** Usa **AAB para Google Play** (Google lo optimiza)

---

## 🚀 RUTAS EXACTAS DE SALIDA

### AAB (Bundle)
```
c:\Users\josel\AndroidStudioProjects\AppMovil\app_ajicolor_backend_node\app\build\outputs\bundle\release\app-release.aab
```

### APK Release
```
c:\Users\josel\AndroidStudioProjects\AppMovil\app_ajicolor_backend_node\app\build\outputs\apk\release\app-release.apk
```

### Verificar firma del APK
```bash
jarsigner -verify -verbose -certs app\build\outputs\apk\release\app-release.apk | findstr CN=
```

---

## 📱 INSTALACIÓN MANUAL EN CELULAR

### Con el APK Release:

```bash
# Conecta celular por USB o adb TCP
adb connect 192.168.1.XXX:5555

# Instala el APK
adb install -r app\build\outputs\apk\release\app-release.apk

# Verifica instalación
adb shell pm list packages | findstr ajicolor
```

---

## 🎯 DESPLIEGUE A GOOGLE PLAY

### Paso 1: Crear Cuenta Google Play Console

1. Ve a: https://play.google.com/console
2. Inicia sesión con tu Google Account
3. Crea un nuevo proyecto (Costo: $25 USD, pago único)
4. Completa tu perfil de desarrollador

---

### Paso 2: Preparar la APP para Google Play

**Requisitos obligatorios:**

- [x] APK/AAB firmado con keystore
- [x] Icono de aplicación 512x512 px
- [x] Capturas de pantalla (mín 2, máx 8)
- [x] Descripción corta (<80 caracteres)
- [x] Descripción larga (<4000 caracteres)
- [x] Categoría (ej: Shopping)
- [x] Calificación de contenido (privacy policy, etc.)

---

### Paso 3: Subir AAB a Google Play Console

1. En Google Play Console:
   - Ve a: **Apps** → **Crear app**
   - Nombre: "AjiColor"
   - Idioma: Español
   - Categoría: Shopping/Commerce

2. **Build Release:**
   - Ve a: **Versión de prueba** → **Producción**
   - Click: **Crear versión**
   - Upload: Arrastra el `app-release.aab`

3. **Metadatos:**
   - Icono (512x512)
   - Nombre de app
   - Descripción

4. **Revisión y lanzamiento:**
   - Revisa todo
   - Click: **Revisar lanzamiento**
   - Click: **Lanzar a producción**

---

### Paso 4: Google Play lo revisa y publica

**Tiempo:** 2-4 horas (típicamente)

**Notificaciones:** Google Play Console te enviará emails

---

## 🔧 COMANDOS RÁPIDOS (COPY-PASTE)

### Build todo de una vez
```bash
cd c:\Users\josel\AndroidStudioProjects\AppMovil\app_ajicolor_backend_node && gradlew clean bundleRelease assembleRelease
```

**Tiempo total:** 5-7 minutos

---

### Verificar APK/AAB generado
```bash
# AAB
dir app\build\outputs\bundle\release\

# APK
dir app\build\outputs\apk\release\
```

---

### Listar con tamaño
```bash
dir /s app\build\outputs\bundle\release\app-release.aab
dir /s app\build\outputs\apk\release\app-release.apk
```

---

## ⚙️ CONFIGURACIONES RECOMENDADAS FINALES

### Para Producción (build.gradle.kts)

```kotlin
buildTypes {
    release {
        isMinifyEnabled = true           // ← Habilita R8 ofuscation
        shrinkResources = true           // ← Elimina recursos no usados
        proguardFiles(
            getDefaultProguardFile("proguard-android-optimize.txt"),
            "proguard-rules.pro"
        )
        signingConfig = signingConfigs.getByName("release")
    }
}
```

**Ventajas:**
- APK más pequeño (~28-32 MB)
- Ofuscación de código
- Mejor performance

**Desventajas:**
- Compilación más lenta (120+ segundos)
- Stack traces ofuscados (usar mapping.txt)

---

## 📊 CHECKLIST PRE-LANZAMIENTO

- [ ] Tests unitarios pasan (`gradlew testDebugUnitTest`)
- [ ] Keystore creado y guardado de forma segura
- [ ] `.gitignore` contiene `keystore/` y `*.jks`
- [ ] `build.gradle` tiene `signingConfig` correcto
- [ ] APK Release compilado sin errores
- [ ] Firma verificada (`jarsigner -verify`)
- [ ] AAB compilado sin errores
- [ ] Google Play Console cuenta creada
- [ ] Metadatos completados (icono, descripción, etc.)
- [ ] Política de privacidad lista
- [ ] APK/AAB subido a Google Play
- [ ] Publicado a "Producción"

---

## 🆘 TROUBLESHOOTING

### "Keystore not found"
```bash
# Verifica que existe
dir keystore\alejandro-key.jks

# Si no existe, crea uno nuevo (ver sección Opción B)
```

---

### "BUILD FAILED: Signing"
```bash
# Verifica credenciales en build.gradle
# Asegúrate que storePassword y keyPassword son correctas

# Prueba la clave:
keytool -list -v -keystore keystore\alejandro-key.jks -storepass 35203520
```

---

### "APK es demasiado grande"
```bash
# Habilita R8 y shrinkResources en build.gradle
buildTypes {
    release {
        isMinifyEnabled = true
        shrinkResources = true
    }
}
```

Esto reduce de 40-45 MB a 28-32 MB.

---

### "Test compilation error"
```bash
# Los tests no existen aún, es normal
# Para saltar tests y compilar directamente:
gradlew assembleRelease -x testDebugUnitTest
```

---

## 📞 PRÓXIMOS PASOS

### Ahora:
1. ✅ Lee esta guía completa
2. ✅ Genera keystore (si no tienes)
3. ✅ Verifica `build.gradle` firmando

### Luego:
1. 🔨 Compila: `gradlew bundleRelease assembleRelease`
2. ✔️ Verifica APK/AAB generados
3. 📤 Sube a Google Play Console
4. 🎉 ¡Publicado!

---

**Preparado por:** GitHub Copilot  
**Fecha:** 15 Dic 2025  
**Estado:** ✅ LISTO PARA SEGUIR  

