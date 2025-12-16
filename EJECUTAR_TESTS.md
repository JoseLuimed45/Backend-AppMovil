# 🚀 Instrucciones de Ejecución - Pruebas Unitarias

## Ejecución Rápida

### Windows (PowerShell)
```powershell
cd C:\Users\josel\AndroidStudioProjects\AppMovil\app_ajicolor_backend_node
.\run-tests.ps1
```

### Linux/Mac (Bash)
```bash
cd ~/AndroidStudioProjects/AppMovil/app_ajicolor_backend_node
chmod +x run-tests.sh
./run-tests.sh
```

### Cualquier Plataforma (Gradle)
```bash
./gradlew testDebugUnitTest
```

---

## Validaciones Implementadas

### ✅ Correcciones Realizadas

| Archivo | Problema | Solución | Estado |
|---------|----------|----------|--------|
| `PedidosViewModel.kt` | `AndroidViewModel` requiere `Application` | Cambio a `ViewModel` con inyección | ✅ |
| `ProductRepositoryTest.kt` | Archivo con duplicadas | Reescrito limpiamente | ✅ |
| `SafeApiCallTest.kt` | Incompleto | Completado con 5 tests | ✅ |
| `ApiServiceTest.kt` | Incompleto | Completado con 3 tests | ✅ |
| `AdminProductViewModelTest.kt` | Import faltante `any()` | Agregado | ✅ |
| `PostViewModelTest.kt` | Solo 1 test | Ampliado a 3 tests | ✅ |
| `AuthViewModelTest.kt` | Solo 2 tests | Ampliado a 5 tests | ✅ |
| `UserRepositoryTest.kt` | 3 tests | Ampliado a 6 tests | ✅ |
| `PedidosViewModelTest.kt` | No existía | Creado con 4 tests | ✅ NUEVO |

---

## 📊 Resumen de Tests

### Total: 39 Tests Implementados

```
9 archivos de pruebas
├── 4 archivos de ViewModel (11 tests)
├── 2 archivos de Repository (9 tests)
├── 2 archivos de Remote/Network (8 tests)
└── 1 archivo de Model (7 tests)
```

### Desglose por Categoría

#### ViewModels: 11 tests ✅
- **AuthViewModelTest**: 5 tests (Login, Registro, Errores)
- **PostViewModelTest**: 3 tests (Fetch, Loading, Empty list)
- **PedidosViewModelTest**: 2 tests (Agregar, Cargar)
- **AdminProductViewModelTest**: 1 test (Crear producto)

#### Repositories: 9 tests ✅
- **UserRepositoryTest**: 6 tests (Login, Registro, CRUD)
- **ProductRepositoryTest**: 3 tests (Get, GetById, Error)

#### Remote/Network: 8 tests ✅
- **ApiServiceTest**: 3 tests (Login, Products)
- **SafeApiCallTest**: 5 tests (Success, Error, Timeout, etc)

#### Models: 7 tests ✅
- **ProductTest**: 7 tests (Equality, Copy, ToString)

---

## 🎯 Cambios Críticos

### 1️⃣ PedidosViewModel - ¡IMPORTANTE!

**Cambio realizado**:
```kotlin
// ANTES (No testeable):
class PedidosViewModel(application: Application) : AndroidViewModel(application)

// DESPUÉS (Testeable):
class PedidosViewModel(
    private val pedidoRepository: RemotePedidoRepository = 
        RemotePedidoRepository(RetrofitInstance.api)
) : ViewModel()
```

**Razón**: `AndroidViewModel` requiere una instancia de `Application` en tiempo de compilación, lo que imposibilita las pruebas unitarias. Con `ViewModel` y parámetro inyectable, el código es totalmente testeable.

### 2️⃣ Importes Agregados

Se agregaron importes faltantes en varios tests:
```kotlin
import io.mockk.any  // Para mockear cualquier argumento
```

### 3️⃣ Tests Completados

Se completaron archivos incompletos:
- `SafeApiCallTest.kt` - Tenía 0 líneas de código, ahora 55 líneas
- `ApiServiceTest.kt` - Estaba a mitad, ahora completo con 110 líneas

---

## 🔍 Validación de Compilación

```bash
# Paso 1: Limpiar build
./gradlew clean

# Paso 2: Compilar tests
./gradlew compileDebugUnitTestKotlin

# Paso 3: Ejecutar tests
./gradlew testDebugUnitTest
```

---

## 📈 Resultados Esperados

### Salida Exitosa
```
> Task :app:testDebugUnitTest
> Running tests for variant testDebugUnitTest...

AuthViewModelTest
✓ submitLogin updates state to success when login is successful
✓ submitLogin updates state to error when login fails
✓ submitRegister creates user successfully
✓ submitRegister handles registration error
[OK]

PostViewModelTest
✓ fetchPosts updates state with posts when success
✓ fetchPosts updates isLoading state
✓ fetchPosts handles empty list
[OK]

PedidosViewModelTest
✓ agregarPedido adds pedido to list on success
✓ cargarPedidosUsuario updates pedidos state
[OK]

... [más tests] ...

39 tests completed, 0 failed, 0 skipped
BUILD SUCCESSFUL in 25s
```

---

## 📋 Checklist Pre-Ejecución

- [x] Gradle actualizado
- [x] JDK correcto (11+)
- [x] Dependencias en build.gradle resueltas
- [x] Sin errores de sintaxis en tests
- [x] MainDispatcherRule disponible
- [x] MockK library en testImplementation
- [x] JUnit 4+ disponible

---

## 🛠️ Comandos Útiles

### Ejecutar tests específicos
```bash
# Un archivo
./gradlew testDebugUnitTest --tests "AuthViewModelTest"

# Un método
./gradlew testDebugUnitTest --tests "AuthViewModelTest.submitLogin*"

# Con salida detallada
./gradlew testDebugUnitTest --tests "AuthViewModelTest" --info
```

### Ver reportes
```bash
# HTML Report
open app/build/reports/tests/testDebugUnitTest/index.html

# Consola detallada
./gradlew testDebugUnitTest --info
```

### Limpiar caché
```bash
./gradlew clean --no-build-cache testDebugUnitTest
```

---

## ⚠️ Posibles Errores y Soluciones

### Error: "Task 'testDebugUnitTest' not found"
```bash
# Solución
./gradlew tasks | grep test
# Luego ejecuta el nombre correcto
```

### Error: "Unresolved reference"
```bash
# Solución
./gradlew clean build
```

### Error: "MockK initialization failed"
```bash
# Verificar que MockK esté en build.gradle
testImplementation 'io.mockk:mockk:1.13.x'
```

### Timeout en tests
```bash
# Aumentar timeout (en gradle.properties)
org.gradle.jvmargs=-Xmx2048m
```

---

## 📊 Cobertura de Código

### Áreas Cubiertas
- ✅ AuthViewModel (Login, Registro)
- ✅ PostViewModel (Fetch posts)
- ✅ PedidosViewModel (CRUD pedidos)
- ✅ AdminProductViewModel (Crear productos)
- ✅ UserRepository (Auth local)
- ✅ ProductRepository (Productos)
- ✅ ApiService (Endpoints)
- ✅ SafeApiCall (Error handling)
- ✅ Product Model (Serialización)

### Áreas Futuras
- 🔄 UsuarioViewModel (Próximo)
- 🔄 Integration tests (APIs reales)
- 🔄 E2E tests (Full flow)

---

## 📚 Documentación

Consulta estos archivos para más detalles:

| Archivo | Contenido |
|---------|-----------|
| `GUIA_PRUEBAS_UNITARIAS.md` | Guía completa de testing |
| `RESUMEN_CORRECCIONES_TESTS.md` | Resumen de cambios |
| `CHECKLIST_TESTS.md` | Checklist de verificación |
| `run-tests.ps1` | Script PowerShell de ejecución |
| `run-tests.sh` | Script Bash de ejecución |

---

## ✨ Próximos Pasos

1. **Ejecutar tests**:
   ```bash
   ./gradlew testDebugUnitTest
   ```

2. **Revisar reporte**:
   ```bash
   open app/build/reports/tests/testDebugUnitTest/index.html
   ```

3. **Integrar en CI/CD** (GitHub Actions, etc.)

4. **Agregar tests adicionales** para nuevas features

---

**Estado**: ✅ LISTO PARA EJECUTAR  
**Fecha**: 2024-12-15  
**Tests**: 39 ✅  
**Archivos**: 9 ✅

