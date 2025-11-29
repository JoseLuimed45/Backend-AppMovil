# Solución de Errores - App Ajicolor

## Problema: La app se cierra al seleccionar una polera

### Errores Corregidos

#### 1. Errores de Compilación ✅
- **UsuarioViewModel.kt línea 93**: Eliminado parámetro `clave` inexistente
  ```kotlin
  // ANTES (❌ Error):
  clave = estadoActual.clave,
  
  // DESPUÉS (✅ Correcto):
  clave = "", // La clave ya no se gestiona localmente
  ```

- **UserRepositoryTest.kt**: Eliminados parámetros `clave` en instancias de `UserEntity`
  ```kotlin
  // ANTES (❌ Error):
  UserEntity(1, "Test", email, telefono, password, direccion)
  
  // DESPUÉS (✅ Correcto):
  UserEntity(1, "Test", email, telefono, direccion)
  ```

- **AppDatabase.kt**: Corregida creación de usuarios seed

#### 2. Problema de Incompatibilidad de Java ⚠️

**Causa**: El sistema tiene Java 25.0.1, pero Gradle 8.13 no soporta esta versión.

**Error**:
```
java.lang.IllegalArgumentException: 25.0.1
at org.jetbrains.kotlin.com.intellij.util.lang.JavaVersion.parse
```

### Soluciones

#### Opción 1: Compilar desde Android Studio (RECOMENDADO)
1. Abre Android Studio
2. File → Open → Selecciona el proyecto `App_Ajicolor`
3. Build → Make Project (Ctrl+F9)
4. Run → Run 'app' (Shift+F10)

#### Opción 2: Usar el script de compilación
Ejecuta el archivo `compilar-con-jdk-correcto.bat` que configurará automáticamente el JDK correcto.

```batch
cd "C:\Users\josel\AndroidStudioProjects\app_ajicolor_backend_node\app poleras\App_Ajicolor"
compilar-con-jdk-correcto.bat
```

#### Opción 3: Configurar JAVA_HOME permanentemente
1. Abre "Variables de entorno del sistema"
2. Agrega/modifica `JAVA_HOME`:
   ```
   C:\Program Files\Android\Android Studio\jbr
   ```
3. Reinicia el terminal y ejecuta:
   ```batch
   gradlew.bat clean assembleDebug
   ```

#### Opción 4: Script rápido multiuso (más simple todavía)
Puedes usar el nuevo script `usar-java-android.bat`.

Uso:
```batch
"c:\Users\josel\AndroidStudioProjects\app_ajicolor_backend_node\app poleras\App_Ajicolor\usar-java-android.bat" assembleDebug
```
Si lo abres con doble clic sin parámetros abre una consola ya configurada.

Qué hace internamente:
1. Fija `JAVA_HOME` al JDK de Android Studio.
2. Añade su `bin` al PATH temporal.
3. Ejecuta `gradlew.bat` con los argumentos que le pases.

Ejemplos útiles:
```batch
usar-java-android.bat clean assembleDebug
usar-java-android.bat installDebug
usar-java-android.bat test --info
```

#### Configuración permanente (explicada como para un niño)
Piensa que tu app necesita una "consola" especial (Java 17). Tu PC trae otra muy nueva (Java 25) que el juego todavía no entiende. El script le dice: "usa la consola correcta". Si quieres que siempre use la correcta sin script:
1. Abre Panel de Control → Sistema → Configuración avanzada → Variables de entorno.
2. Crea o edita `JAVA_HOME` y pon:
   ```
   C:\Program Files\Android\Android Studio\jbr
   ```
3. En la variable `Path` asegúrate de tener una línea:
   ```
   %JAVA_HOME%\bin
   ```
4. Abre una nueva ventana de `cmd` y verifica:
   ```batch
   java -version
   echo %JAVA_HOME%
   ```
Deberías ver una versión 17.

Si algún otro programa necesita Java 25, no hagas esto y usa sólo el script.

### Próximos Pasos para Diagnosticar el Crash

Una vez que el proyecto compile correctamente:

1. **Instalar la APK en el dispositivo**:
   ```batch
   gradlew.bat installDebug
   ```

2. **Capturar logs al hacer click en una polera**:
   ```batch
   adb logcat -v threadtime | findstr "appajicolorgrupo4"
   ```

3. **Revisar el código de DetalleProductoScreen.kt**:
   - Línea 46-100: Verificar carga del producto
   - Revisar si el `productoId` es válido
   - Verificar los ViewModels (ProductoViewModel, CarritoViewModel)

### Archivos Modificados

- ✅ `viewmodel/UsuarioViewModel.kt`
- ✅ `test/data/repository/UserRepositoryTest.kt`
- ✅ `data/local/database/AppDatabase.kt`
- ✅ `gradle.properties` (revertido)

### Notas Importantes

- El proyecto usa **Room Database** para datos locales
- **UserEntity** ya NO tiene campo `clave`/`password` (se eliminó por seguridad)
- La autenticación ahora se maneja vía API REST
- El campo `telefono` tiene valor por defecto `""`

### Causas Posibles del Crash al Seleccionar Polera

Una vez que compile, revisar:
1. **NullPointerException** en `DetalleProductoScreen` si el producto no existe
2. **Error en ViewModel** al cargar datos del producto
3. **Problema de navegación** con parámetros inválidos
4. **Error en base de datos** al intentar guardar en carrito
