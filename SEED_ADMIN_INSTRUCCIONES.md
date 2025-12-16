# Instrucciones: Crear Usuario Admin

## Opción 1: Ejecutar Script Seed (Recomendado)

### Requisitos
- Node.js instalado
- Acceso a la URI de MongoDB Atlas

### Pasos

1. **Configurar la URI de MongoDB**

Opción A - Variable de entorno temporal:
```bash
set MONGODB_URI=tu_uri_completa_de_mongodb_atlas
```

Opción B - Editar el archivo `seed-admin.js` directamente (línea 4):
```javascript
const MONGODB_URI = 'mongodb+srv://usuario:password@cluster.mongodb.net/nombre_db';
```

2. **Ejecutar el script**
```bash
node seed-admin.js
```

El script:
- ✅ Verifica si el admin existe
- ✅ Si existe: actualiza la contraseña a `admin123` y el rol a `ADMIN`
- ✅ Si no existe: crea el usuario admin completo

---

## Opción 2: Comando MongoDB Directo

### Usando MongoDB Compass o mongosh

```javascript
use nombre_de_tu_base_de_datos

// Generar hash de contraseña (bcrypt con salt 10)
// Hash para "admin123": $2a$10$...

db.users.insertOne({
  name: "Administrador",
  email: "admin@ajicolor.com",
  password: "$2a$10$YourHashedPasswordHere",
  role: "ADMIN",
  createdAt: new Date(),
  updatedAt: new Date()
})
```

### Hash de contraseña precalculado

Para `admin123` con bcrypt rounds=10:
```
$2a$10$N9qo8uLOickgx2ZMRZoMye1BjVUF6y5/Px8.uQCZy5KN.ZkPqYvEa
```

---

## Opción 3: Crear Endpoint Seed Temporal

Si tienes acceso al código del backend en Vercel:

1. **Agregar ruta temporal en `api/index.js`**:

```javascript
// Seed temporal - ELIMINAR EN PRODUCCIÓN
app.post('/api/seed-admin-once', async (req, res) => {
  try {
    const bcrypt = require('bcryptjs');
    const User = require('./models/User');
    
    const exists = await User.findOne({ email: 'admin@ajicolor.com' });
    if (exists) {
      return res.json({ message: 'Admin ya existe', email: exists.email });
    }
    
    const hashedPassword = await bcrypt.hash('admin123', 10);
    const admin = await User.create({
      name: 'Administrador',
      email: 'admin@ajicolor.com',
      password: hashedPassword,
      role: 'ADMIN'
    });
    
    return res.json({ 
      message: 'Admin creado', 
      email: admin.email,
      role: admin.role 
    });
  } catch (error) {
    return res.status(500).json({ error: error.message });
  }
});
```

2. **Deploy a Vercel**

3. **Ejecutar una vez**:
```bash
curl -X POST https://backend-app-movil.vercel.app/api/seed-admin-once
```

4. **ELIMINAR el endpoint** después de usar

---

## Credenciales Finales

```
Email:    admin@ajicolor.com
Password: admin123
Rol:      ADMIN
```

---

## Verificación

Probar login con curl:
```bash
curl -X POST https://backend-app-movil.vercel.app/api/v1/usuarios/login \
  -H "Content-Type: application/json" \
  -d "{\"email\":\"admin@ajicolor.com\",\"password\":\"admin123\"}"
```

Respuesta esperada:
```json
{
  "token": "eyJhbGc...",
  "user": {
    "email": "admin@ajicolor.com",
    "name": "Administrador",
    "role": "ADMIN"
  }
}
```

---

## Notas de Seguridad

⚠️ **Importante:**
- Cambia `admin123` por una contraseña fuerte en producción
- Elimina cualquier endpoint de seed después de usar
- No expongas las credenciales admin en repositorios públicos
- Usa variables de entorno para credenciales sensibles
