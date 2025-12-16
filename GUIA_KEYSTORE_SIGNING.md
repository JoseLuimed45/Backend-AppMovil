# 🔐 GUÍA COMPLETA: KEYSTORE Y SIGNING

**Fecha:** 15 de Diciembre de 2025  
**Versión:** 1.0  
**Objetivo:** Entender y manejar keystores para firma digital

---

## ¿QUÉ ES UN KEYSTORE?

Un **keystore** es un archivo cifrado (`.jks`) que contiene:
- **Clave privada:** Solo tú la tienes (SEGRETA)
- **Certificado público:** Identificación de tu app
- **Metadatos:** Información del desarrollador

**Analogía:** Es como el DNI + firma digital de tu app

---

## 🔍 VERIFICAR SI YA TIENES KEYSTORE

### Opción 1: Ver si existe el archivo

```bash
cd c:\Users\josel\AndroidStudioProjects\AppMovil\app_ajicolor_backend_node
dir keystore\
```

**Si ves `alejandro-key.jks`:** ✅ Ya tienes keystore

**Si no ves nada:** Continúa a sección "Crear nuevo Keystore"

---

### Opción 2: Verificar credenciales del keystore existente

```bash
keytool -list -v -keystore keystore\alejandro-key.jks -storepass 35203520
```

**Resultado esperado:**
```
Keystore type: JKS
Keystore provider: SUN

Your keystore contains 1 entry

Alias name: key0
Creation date: Nov 10, 2024
Entry type: PrivateKeyEntry
Certificate chain length: 1
Certificate[1]:
        Owner: CN=Alejandro Placencia, OU=..., O=..., L=..., ST=..., C=...
        ...
        Valid from: 2024-11-10 to 2054-11-02
```

---

## 🆕 CREAR NUEVO KEYSTORE (Si necesitas)

### Paso 1: Navega a la carpeta del proyecto

```bash
cd c:\Users\josel\AndroidStudioProjects\AppMovil\app_ajicolor_backend_node
```

### Paso 2: Crea carpeta keystore (si no existe)

```bash
mkdir keystore
```

### Paso 3: Genera la clave con keytool

```bash
keytool -genkey -v -keystore keystore\ajicolor-release-key.jks -keyalg RSA -keysize 2048 -validity 10000 -alias ajicolor_key
```

### Paso 4: Responde las preguntas interactivamente

**Ejemplo de respuestas:**

```
Enter keystore password: AjiColor2024!@#
Re-enter new password: AjiColor2024!@#

What is your first and last name?
  [Unknown]: Jose Luis Mendoza

What is the name of your organizational unit?
  [Unknown]: AjiColor Dev

What is the name of your organization?
  [Unknown]: AjiColor

What is the name of your City or Locality?
  [Unknown]: Cochabamba

What is the name of your State or Province?
  [Unknown]: Bolivia

What is the two-letter country code for this unit?
  [Unknown]: BO

Is CN=Jose Luis Mendoza, OU=AjiColor Dev, O=AjiColor, L=Cochabamba, ST=Bolivia, C=BO correct?
  [no]: yes

Enter key password for <ajicolor_key>
  (RETURN if same as keystore password): [Presiona Enter]

[Generating 2,048 bit RSA key pair and self-signed certificate...]
```

### Paso 5: Verifica que se creó

```bash
dir keystore\
```

Debe mostrar: `ajicolor-release-key.jks` (>1 KB)

---

## 🛠️ CONFIGURAR BUILD.GRADLE CON EL NUEVO KEYSTORE

Si creaste uno nuevo, actualiza `build.gradle.kts` líneas 30-41:

**Busca:**
```kotlin
signingConfigs {
    create("release") {
        storeFile = file("keystore/alejandro-key.jks")
        storePassword = "35203520"
        keyAlias = "key0"
        keyPassword = "35203520"
    }
}
```

**Reemplaza con:**
```kotlin
signingConfigs {
    create("release") {
        storeFile = file("keystore/ajicolor-release-key.jks")  // ← Nombre nuevo
        storePassword = "AjiColor2024!@#"                       // ← Contraseña nueva
        keyAlias = "ajicolor_key"                               // ← Alias nuevo
        keyPassword = "AjiColor2024!@#"                         // ← Contraseña nueva
    }
}
```

---

## 🔍 LISTAR CLAVES DE UN KEYSTORE

### Ver todas las claves

```bash
keytool -list -keystore keystore\ajicolor-release-key.jks -storepass AjiColor2024!@#
```

**Salida:**
```
Keystore type: JKS
...
Alias name: ajicolor_key
Creation date: Dec 15, 2025
Entry type: PrivateKeyEntry
```

### Ver detalles completos de una clave

```bash
keytool -list -v -keystore keystore\ajicolor-release-key.jks -alias ajicolor_key -storepass AjiColor2024!@#
```

---

## 📋 FLUJO COMPLETO: CREAR Y CONFIGURAR

### 1️⃣ Generar keystore
```bash
keytool -genkey -v -keystore keystore\ajicolor-release-key.jks -keyalg RSA -keysize 2048 -validity 10000 -alias ajicolor_key
```

### 2️⃣ Editar build.gradle.kts con credenciales

### 3️⃣ Compilar APK/AAB
```bash
gradlew clean bundleRelease assembleRelease
```

### 4️⃣ Verificar firma
```bash
jarsigner -verify -verbose app\build\outputs\apk\release\app-release.apk
```

---

## 🔐 COMANDOS KEYTOOL MÁS USADOS

### Generar keystore
```bash
keytool -genkey -v -keystore [archivo.jks] -keyalg RSA -keysize 2048 -validity 10000 -alias [alias]
```

### Listar claves
```bash
keytool -list -keystore [archivo.jks] -storepass [contraseña]
```

### Ver detalles de una clave
```bash
keytool -list -v -keystore [archivo.jks] -alias [alias] -storepass [contraseña]
```

### Cambiar contraseña del keystore
```bash
keytool -storepasswd -keystore [archivo.jks] -storepass [vieja] -new [nueva]
```

### Cambiar contraseña de una clave
```bash
keytool -keypasswd -keystore [archivo.jks] -alias [alias] -keypass [vieja] -new [nueva]
```

### Exportar certificado
```bash
keytool -export -keystore [archivo.jks] -alias [alias] -file [salida.cer] -storepass [contraseña]
```

---

## ⚠️ SEGURIDAD: PROTEGER TU KEYSTORE

### 1️⃣ NUNCA subas el keystore a GitHub

Verifica `.gitignore`:

```bash
# En .gitignore debe haber:
keystore/
*.jks
*.keystore
*.p12
```

**Verifica:**
```bash
type .gitignore | findstr keystore
```

### 2️⃣ Guarda una copia de seguridad

Copia `keystore\alejandro-key.jks` a:
- USB externa
- Google Drive (cifrada)
- Dropbox (cifrada)
- Servidor NAS (cifrado)

**NUNCA** en repositorio público de GitHub

### 3️⃣ Usa contraseña fuerte

```
❌ Débil:    12345, password, admin
✅ Fuerte:   AjiColor2024!@#XyZ
✅ Muy fuerte: P@ssw0rd$#2024!AjiColor.SECURE
```

### 4️⃣ Documenta pero no en GitHub

Crea archivo `KEYSTORE_INFO.txt` **SOLO LOCALMENTE**:
```
BACKUP KEYSTORE INFO (NO SUBIR A GITHUB)
Archivo: alejandro-key.jks
Ubicación: keystore/
Alias: key0
Store password: 35203520
Key password: 35203520
Creado: 2024-11-10
Válido hasta: 2054-11-02
```

Guarda en USB, NO en GitHub.

---

## 🛠️ TROUBLESHOOTING KEYTOOL

### "Keytool not found"

**Solución:**
```bash
# Verifica que JDK 17 está en PATH
java -version
# Debe mostrar: openjdk version "17.x.x"

# Si no está, agrega a PATH:
# Inicio > Variables de entorno > JAVA_HOME
# C:\Program Files\Java\jdk-17.X.X
```

---

### "Unable to open keystore"

```bash
# Verifica que el archivo existe
dir keystore\alejandro-key.jks

# Verifica ruta exacta (sin espacios):
keytool -list -keystore keystore\alejandro-key.jks -storepass 35203520
```

---

### "Invalid keystore format"

**Causa:** El archivo está corrupto o no es un `.jks`

```bash
# Verifica tipo de archivo
file keystore\alejandro-key.jks

# Si está corrupto, crea uno nuevo
keytool -genkey -v -keystore keystore\nuevo.jks ...
```

---

### "Password is incorrect"

```bash
# Verifica contraseña exacta
keytool -list -keystore keystore\alejandro-key.jks -storepass 35203520

# Si olvidas, no hay forma de recuperar
# Debes crear uno nuevo
```

---

## 📊 COMPARATIVA: KEYSTORE vs CERTIFICADO

| Aspecto | Keystore (.jks) | Certificado (.cer) |
|---------|-----------------|-------------------|
| **Contiene** | Clave privada + Certificado | Solo certificado público |
| **Seguridad** | MUY ALTA (cifrado) | BAJA (público) |
| **Firmar APK** | ✅ Sí | ❌ No |
| **Verificar firma** | ✅ Sí | ✅ Sí |
| **Compartir** | ❌ NUNCA | ✅ Sí |
| **Archivo** | `.jks` | `.cer` |

---

## 🎯 RESUMEN RÁPIDO

### Si ya tienes keystore (como tú):
1. ✅ Está en: `keystore/alejandro-key.jks`
2. ✅ Está configurado en: `build.gradle.kts`
3. ✅ Listo para: `gradlew bundleRelease assembleRelease`

### Si necesitas crear nuevo:
1. `keytool -genkey -v -keystore keystore\new.jks ...`
2. Actualiza `build.gradle.kts` con credenciales
3. `gradlew bundleRelease assembleRelease`

---

## ✅ CHECKLIST KEYTOOL

- [ ] Keystore existe: `dir keystore\`
- [ ] Contraseña correcta: `keytool -list -keystore ...`
- [ ] Alias existe: `keytool -list -v -keystore ... -alias key0`
- [ ] build.gradle.kts tiene credenciales correctas
- [ ] `.gitignore` contiene `keystore/`
- [ ] Backup guardado de forma segura (USB)
- [ ] Documentación guardada LOCALMENTE (no GitHub)

---

## 🆘 AYUDA RÁPIDA

```bash
# Verificar JDK
keytool -version

# Listar keystore
keytool -list -keystore keystore\alejandro-key.jks -storepass 35203520

# Ver detalles
keytool -list -v -keystore keystore\alejandro-key.jks -storepass 35203520

# Crear nuevo
keytool -genkey -v -keystore keystore\new.jks -keyalg RSA -keysize 2048 -validity 10000 -alias key

# Ver firma de APK
jarsigner -verify -verbose app\build\outputs\apk\release\app-release.apk
```

---

**Preparado por:** GitHub Copilot  
**Fecha:** 15 Dic 2025  
**Status:** ✅ REFERENCIA COMPLETA

