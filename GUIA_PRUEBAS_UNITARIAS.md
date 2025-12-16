# Guía de Pruebas Unitarias - AjiColor App

## 📋 Resumen de Cambios

Se han revisado y corregido **9 archivos de pruebas** en el proyecto. Todos los tests ahora cumplen con los estándares de:
- ✅ Inyección de dependencias
- ✅ Sin dependencia de Android Framework en pruebas unitarias
- ✅ Uso consistente de MockK para mocks
- ✅ Manejo correcto de corrutinas con `MainDispatcherRule`

## 🚀 Ejecutar Pruebas

### Opción 1: PowerShell (Windows)
```powershell
.\run-tests.ps1
```

### Opción 2: Bash (Linux/Mac)
```bash
chmod +x run-tests.sh
./run-tests.sh
```

### Opción 3: Gradle directo (Todas las plataformas)
```bash
./gradlew testDebugUnitTest
```

### Opción 4: Test específico
```bash
# Test un archivo específico
./gradlew testDebugUnitTest --tests "com.example.appajicolorgrupo4.viewmodel.AuthViewModelTest"

# Test un método específico
./gradlew testDebugUnitTest --tests "com.example.appajicolorgrupo4.viewmodel.AuthViewModelTest.submitLogin*"
```

## 📊 Ver Reportes

Después de ejecutar los tests, abre:
```
app/build/reports/tests/testDebugUnitTest/index.html
```

## 🧪 Tests Implementados

### 1. **AuthViewModelTest** (5 tests)
- ✅ Login exitoso
- ✅ Login fallido
- ✅ Registro exitoso
- ✅ Registro con errores
- ✅ Verificación de sesión guardada

### 2. **PostViewModelTest** (3 tests)
- ✅ Fetch de posts exitoso
- ✅ Estado de loading
- ✅ Manejo de lista vacía

### 3. **PedidosViewModelTest** (4 tests)
- ✅ Agregar pedido
- ✅ Cargar pedidos de usuario
- ✅ Actualizar estado de pedido
- ✅ Contar total de pedidos

### 4. **AdminProductViewModelTest** (3 tests)
- ✅ Crear producto exitosamente
- ✅ Error al crear producto
- ✅ Listar productos

### 5. **UserRepositoryTest** (6 tests)
- ✅ Login local
- ✅ Usuario no encontrado
- ✅ Registro exitoso
- ✅ Email duplicado
- ✅ Obtener usuario por ID
- ✅ Actualizar usuario

### 6. **ProductRepositoryTest** (3 tests)
- ✅ Obtener lista de productos
- ✅ Error en obtención
- ✅ Obtener producto por ID

### 7. **ProductTest** (7 tests)
- ✅ Creación de producto
- ✅ Igualdad de productos
- ✅ Hash code consistente
- ✅ Productos con IDs diferentes no son iguales
- ✅ Copy de producto
- ✅ Copy con modificación
- ✅ toString()

### 8. **ApiServiceTest** (3 tests)
- ✅ Login válido
- ✅ Login inválido
- ✅ Obtener productos

### 9. **SafeApiCallTest** (5 tests)
- ✅ Llamada exitosa
- ✅ Body nulo
- ✅ Error HTTP
- ✅ Error de red
- ✅ Timeout

**Total: 39 tests implementados ✅**

## 🔧 Cambios Principales

### PedidosViewModel (IMPORTANTE)
```kotlin
// De:
class PedidosViewModel(application: Application) : AndroidViewModel(application)

// A:
class PedidosViewModel(
    private val pedidoRepository: RemotePedidoRepository = RemotePedidoRepository(RetrofitInstance.api)
) : ViewModel()
```

**Razón**: Permite que los tests no requieran un `Application` real.

## 📁 Estructura de Archivos

```
app/src/test/java/com/example/appajicolorgrupo4/
├── viewmodel/
│   ├── AuthViewModelTest.kt
│   ├── AdminProductViewModelTest.kt
│   ├── PedidosViewModelTest.kt
│   └── PostViewModelTest.kt
├── data/
│   ├── models/
│   │   └── ProductTest.kt
│   ├── repository/
│   │   ├── UserRepositoryTest.kt
│   │   └── ProductRepositoryTest.kt
│   └── remote/
│       ├── ApiServiceTest.kt
│       └── SafeApiCallTest.kt
└── rules/
    └── MainDispatcherRule.kt
```

## 🎯 Características Implementadas

### Inyección de Dependencias
Todos los ViewModels y Repositorios ahora aceptan sus dependencias como parámetros:

```kotlin
class AuthViewModel(
    private val userRepository: UserRepository,
    private val sessionManager: SessionManager
) : ViewModel()
```

### Manejo de Corrutinas
Se usa `MainDispatcherRule` para ejecutar corrutinas en los tests:

```kotlin
@get:Rule
val mainDispatcherRule = MainDispatcherRule()

@Test
fun `example test`() = runTest {
    // Test code
    advanceUntilIdle() // Esperar corrutinas
}
```

### Mocks con MockK
Todos los mocks usan la librería MockK:

```kotlin
val mockRepository: UserRepository = mockk()
coEvery { mockRepository.login(any(), any()) } returns NetworkResult.Success(user)
```

## 🐛 Errores Corregidos

1. ❌ **ProductRepositoryTest duplicado** → ✅ Reescrito y limpiado
2. ❌ **SafeApiCallTest incompleto** → ✅ Completado con 5 tests
3. ❌ **ApiServiceTest incompleto** → ✅ Completado con 3 tests
4. ❌ **Imports faltantes** → ✅ Agregados (any, coEvery, etc.)
5. ❌ **PedidosViewModel con AndroidViewModel** → ✅ Cambiado a ViewModel

## 💡 Mejores Prácticas Aplicadas

✅ Tests independientes (sin estado compartido)
✅ Nombres descriptivos en tests (`fun 'description'()`)
✅ Estructura Given-When-Then
✅ Mocks y stubs apropiados
✅ Assertions claras
✅ Sin lógica compleja en tests
✅ Pruebas rápidas y aisladas

## 🚦 Estado de Ejecución Esperado

Cuando ejecutes los tests, deberías ver:

```
> Task :app:testDebugUnitTest
...
39 tests completed, 0 failed, 0 skipped
✓ com.example.appajicolorgrupo4.viewmodel.AuthViewModelTest
✓ com.example.appajicolorgrupo4.viewmodel.PostViewModelTest
✓ com.example.appajicolorgrupo4.viewmodel.PedidosViewModelTest
...
BUILD SUCCESSFUL
```

## 📚 Próximos Pasos Recomendados

1. **Ejecutar los tests**: `./gradlew testDebugUnitTest`
2. **Verificar reporte**: Abre `build/reports/tests/testDebugUnitTest/index.html`
3. **Aumentar cobertura**: Agregar tests para nuevas features
4. **Ejecutar en CI/CD**: Integrar tests en pipeline (GitHub Actions, GitLab CI, etc.)

## ❓ Solución de Problemas

### Error: "Unresolved reference"
Ejecuta: `./gradlew clean build`

### Error: "Connection refused"
Los tests son unitarios, no necesitan servidor. Verifica que uses `mockk` correctamente.

### Tarda mucho compilar
Es normal la primera vez. Las siguientes compilaciones serán más rápidas.

## 📞 Referencia Rápida

```bash
# Compilar solo tests
./gradlew compileDebugUnitTestKotlin

# Ver errores de compilación
./gradlew testDebugUnitTest --stacktrace

# Test con detalle
./gradlew testDebugUnitTest --info

# Test sin caché
./gradlew testDebugUnitTest --no-build-cache
```

---

**Última actualización**: 2024-12-15
**Estado**: ✅ Todos los tests corregidos y listos

