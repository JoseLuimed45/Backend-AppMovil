# 📋 INSTRUCCIONES PARA CONFIGURAR LA FIRMA JKS

## ✅ Resumen de la Configuración Actual

Tu proyecto está configurado para usar la firma de **Alejandro Placencia** con los siguientes datos:

- **Archivo JKS**: `alejandro-key.jks`
- **Contraseña Store**: `35203520`
- **Alias**: `alejandro_placencia`
- **Contraseña Key**: `35203520`

---

## 📁 Pasos para Copiar tu Firma al Proyecto

### 1. Localiza tu archivo de firma JKS
Busca tu archivo `.jks` en:
```
C:\Users\josel\AndroidStudioProjects\FimasJKS\firmaDevloper
```

O en cualquier otra ubicación donde lo tengas guardado.

### 2. Copia el archivo al proyecto
Copia tu archivo `.jks` a la siguiente ubicación:
```
C:\Users\josel\AndroidStudioProjects\app_ajicolor_backend_node\app poleras\App_Ajicolor\app\keystore\alejandro-key.jks
```

**IMPORTANTE**: El archivo DEBE llamarse `alejandro-key.jks` (o actualiza el nombre en las configuraciones).

### 3. Verifica que el archivo esté en el lugar correcto
Deberías tener esta estructura:
```
App_Ajicolor/
├── app/
│   ├── keystore/
│   │   ├── alejandro-key.jks    ← Tu archivo de firma AQUÍ
│   │   ├── release-key.jks      ← Firma generada automáticamente (backup)
│   │   └── .gitignore
│   └── build.gradle.kts
└── keystore.properties
```

---

## 🔍 Verificar el Alias del Keystore

Después de copiar tu archivo, verifica que el alias sea correcto ejecutando:

```powershell
cd "C:\Users\josel\AndroidStudioProjects\app_ajicolor_backend_node\app poleras\App_Ajicolor\app\keystore"
keytool -list -v -keystore alejandro-key.jks -storepass 35203520
```

Busca la línea que dice `Alias name:` y verifica que sea `alejandro_placencia`.

**Si el alias es diferente**, actualiza estas configuraciones:

### En `keystore.properties`:
```properties
keyAlias=TU_ALIAS_REAL
```

### En `app/build.gradle.kts`:
```kotlin
keyAlias = "TU_ALIAS_REAL"
```

---

## 🔄 Opciones de Firma Disponibles

El proyecto tiene configuradas **2 opciones de firma**:

### Opción 1: Tu firma existente (Alejandro Placencia) - **ACTIVA**
```
Archivo: alejandro-key.jks
Password: 35203520
Alias: alejandro_placencia
```

### Opción 2: Firma generada automáticamente - **BACKUP**
```
Archivo: release-key.jks
Password: ajicolor2024
Alias: ajicolor_key
```

Para cambiar entre opciones, edita `app/build.gradle.kts` y comenta/descomenta las líneas correspondientes.

---

## 🚀 Generar APK Firmado

Una vez configurada la firma, genera el APK con:

```powershell
cd "C:\Users\josel\AndroidStudioProjects\app_ajicolor_backend_node\app poleras\App_Ajicolor"
./gradlew assembleRelease
```

El APK firmado se generará en:
```
app/build/outputs/apk/release/app-release.apk
```

---

## 🔐 Seguridad

⚠️ **MUY IMPORTANTE**:
- NO subas el archivo `.jks` a repositorios públicos
- NO compartas las contraseñas públicamente
- El archivo `keystore.properties` está en `.gitignore` para proteger las credenciales
- Guarda una copia de seguridad de tu `.jks` en un lugar seguro

---

## ❓ Solución de Problemas

### Error: "Keystore file not found"
- Verifica que copiaste el archivo a `app/keystore/alejandro-key.jks`
- Verifica el nombre del archivo (debe ser exactamente `alejandro-key.jks`)

### Error: "Incorrect password"
- Verifica que la contraseña sea `35203520`
- Verifica que el alias sea correcto

### Error: "Alias not found"
- Ejecuta `keytool -list` para ver el alias real
- Actualiza `keyAlias` en las configuraciones con el alias correcto

---

## 📞 Contacto

Si tienes problemas, verifica:
1. ✅ El archivo `.jks` está en `app/keystore/`
2. ✅ El nombre del archivo es correcto
3. ✅ La contraseña es `35203520`
4. ✅ El alias coincide con el de tu keystore

Generado: 2025-12-01

