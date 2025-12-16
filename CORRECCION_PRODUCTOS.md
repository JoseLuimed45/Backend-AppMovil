# 🛠️ Corrección: Productos - No se pueden agregar ni ver en catálogo

## 📅 Fecha: 2025-12-16

---

## 🐛 Problema Identificado

**Síntomas:**
- No se pueden agregar productos desde la app Android
- El catálogo no muestra productos
- Posibles errores 400 (Bad Request) o 404 (Not Found)

**Causa raíz:**
Desajuste entre los nombres de campos que usa el backend y los que espera la app Android.

---

## 📊 Comparación de Campos

### ❌ Antes (Backend)
```javascript
{
  name: "Camiseta",
  description: "...",
  price: 15000,
  category: "...",
  stock: 10,
  image: "..."
}
```

### ✅ Después (Backend - alineado con Android)
```javascript
{
  id: "PROD-001",
  nombre: "Camiseta",
  descripcion: "...",
  precio: 15000,
  categoria: "SERIGRAFIA",
  stock: 10,
  imagenUrl: "..."
}
```

---

## ✅ Correcciones Implementadas

### 1. **Modelo Product actualizado** (`api/models/Product.js`)

```javascript
const productSchema = new mongoose.Schema({
  id: { type: String, required: true, unique: true },      // ✅ NUEVO - ID personalizado
  nombre: { type: String, required: true, trim: true },    // ✅ name → nombre
  descripcion: { type: String, default: '' },             // ✅ description → descripcion
  precio: { type: Number, required: true, min: 0.01 },    // ✅ price → precio
  categoria: { type: String, required: true },             // ✅ category → categoria
  stock: { type: Number, default: 0, min: 0 },            // ✅ igual
  imagenUrl: { type: String, default: '' },               // ✅ image → imagenUrl
}, { timestamps: true });
```

### 2. **POST /api/products** - Crear producto

**Cambios:**
- Requiere campo `id` único
- Usa nombres en español: `nombre`, `descripcion`, `precio`, `categoria`, `imagenUrl`
- Valida que el ID no esté duplicado
- Mensajes de error en formato `{ message: "..." }`

**Request esperado:**
```json
{
  "id": "PROD-001",
  "nombre": "Camiseta Serigrafía",
  "descripcion": "Camiseta con diseño personalizado",
  "precio": 15000,
  "categoria": "SERIGRAFIA",
  "stock": 10,
  "imagenUrl": "https://..."
}
```

**Response exitoso (201):**
```json
{
  "_id": "675f...",
  "id": "PROD-001",
  "nombre": "Camiseta Serigrafía",
  "descripcion": "Camiseta con diseño personalizado",
  "precio": 15000,
  "categoria": "SERIGRAFIA",
  "stock": 10,
  "imagenUrl": "https://...",
  "createdAt": "2025-12-16T...",
  "updatedAt": "2025-12-16T..."
}
```

### 3. **GET /api/products/:id** - Obtener por ID

**Cambios:**
- Busca por el campo `id` personalizado (no por `_id` de MongoDB)
- Mensaje de error estandarizado

### 4. **PUT /api/products/:id** - Actualizar producto

**Cambios:**
- Busca por campo `id` personalizado
- Acepta campos en español: `nombre`, `descripcion`, `precio`, etc.
- Validaciones de precio y stock

### 5. **DELETE /api/products/:id** - Eliminar producto

**Cambios:**
- Busca y elimina por campo `id` personalizado
- Mensaje de confirmación

### 6. **GET /api/products** - Listar todos

No requiere cambios, devuelve todos los productos.

---

## 🧪 Testing

### Script de Test Automatizado

```bash
# Ejecutar tests contra servidor local
node test-products.js

# Ejecutar tests contra Vercel
BASE_URL=https://backend-app-movil.vercel.app node test-products.js
```

### Test Manual con cURL

#### 1. Crear producto:
```bash
curl -i -X POST https://backend-app-movil.vercel.app/api/products \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <TOKEN_ADMIN>" \
  -d '{
    "id":"PROD-001",
    "nombre":"Camiseta Test",
    "descripcion":"Producto de prueba",
    "precio":15000,
    "categoria":"SERIGRAFIA",
    "stock":10,
    "imagenUrl":"https://via.placeholder.com/300"
  }'
```

#### 2. Listar productos:
```bash
curl -i https://backend-app-movil.vercel.app/api/products
```

#### 3. Obtener por ID:
```bash
curl -i https://backend-app-movil.vercel.app/api/products/PROD-001
```

#### 4. Actualizar:
```bash
curl -i -X PUT https://backend-app-movil.vercel.app/api/products/PROD-001 \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <TOKEN_ADMIN>" \
  -d '{"precio":18000,"stock":15}'
```

#### 5. Eliminar:
```bash
curl -i -X DELETE https://backend-app-movil.vercel.app/api/products/PROD-001 \
  -H "Authorization: Bearer <TOKEN_ADMIN>"
```

---

## 🚀 Despliegue

### Paso 1: Commit y Push
```bash
cd c:\Users\josel\AndroidStudioProjects\AppMovil\ajicolor_backend

git add api/models/Product.js api/routes/products.js
git commit -m "fix: alinear campos de productos con app Android (español)"
git push origin main
```

### Paso 2: Vercel desplegará automáticamente

### Paso 3: Probar en producción
```bash
BASE_URL=https://backend-app-movil.vercel.app node test-products.js
```

---

## 📱 App Android

### No requiere cambios

La app Android ya está configurada para:
- Enviar productos con campos en español
- Recibir productos con campos en español
- El modelo `Product.kt` coincide con la nueva estructura del backend

```kotlin
data class Product(
    val id: String,
    val nombre: String,
    val descripcion: String,
    val precio: Int,
    val categoria: String,
    val stock: Int,
    val imagenUrl: String,
    // ...
)
```

---

## 🔍 Verificación

### Checklist después del despliegue:

- [ ] Backend desplegado en Vercel
- [ ] Script de test ejecutado: `node test-products.js`
- [ ] Todos los tests pasan ✅
- [ ] GET /api/products devuelve productos
- [ ] POST /api/products crea correctamente
- [ ] PUT /api/products actualiza correctamente
- [ ] DELETE /api/products elimina correctamente
- [ ] App Android puede:
  - [ ] Listar productos en el catálogo
  - [ ] Ver detalle de producto
  - [ ] Agregar productos (Admin)
  - [ ] Editar productos (Admin)
  - [ ] Eliminar productos (Admin)

---

## 🐛 Troubleshooting

### Problema: No se pueden listar productos

**Verificar:**
```bash
curl -i https://backend-app-movil.vercel.app/api/products
```

**Esperado:** Array con productos (puede estar vacío `[]`)

**Si da error 500:**
- Verificar conexión a MongoDB
- Verificar logs en Vercel Dashboard

### Problema: No se pueden crear productos (401)

**Causa:** Token de autorización faltante o inválido

**Solución:**
1. Hacer login como admin
2. Obtener el token
3. Enviarlo en header: `Authorization: Bearer <TOKEN>`

### Problema: Error 409 "Ya existe un producto con ese ID"

**Causa:** El campo `id` debe ser único

**Solución:** Usar un ID diferente (ej: `PROD-002`, `PROD-003`, etc.)

### Problema: El catálogo no muestra productos

**Verificar:**
1. Que el backend esté desplegado con los cambios
2. Que haya productos en MongoDB
3. Logs de la app Android:
   ```powershell
   & "$env:LOCALAPPDATA\Android\Sdk\platform-tools\adb.exe" logcat | Select-String "ProductRepository|Catalogo"
   ```

---

## 📊 Estructura de la Base de Datos

### Colección: `products`

```javascript
{
  "_id": ObjectId("675f..."),
  "id": "PROD-001",                    // ✅ ID personalizado único
  "nombre": "Camiseta Serigrafía",
  "descripcion": "Camiseta con diseño personalizado",
  "precio": 15000,
  "categoria": "SERIGRAFIA",
  "stock": 10,
  "imagenUrl": "https://...",
  "createdAt": ISODate("2025-12-16T..."),
  "updatedAt": ISODate("2025-12-16T...")
}
```

### Índices necesarios:
- `id`: único (creado automáticamente por `unique: true`)
- `_id`: único (MongoDB por defecto)

---

## 📝 Notas Importantes

1. **Campo `id` es diferente de `_id`:**
   - `_id`: ObjectId de MongoDB (generado automáticamente)
   - `id`: String personalizado (requerido por la app Android)

2. **Categorías válidas:**
   - `SERIGRAFIA`
   - `DTF`
   - `CORPORATIVA`
   - `ACCESORIOS`

3. **El precio es un número (no string):**
   - Debe ser > 0
   - En la app se maneja como `Int` (sin decimales)

4. **Stock es un entero:**
   - Debe ser >= 0
   - No puede ser negativo

5. **imagenUrl es opcional:**
   - Si se proporciona, debe ser una URL válida (http:// o https://)
   - Puede estar vacío: `""`

---

## ✅ Resumen de Archivos Modificados

| Archivo | Cambios |
|---------|---------|
| `api/models/Product.js` | Campos renombrados a español, agregado campo `id` |
| `api/routes/products.js` | Todas las rutas actualizadas para usar campos en español |
| `test-products.js` | **NUEVO** - Script de pruebas automatizadas |

---

## 🎯 Resultado Esperado

Después de desplegar los cambios:

1. **Backend responde con estructura correcta:**
   ```json
   {
     "id": "PROD-001",
     "nombre": "...",
     "descripcion": "...",
     "precio": 15000,
     "categoria": "SERIGRAFIA",
     "stock": 10,
     "imagenUrl": "..."
   }
   ```

2. **App Android puede:**
   - ✅ Ver productos en el catálogo
   - ✅ Ver detalle de cada producto
   - ✅ Agregar productos al carrito
   - ✅ (Admin) Crear, editar y eliminar productos

3. **MongoDB almacena:**
   - ✅ Productos con campos en español
   - ✅ Campo `id` único para cada producto
   - ✅ Timestamps de creación y actualización

---

**Siguiente paso:** Desplegar a Vercel y probar la funcionalidad completa en la app Android.

