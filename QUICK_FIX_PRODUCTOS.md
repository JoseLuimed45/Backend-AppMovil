# ⚡ Guía Rápida - Corrección Productos

## 🐛 Problema
- No se pueden agregar productos
- El catálogo no muestra productos

## ✅ Solución
Alineados los campos del backend con la app Android (nombres en español).

---

## 🚀 Desplegar Corrección

```bash
# 1. Commit y push
cd c:\Users\josel\AndroidStudioProjects\AppMovil\ajicolor_backend
git add api/models/Product.js api/routes/products.js
git commit -m "fix: alinear campos de productos con app Android"
git push origin main

# 2. Vercel desplegará automáticamente

# 3. Probar
BASE_URL=https://backend-app-movil.vercel.app node test-products.js
```

---

## 📊 Campos Corregidos

| Antes (inglés) | Después (español) |
|----------------|-------------------|
| `name` | `nombre` |
| `description` | `descripcion` |
| `price` | `precio` |
| `category` | `categoria` |
| `stock` | `stock` (igual) |
| `image` | `imagenUrl` |
| *sin campo id* | `id` (NUEVO - requerido) |

---

## 🧪 Test Rápido

### Crear producto:
```bash
curl -i -X POST https://backend-app-movil.vercel.app/api/products \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <TOKEN>" \
  -d '{
    "id":"PROD-001",
    "nombre":"Camiseta Test",
    "precio":15000,
    "categoria":"SERIGRAFIA",
    "stock":10
  }'
```

### Listar productos:
```bash
curl -i https://backend-app-movil.vercel.app/api/products
```

---

## ✅ Checklist

- [ ] Desplegar a Vercel (`git push`)
- [ ] Ejecutar `node test-products.js`
- [ ] Verificar que GET /api/products funciona
- [ ] Probar crear producto desde app Android
- [ ] Verificar que el catálogo muestra productos

---

## 📝 Archivos Modificados

- `api/models/Product.js` - Modelo actualizado
- `api/routes/products.js` - Rutas actualizadas
- `test-products.js` - Script de test (nuevo)

---

## 📚 Documentación Completa

Ver: `CORRECCION_PRODUCTOS.md`

---

**Última actualización:** 2025-12-16  
**Estado:** ✅ Correcciones implementadas  
**Siguiente paso:** Desplegar y probar

