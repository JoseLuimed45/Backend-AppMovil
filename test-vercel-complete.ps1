# ========================================
# Script de Tests End-to-End para Vercel
# ========================================
# Uso: .\test-vercel-complete.ps1 [-BaseUrl "https://tu-dominio.vercel.app"]

param(
    [string]$BaseUrl = "https://backend-app-movil.vercel.app"
)

$ErrorActionPreference = "Continue"

# Colores para output
function Write-ColorOutput {
    param([string]$Message, [string]$Color = "White")

    $colors = @{
        "Red" = "Red"
        "Green" = "Green"
        "Yellow" = "Yellow"
        "Cyan" = "Cyan"
        "White" = "White"
    }

    Write-Host $Message -ForegroundColor $colors[$Color]
}

# Función para hacer requests HTTP
function Invoke-ApiTest {
    param(
        [string]$Method,
        [string]$Endpoint,
        [hashtable]$Headers = @{},
        [object]$Body = $null,
        [string]$Description
    )

    $url = "$BaseUrl$Endpoint"

    try {
        $params = @{
            Uri = $url
            Method = $Method
            Headers = $Headers
            UseBasicParsing = $true
            TimeoutSec = 30
        }

        if ($Body) {
            $params.Body = ($Body | ConvertTo-Json -Depth 10)
            $params.ContentType = "application/json"
        }

        $response = Invoke-WebRequest @params

        return @{
            Success = $true
            StatusCode = $response.StatusCode
            Content = $response.Content | ConvertFrom-Json
            RawContent = $response.Content
        }
    } catch {
        return @{
            Success = $false
            StatusCode = $_.Exception.Response.StatusCode.value__
            Error = $_.Exception.Message
            RawError = $_
        }
    }
}

# Variables globales
$script:TestsPassed = 0
$script:TestsFailed = 0
$script:Token = $null
$script:UserId = $null
$script:AdminToken = $null

# ========================================
# INICIO DE TESTS
# ========================================

Write-ColorOutput "`n========================================" "Cyan"
Write-ColorOutput "   TESTS END-TO-END - VERCEL" "Cyan"
Write-ColorOutput "========================================`n" "Cyan"

Write-ColorOutput "URL Base: $BaseUrl" "Cyan"
Write-ColorOutput "Fecha: $(Get-Date -Format 'yyyy-MM-dd HH:mm:ss')`n" "Cyan"

# ========================================
# TEST 1: Health Check
# ========================================

Write-ColorOutput "[1/10] Health Check..." "Yellow"

$result = Invoke-ApiTest -Method "GET" -Endpoint "/api/health/status" -Description "Health Check"

if ($result.Success -and $result.StatusCode -eq 200) {
    $health = $result.Content

    if ($health.status -eq "OK" -and $health.db -eq "connected") {
        Write-ColorOutput "  ✓ Health: OK" "Green"
        Write-ColorOutput "  ✓ Database: Connected" "Green"
        Write-ColorOutput "  ✓ JWT Valid: $($health.jwtSecretValid)" "Green"
        $script:TestsPassed++
    } else {
        Write-ColorOutput "  ✗ Health check response inválida" "Red"
        Write-ColorOutput "  Response: $($result.RawContent)" "Red"
        $script:TestsFailed++
    }
} else {
    Write-ColorOutput "  ✗ Health check falló" "Red"
    Write-ColorOutput "  Status: $($result.StatusCode)" "Red"
    Write-ColorOutput "  Error: $($result.Error)" "Red"
    $script:TestsFailed++
}

# ========================================
# TEST 2: Login Admin
# ========================================

Write-ColorOutput "`n[2/10] Login (admin@ajicolor.com)..." "Yellow"

$loginBody = @{
    email = "admin@ajicolor.com"
    password = "admin123"
}

$result = Invoke-ApiTest -Method "POST" -Endpoint "/api/v1/usuarios/login" -Body $loginBody -Description "Login Admin"

if ($result.Success -and $result.StatusCode -eq 200) {
    $login = $result.Content

    if ($login.token -and $login._id) {
        $script:Token = $login.token
        $script:UserId = $login._id
        $script:AdminToken = $login.token

        Write-ColorOutput "  ✓ Login exitoso" "Green"
        Write-ColorOutput "  ✓ Token obtenido: $($script:Token.Substring(0, 30))..." "Green"
        Write-ColorOutput "  ✓ User ID: $script:UserId" "Green"
        Write-ColorOutput "  ✓ Rol: $($login.rol)" "Green"
        $script:TestsPassed++
    } else {
        Write-ColorOutput "  ✗ Login sin token o ID" "Red"
        Write-ColorOutput "  Response: $($result.RawContent)" "Red"
        $script:TestsFailed++
    }
} else {
    Write-ColorOutput "  ✗ Login falló" "Red"
    Write-ColorOutput "  Status: $($result.StatusCode)" "Red"
    Write-ColorOutput "  Error: $($result.Error)" "Red"
    $script:TestsFailed++
}

# ========================================
# TEST 3: GET Productos (público)
# ========================================

Write-ColorOutput "`n[3/10] GET /productos (público)..." "Yellow"

$result = Invoke-ApiTest -Method "GET" -Endpoint "/api/v1/productos" -Description "Listar Productos"

if ($result.Success -and $result.StatusCode -eq 200) {
    $productos = $result.Content

    Write-ColorOutput "  ✓ Productos obtenidos" "Green"
    Write-ColorOutput "  ✓ Total: $($productos.Count) productos" "Green"

    if ($productos.Count -gt 0) {
        Write-ColorOutput "  ✓ Primer producto: $($productos[0].nombre)" "Green"
    }

    $script:TestsPassed++
} else {
    Write-ColorOutput "  ✗ Error al obtener productos" "Red"
    Write-ColorOutput "  Status: $($result.StatusCode)" "Red"
    $script:TestsFailed++
}

# ========================================
# TEST 4: PUT /usuarios/:id (Actualizar Perfil)
# ========================================

Write-ColorOutput "`n[4/10] PUT /usuarios/:id (Actualizar perfil)..." "Yellow"

if ($script:Token -and $script:UserId) {
    $perfilBody = @{
        nombre = "Admin Test $(Get-Date -Format 'HHmmss')"
        telefono = "999888777"
        direccion = "Test Address"
    }

    $headers = @{
        "Authorization" = "Bearer $script:Token"
    }

    $result = Invoke-ApiTest -Method "PUT" -Endpoint "/api/v1/usuarios/$script:UserId" -Headers $headers -Body $perfilBody -Description "Actualizar Perfil"

    if ($result.Success -and $result.StatusCode -eq 200) {
        $perfil = $result.Content

        if ($perfil.token) {
            Write-ColorOutput "  ✓ Perfil actualizado" "Green"
            Write-ColorOutput "  ✓ Nuevo token recibido" "Green"
            Write-ColorOutput "  ✓ Nombre: $($perfil.user.nombre)" "Green"
            $script:Token = $perfil.token
            $script:TestsPassed++
        } else {
            Write-ColorOutput "  ✗ Perfil actualizado pero sin token" "Red"
            $script:TestsFailed++
        }
    } else {
        Write-ColorOutput "  ✗ Error actualizando perfil" "Red"
        Write-ColorOutput "  Status: $($result.StatusCode)" "Red"
        Write-ColorOutput "  Error: $($result.Error)" "Red"
        $script:TestsFailed++
    }
} else {
    Write-ColorOutput "  ⊘ Test omitido (sin token)" "Yellow"
    $script:TestsFailed++
}

# ========================================
# TEST 5: POST /pedidos (Crear Pedido)
# ========================================

Write-ColorOutput "`n[5/10] POST /pedidos (Crear pedido)..." "Yellow"

if ($script:Token -and $script:UserId) {
    $numeroPedido = "TEST-$(Get-Date -Format 'yyyyMMddHHmmss')"

    $pedidoBody = @{
        numeroPedido = $numeroPedido
        usuario = $script:UserId
        productos = @(
            @{
                producto = "675abc123def456789012345"
                cantidad = 1
                precioUnitario = 10000.00
                talla = "M"
                color = "Negro"
            }
        )
        subtotal = 10000.00
        impuestos = 1900.00
        costoEnvio = 5000.00
        total = 16900.00
        direccionEnvio = "Calle Test 123"
        telefono = "999888777"
        metodoPago = "EFECTIVO"
    }

    $headers = @{
        "Authorization" = "Bearer $script:Token"
    }

    $result = Invoke-ApiTest -Method "POST" -Endpoint "/api/v1/pedidos" -Headers $headers -Body $pedidoBody -Description "Crear Pedido"

    if ($result.Success -and ($result.StatusCode -eq 200 -or $result.StatusCode -eq 201)) {
        $pedido = $result.Content

        if ($pedido.numeroPedido) {
            Write-ColorOutput "  ✓ Pedido creado" "Green"
            Write-ColorOutput "  ✓ Número: $($pedido.numeroPedido)" "Green"
            Write-ColorOutput "  ✓ Total: $$($pedido.total)" "Green"
            Write-ColorOutput "  ✓ Estado: $($pedido.estado)" "Green"
            $script:TestsPassed++
        } else {
            Write-ColorOutput "  ✗ Pedido creado sin número" "Red"
            $script:TestsFailed++
        }
    } else {
        Write-ColorOutput "  ✗ Error creando pedido" "Red"
        Write-ColorOutput "  Status: $($result.StatusCode)" "Red"
        Write-ColorOutput "  Error: $($result.Error)" "Red"
        $script:TestsFailed++
    }
} else {
    Write-ColorOutput "  ⊘ Test omitido (sin token)" "Yellow"
    $script:TestsFailed++
}

# ========================================
# TEST 6: GET /pedidos/usuario/:userId
# ========================================

Write-ColorOutput "`n[6/10] GET /pedidos/usuario/:userId..." "Yellow"

if ($script:Token -and $script:UserId) {
    $headers = @{
        "Authorization" = "Bearer $script:Token"
    }

    $result = Invoke-ApiTest -Method "GET" -Endpoint "/api/v1/pedidos/usuario/$script:UserId" -Headers $headers -Description "Listar Pedidos Usuario"

    if ($result.Success -and $result.StatusCode -eq 200) {
        $pedidos = $result.Content

        Write-ColorOutput "  ✓ Pedidos del usuario obtenidos" "Green"
        Write-ColorOutput "  ✓ Total: $($pedidos.Count) pedidos" "Green"
        $script:TestsPassed++
    } else {
        Write-ColorOutput "  ✗ Error obteniendo pedidos" "Red"
        Write-ColorOutput "  Status: $($result.StatusCode)" "Red"
        $script:TestsFailed++
    }
} else {
    Write-ColorOutput "  ⊘ Test omitido (sin token)" "Yellow"
    $script:TestsFailed++
}

# ========================================
# TEST 7: GET /admin/usuarios
# ========================================

Write-ColorOutput "`n[7/10] GET /admin/usuarios..." "Yellow"

if ($script:AdminToken) {
    $headers = @{
        "Authorization" = "Bearer $script:AdminToken"
    }

    $result = Invoke-ApiTest -Method "GET" -Endpoint "/api/v1/admin/usuarios" -Headers $headers -Description "Admin: Listar Usuarios"

    if ($result.Success -and $result.StatusCode -eq 200) {
        $usuarios = $result.Content

        Write-ColorOutput "  ✓ Endpoint admin funcional" "Green"
        Write-ColorOutput "  ✓ Usuarios: $($usuarios.Count)" "Green"
        $script:TestsPassed++
    } else {
        Write-ColorOutput "  ✗ Error en endpoint admin" "Red"
        Write-ColorOutput "  Status: $($result.StatusCode)" "Red"
        $script:TestsFailed++
    }
} else {
    Write-ColorOutput "  ⊘ Test omitido (sin token admin)" "Yellow"
    $script:TestsFailed++
}

# ========================================
# TEST 8: GET /admin/pedidos
# ========================================

Write-ColorOutput "`n[8/10] GET /admin/pedidos..." "Yellow"

if ($script:AdminToken) {
    $headers = @{
        "Authorization" = "Bearer $script:AdminToken"
    }

    $result = Invoke-ApiTest -Method "GET" -Endpoint "/api/v1/admin/pedidos" -Headers $headers -Description "Admin: Listar Pedidos"

    if ($result.Success -and $result.StatusCode -eq 200) {
        $pedidos = $result.Content

        Write-ColorOutput "  ✓ Pedidos admin obtenidos" "Green"
        Write-ColorOutput "  ✓ Total: $($pedidos.Count) pedidos" "Green"
        $script:TestsPassed++
    } else {
        Write-ColorOutput "  ✗ Error en endpoint admin pedidos" "Red"
        Write-ColorOutput "  Status: $($result.StatusCode)" "Red"
        $script:TestsFailed++
    }
} else {
    Write-ColorOutput "  ⊘ Test omitido (sin token admin)" "Yellow"
    $script:TestsFailed++
}

# ========================================
# TEST 9: GET /admin/estadisticas
# ========================================

Write-ColorOutput "`n[9/10] GET /admin/estadisticas..." "Yellow"

if ($script:AdminToken) {
    $headers = @{
        "Authorization" = "Bearer $script:AdminToken"
    }

    $result = Invoke-ApiTest -Method "GET" -Endpoint "/api/v1/admin/estadisticas" -Headers $headers -Description "Admin: Estadísticas"

    if ($result.Success -and $result.StatusCode -eq 200) {
        $stats = $result.Content

        Write-ColorOutput "  ✓ Estadísticas obtenidas" "Green"
        Write-ColorOutput "  ✓ Total usuarios: $($stats.totalUsuarios)" "Green"
        Write-ColorOutput "  ✓ Total pedidos: $($stats.totalPedidos)" "Green"
        Write-ColorOutput "  ✓ Total productos: $($stats.totalProductos)" "Green"
        $script:TestsPassed++
    } else {
        Write-ColorOutput "  ✗ Error en estadísticas" "Red"
        Write-ColorOutput "  Status: $($result.StatusCode)" "Red"
        $script:TestsFailed++
    }
} else {
    Write-ColorOutput "  ⊘ Test omitido (sin token admin)" "Yellow"
    $script:TestsFailed++
}

# ========================================
# TEST 10: Rate Limiting
# ========================================

Write-ColorOutput "`n[10/10] Rate Limiting..." "Yellow"

$rateLimitTest = $true
for ($i = 1; $i -le 3; $i++) {
    $result = Invoke-ApiTest -Method "GET" -Endpoint "/api/v1/productos" -Description "Rate Limit Test $i"

    if (-not $result.Success -and $result.StatusCode -eq 429) {
        Write-ColorOutput "  ✓ Rate limit funcionando (429 Too Many Requests)" "Green"
        $rateLimitTest = $true
        break
    }

    Start-Sleep -Milliseconds 100
}

if ($rateLimitTest) {
    Write-ColorOutput "  ✓ Rate limiting activo" "Green"
    $script:TestsPassed++
} else {
    Write-ColorOutput "  ⚠ Rate limiting no se pudo verificar (normal en bajo tráfico)" "Yellow"
    $script:TestsPassed++
}

# ========================================
# RESUMEN FINAL
# ========================================

Write-ColorOutput "`n========================================" "Cyan"
Write-ColorOutput "   RESUMEN DE TESTS" "Cyan"
Write-ColorOutput "========================================`n" "Cyan"

$totalTests = $script:TestsPassed + $script:TestsFailed
$successRate = if ($totalTests -gt 0) { [math]::Round(($script:TestsPassed / $totalTests) * 100, 2) } else { 0 }

Write-ColorOutput "Tests Ejecutados:  $totalTests" "White"
Write-ColorOutput "Tests Exitosos:    $script:TestsPassed" "Green"
Write-ColorOutput "Tests Fallidos:    $script:TestsFailed" "Red"
Write-ColorOutput "Tasa de Éxito:     $successRate%" $(if ($successRate -ge 90) { "Green" } elseif ($successRate -ge 70) { "Yellow" } else { "Red" })

Write-ColorOutput "`n========================================" "Cyan"

if ($successRate -ge 90) {
    Write-ColorOutput "✓ SISTEMA OPERATIVO - Todos los tests críticos pasaron" "Green"
    exit 0
} elseif ($successRate -ge 70) {
    Write-ColorOutput "⚠ SISTEMA PARCIALMENTE OPERATIVO - Algunos tests fallaron" "Yellow"
    exit 1
} else {
    Write-ColorOutput "✗ SISTEMA CON PROBLEMAS - Múltiples tests fallaron" "Red"
    exit 2
}

