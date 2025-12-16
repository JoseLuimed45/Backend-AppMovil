# 🗄️ COMANDOS RÁPIDOS - Verificación MongoDB Atlas

## 📊 VERIFICAR CONFIGURACIÓN DE BASE DE DATOS

### Opción 1: Script Node.js (Backend)

```bash
cd C:\Users\josel\AndroidStudioProjects\AppMovil\ajicolor_backend

# Test: Conectar a BDAjicolor (Development/Test)
NODE_ENV=development MONGO_URI="mongodb+srv://db_user:PASSWORD@dbappmovil.ok21huu.mongodb.net/BDAjicolor?retryWrites=true&w=majority" node scripts/verify-db-config.js

# Production: Conectar a DBAppMovil
NODE_ENV=production MONGO_URI="mongodb+srv://db_user:PASSWORD@dbappmovil.ok21huu.mongodb.net/DBAppMovil?retryWrites=true&w=majority" node scripts/verify-db-config.js
```

### Opción 2: MongoDB Compass

```
1. Abrir MongoDB Compass
2. New Connection
3. URI: mongodb+srv://db_user:PASSWORD@dbappmovil.ok21huu.mongodb.net/
4. Connect
5. Ver bases de datos en el panel izquierdo
```

### Opción 3: MongoDB Atlas Web UI

```
URL: https://cloud.mongodb.com/v2/69344c8ae667e36cd4c3a1a0#/clusters/explorer

1. Browse Collections
2. Ver bases de datos:
   - BDAjicolor (Test/Dev)
   - DBAppMovil (Production)
```

---

## 🔍 CONSULTAS MONGODB

### Verificar Usuario Admin

```javascript
// En MongoDB Atlas UI o Compass

// BDAjicolor (Test)
use BDAjicolor
db.users.findOne({ email: "admin@ajicolor.com" })

// DBAppMovil (Production)
use DBAppMovil
db.users.findOne({ email: "admin@ajicolor.com" })
```

**Resultado esperado:**
```javascript
{
  _id: ObjectId("675e8b7d..."),
  nombre: "Admin",
  email: "admin@ajicolor.com",
  password: "$2a$10$...", // Hash bcrypt
  rol: "ADMIN",
  telefono: "",
  direccion: "",
  createdAt: ISODate("..."),
  updatedAt: ISODate("...")
}
```

### Verificar Productos

```javascript
use BDAjicolor  // o DBAppMovil

// Contar productos
db.products.countDocuments()

// Ver primeros 3 productos
db.products.find().limit(3).pretty()

// Buscar producto específico
db.products.findOne({ nombre: /polera/i })
```

### Verificar Pedidos Recientes

```javascript
use BDAjicolor  // o DBAppMovil

// Últimos 5 pedidos
db.orders.find()
  .sort({ createdAt: -1 })
  .limit(5)
  .pretty()

// Pedidos por estado
db.orders.aggregate([
  { $group: {
      _id: "$estado",
      count: { $sum: 1 }
  }}
])

// Pedidos de un usuario específico
db.orders.find({ usuario: ObjectId("USER_ID_AQUI") })
```

### Verificar Colecciones Existentes

```javascript
use BDAjicolor  // o DBAppMovil

// Listar todas las colecciones
db.getCollectionNames()

// Ver estadísticas de una colección
db.users.stats()
db.products.stats()
db.orders.stats()
```

---

## 🔧 COMANDOS ÚTILES

### Limpiar Base de Datos de Test

```javascript
use BDAjicolor

// ⚠️ CUIDADO: Esto borra todos los pedidos de test
db.orders.deleteMany({ numeroPedido: /TEST-/ })

// Ver pedidos eliminados
db.orders.find({ numeroPedido: /TEST-/ }).count()  // Debe ser 0
```

### Crear Usuario Admin (si no existe)

```javascript
use DBAppMovil  // O BDAjicolor

// Insertar admin (password: admin123 hasheado con bcrypt)
db.users.insertOne({
  nombre: "Admin",
  email: "admin@ajicolor.com",
  password: "$2a$10$8K1p/a0dL.1H3P/G9C0zZ.YK7wPxMQPr0t6cVU6BzYMYJmT8CzjM2",
  rol: "ADMIN",
  telefono: "",
  direccion: "",
  createdAt: new Date(),
  updatedAt: new Date()
})

// Verificar
db.users.findOne({ email: "admin@ajicolor.com" })
```

### Copiar Datos entre Bases

```javascript
// Copiar usuarios de BDAjicolor a DBAppMovil
use BDAjicolor
var usuarios = db.users.find().toArray()

use DBAppMovil
db.users.insertMany(usuarios)

// Verificar
db.users.countDocuments()
```

---

## 📊 ESTADÍSTICAS RÁPIDAS

```javascript
use BDAjicolor  // o DBAppMovil

// Resumen completo
print("=== ESTADÍSTICAS DE BASE DE DATOS ===\n")

print("Usuarios: " + db.users.countDocuments())
print("Productos: " + db.products.countDocuments())
print("Pedidos: " + db.orders.countDocuments())
print("Posts: " + db.posts.countDocuments())
print("Audit Logs: " + db.auditlogs.countDocuments())

// Usuarios por rol
print("\n=== USUARIOS POR ROL ===")
db.users.aggregate([
  { $group: { _id: "$rol", count: { $sum: 1 } } }
]).forEach(doc => print(doc._id + ": " + doc.count))

// Pedidos por estado
print("\n=== PEDIDOS POR ESTADO ===")
db.orders.aggregate([
  { $group: { _id: "$estado", count: { $sum: 1 } } }
]).forEach(doc => print(doc._id + ": " + doc.count))

// Productos por categoría
print("\n=== PRODUCTOS POR CATEGORÍA ===")
db.products.aggregate([
  { $group: { _id: "$categoria", count: { $sum: 1 } } }
]).forEach(doc => print(doc._id + ": " + doc.count))
```

---

## 🔒 SEGURIDAD Y PERMISOS

### Verificar Usuarios de BD

```
MongoDB Atlas → Database Access

Verificar:
- db_user existe
- Password actualizada (no expuesta)
- Role: readWriteAnyDatabase o atlas_admin
- IP Whitelist: 0.0.0.0/0 (para Vercel) o IPs específicas
```

### Cambiar Password

```
1. MongoDB Atlas → Database Access
2. db_user → Edit
3. Edit Password → Autogenerate Secure Password
4. Copiar nueva password
5. Update User
6. Actualizar MONGO_URI en Vercel y .env local
7. Redeploy Vercel
```

---

## 🧪 TESTS DE CONECTIVIDAD

### Test Rápido con mongosh (CLI)

```bash
# Instalar mongosh si no está instalado
# https://www.mongodb.com/try/download/shell

# Conectar a BDAjicolor
mongosh "mongodb+srv://db_user:PASSWORD@dbappmovil.ok21huu.mongodb.net/BDAjicolor"

# Dentro de mongosh
show dbs
use BDAjicolor
show collections
db.users.findOne({ email: "admin@ajicolor.com" })
exit
```

### Test con Node.js (One-liner)

```bash
node -e "require('mongoose').connect('mongodb+srv://db_user:PASSWORD@dbappmovil.ok21huu.mongodb.net/BDAjicolor').then(() => console.log('✓ Connected')).catch(err => console.error('✗ Error:', err.message))"
```

---

## 📝 PLANTILLAS DE CONSULTAS

### Buscar Usuario por Email

```javascript
db.users.findOne({ email: "usuario@ejemplo.com" })
```

### Buscar Pedidos de Hoy

```javascript
var hoy = new Date()
hoy.setHours(0,0,0,0)

db.orders.find({
  createdAt: { $gte: hoy }
}).sort({ createdAt: -1 })
```

### Productos con Stock Bajo

```javascript
db.products.find({
  stock: { $lt: 10 }
}).sort({ stock: 1 })
```

### Total de Ventas del Mes

```javascript
var inicioMes = new Date()
inicioMes.setDate(1)
inicioMes.setHours(0,0,0,0)

db.orders.aggregate([
  {
    $match: {
      createdAt: { $gte: inicioMes },
      estado: { $in: ["CONFIRMADO", "ENVIADO", "ENTREGADO"] }
    }
  },
  {
    $group: {
      _id: null,
      totalVentas: { $sum: "$total" },
      cantidadPedidos: { $sum: 1 }
    }
  }
])
```

---

## 🎯 RESUMEN DE COMANDOS ESENCIALES

```bash
# Backend: Verificar conexión
cd ajicolor_backend
node scripts/verify-db-config.js

# MongoDB: Ver bases de datos
mongosh "mongodb+srv://..." --eval "show dbs"

# MongoDB: Contar documentos
mongosh "mongodb+srv://.../BDAjicolor" --eval "db.users.countDocuments()"

# Tests: Ejecutar suite completa
cd app_ajicolor_backend_node
.\test-vercel-complete.ps1
```

---

**Tip:** Guarda estos comandos en un archivo de referencia rápida para uso frecuente.

