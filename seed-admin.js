const mongoose = require('mongoose');
const bcrypt = require('bcryptjs');

// Conectar a MongoDB (usa tu URI de Atlas)
// Acepta ambas variables de entorno por conveniencia: MONGO_URI y MONGODB_URI
const MONGODB_URI = process.env.MONGO_URI || process.env.MONGODB_URI || 'tu_uri_de_mongodb_atlas';

const userSchema = new mongoose.Schema({
  name: { type: String, required: true, trim: true },
  email: { type: String, required: true, unique: true, lowercase: true, trim: true },
  password: { type: String, required: true },
  rol: { type: String, default: 'user' },
}, { timestamps: true });

const User = mongoose.models.User || mongoose.model('User', userSchema);

async function seedAdmin() {
  try {
    console.log('🔌 Conectando a MongoDB...');
    if (!MONGODB_URI || MONGODB_URI === 'tu_uri_de_mongodb_atlas') {
      throw new Error('MONGODB_URI/MONGO_URI no definido. Establece la URI de Atlas antes de ejecutar.');
    }
    await mongoose.connect(MONGODB_URI);
    console.log('✅ Conexión exitosa');

    // Verificar si ya existe el admin
    const existingAdmin = await User.findOne({ email: 'admin@ajicolor.com' });
    
    if (existingAdmin) {
      console.log('⚠️  El usuario admin ya existe');
      console.log('📧 Email:', existingAdmin.email);
      console.log('👤 Nombre:', existingAdmin.name);
      console.log('🔑 Rol:', existingAdmin.rol);
      
      // Actualizar la contraseña si es necesario
      const hashedPassword = await bcrypt.hash('admin123', 10);
      await User.updateOne(
        { email: 'admin@ajicolor.com' },
        { 
          password: hashedPassword,
          rol: 'ADMIN',
          name: 'Administrador'
        }
      );
      console.log('✅ Contraseña y rol actualizados (admin123, ADMIN)');
    } else {
      console.log('🆕 Creando usuario admin...');
      
      // Hash de la contraseña
      const hashedPassword = await bcrypt.hash('admin123', 10);
      
      // Crear el usuario admin
      const admin = await User.create({
        name: 'Administrador',
        email: 'admin@ajicolor.com',
        password: hashedPassword,
        rol: 'ADMIN'
      });
      
      console.log('✅ Usuario admin creado exitosamente');
      console.log('📧 Email: admin@ajicolor.com');
      console.log('🔑 Password: admin123');
      console.log('👤 Rol: ADMIN');
      console.log('🆔 ID:', admin._id);
    }
    
    console.log('\n✅ Proceso completado');
    process.exit(0);
  } catch (error) {
    console.error('❌ Error:', error.message);
    process.exit(1);
  }
}

// Ejecutar solo si se llama directamente
if (require.main === module) {
  seedAdmin();
}

module.exports = { seedAdmin };
