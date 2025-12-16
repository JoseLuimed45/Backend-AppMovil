/**
 * Script de prueba para el endpoint de productos
 * Testea CRUD completo con la estructura corregida
 */

const axios = require('axios');

// Configuración
const BASE_URL = process.env.BASE_URL || 'http://localhost:3000';
const API_URL = `${BASE_URL}/api`;

// Colores para la consola
const colors = {
  reset: '\x1b[0m',
  green: '\x1b[32m',
  red: '\x1b[31m',
  yellow: '\x1b[33m',
  blue: '\x1b[34m',
  cyan: '\x1b[36m'
};

function log(message, color = 'reset') {
  console.log(`${colors[color]}${message}${colors.reset}`);
}

function logSection(title) {
  console.log('\n' + '='.repeat(60));
  log(title, 'cyan');
  console.log('='.repeat(60) + '\n');
}

// Token de admin (necesitas obtener uno válido)
let adminToken = '';

async function getAdminToken() {
  logSection('OBTENIENDO TOKEN DE ADMIN');

  try {
    // Primero intentar login con admin
    log('Intentando login como admin...', 'blue');
    const response = await axios.post(`${API_URL}/auth/login`, {
      email: 'admin@ajicolor.com',
      password: 'admin123'
    });

    adminToken = response.data.token;
    log('✅ Token de admin obtenido', 'green');
    return true;
  } catch (error) {
    if (error.response && error.response.status === 404) {
      log('⚠️  Usuario admin no existe, intentando crear...', 'yellow');
      try {
        // Si no existe, crear usuario admin
        const registerResponse = await axios.post(`${API_URL}/auth/register`, {
          name: 'Admin',
          email: 'admin@ajicolor.com',
          password: 'admin123'
        });

        adminToken = registerResponse.data.token;

        // TODO: Actualizar el rol a ADMIN en MongoDB manualmente
        log('✅ Usuario admin creado', 'green');
        log('⚠️  IMPORTANTE: Actualiza el rol a "admin" en MongoDB:', 'yellow');
        log('   db.users.updateOne({email:"admin@ajicolor.com"},{$set:{role:"admin"}})', 'yellow');
        return true;
      } catch (regError) {
        log('❌ Error al crear admin', 'red');
        return false;
      }
    } else {
      log('❌ Error al obtener token de admin', 'red');
      if (error.response) {
        console.log('Status:', error.response.status);
        console.log('Data:', error.response.data);
      }
      return false;
    }
  }
}

async function testGetProducts() {
  logSection('TEST 1: LISTAR PRODUCTOS (GET /api/products)');

  try {
    log('Obteniendo lista de productos...', 'blue');
    const response = await axios.get(`${API_URL}/products`);

    log('✅ Productos obtenidos correctamente', 'green');
    log(`Total de productos: ${response.data.length}`, 'yellow');

    if (response.data.length > 0) {
      log('\nPrimer producto:', 'yellow');
      console.log(JSON.stringify(response.data[0], null, 2));
    }

    return response.data;
  } catch (error) {
    log('❌ Error al obtener productos', 'red');
    if (error.response) {
      console.log('Status:', error.response.status);
      console.log('Data:', error.response.data);
    }
    return [];
  }
}

async function testCreateProduct() {
  logSection('TEST 2: CREAR PRODUCTO (POST /api/products)');

  const newProduct = {
    id: `PROD-${Date.now()}`,
    nombre: 'Camiseta Test',
    descripcion: 'Camiseta de prueba creada por el script',
    precio: 15000,
    categoria: 'SERIGRAFIA',
    stock: 10,
    imagenUrl: 'https://via.placeholder.com/300'
  };

  try {
    log('Creando nuevo producto...', 'blue');
    log('Datos del producto:', 'yellow');
    console.log(JSON.stringify(newProduct, null, 2));

    const response = await axios.post(`${API_URL}/products`, newProduct, {
      headers: { Authorization: `Bearer ${adminToken}` }
    });

    log('✅ Producto creado correctamente', 'green');
    log('Respuesta del servidor:', 'yellow');
    console.log(JSON.stringify(response.data, null, 2));

    // Validar estructura de respuesta
    const { id, nombre, descripcion, precio, categoria, stock, imagenUrl } = response.data;
    if (!id || !nombre || !precio || !categoria) {
      log('⚠️  Advertencia: Respuesta incompleta', 'yellow');
    } else {
      log('✅ Estructura de respuesta correcta', 'green');
    }

    return response.data;
  } catch (error) {
    log('❌ Error al crear producto', 'red');
    if (error.response) {
      console.log('Status:', error.response.status);
      console.log('Data:', error.response.data);
    }
    return null;
  }
}

async function testGetProductById(productId) {
  logSection('TEST 3: OBTENER PRODUCTO POR ID (GET /api/products/:id)');

  try {
    log(`Obteniendo producto con ID: ${productId}`, 'blue');
    const response = await axios.get(`${API_URL}/products/${productId}`);

    log('✅ Producto obtenido correctamente', 'green');
    log('Datos del producto:', 'yellow');
    console.log(JSON.stringify(response.data, null, 2));

    return response.data;
  } catch (error) {
    log('❌ Error al obtener producto', 'red');
    if (error.response) {
      console.log('Status:', error.response.status);
      console.log('Data:', error.response.data);
    }
    return null;
  }
}

async function testUpdateProduct(productId) {
  logSection('TEST 4: ACTUALIZAR PRODUCTO (PUT /api/products/:id)');

  const updates = {
    nombre: 'Camiseta Test ACTUALIZADA',
    precio: 18000,
    stock: 15
  };

  try {
    log(`Actualizando producto: ${productId}`, 'blue');
    log('Datos a actualizar:', 'yellow');
    console.log(JSON.stringify(updates, null, 2));

    const response = await axios.put(`${API_URL}/products/${productId}`, updates, {
      headers: { Authorization: `Bearer ${adminToken}` }
    });

    log('✅ Producto actualizado correctamente', 'green');
    log('Datos actualizados:', 'yellow');
    console.log(JSON.stringify(response.data, null, 2));

    return response.data;
  } catch (error) {
    log('❌ Error al actualizar producto', 'red');
    if (error.response) {
      console.log('Status:', error.response.status);
      console.log('Data:', error.response.data);
    }
    return null;
  }
}

async function testDeleteProduct(productId) {
  logSection('TEST 5: ELIMINAR PRODUCTO (DELETE /api/products/:id)');

  try {
    log(`Eliminando producto: ${productId}`, 'blue');
    const response = await axios.delete(`${API_URL}/products/${productId}`, {
      headers: { Authorization: `Bearer ${adminToken}` }
    });

    log('✅ Producto eliminado correctamente', 'green');
    log('Respuesta:', 'yellow');
    console.log(JSON.stringify(response.data, null, 2));

    return true;
  } catch (error) {
    log('❌ Error al eliminar producto', 'red');
    if (error.response) {
      console.log('Status:', error.response.status);
      console.log('Data:', error.response.data);
    }
    return false;
  }
}

async function testValidation() {
  logSection('TEST 6: VALIDACIÓN DE CAMPOS');

  // Test sin nombre
  try {
    log('Test 6.1: Crear producto sin nombre', 'blue');
    await axios.post(`${API_URL}/products`, {
      id: 'TEST-1',
      precio: 10000,
      categoria: 'TEST'
    }, {
      headers: { Authorization: `Bearer ${adminToken}` }
    });
    log('❌ Debería rechazar producto sin nombre', 'red');
  } catch (error) {
    if (error.response && error.response.status === 400) {
      log('✅ Validación correcta (400)', 'green');
      log(`Mensaje: ${error.response.data.message}`, 'yellow');
    }
  }

  // Test sin precio
  try {
    log('\nTest 6.2: Crear producto sin precio', 'blue');
    await axios.post(`${API_URL}/products`, {
      id: 'TEST-2',
      nombre: 'Test',
      categoria: 'TEST'
    }, {
      headers: { Authorization: `Bearer ${adminToken}` }
    });
    log('❌ Debería rechazar producto sin precio', 'red');
  } catch (error) {
    if (error.response && error.response.status === 400) {
      log('✅ Validación correcta (400)', 'green');
      log(`Mensaje: ${error.response.data.message}`, 'yellow');
    }
  }

  // Test sin categoría
  try {
    log('\nTest 6.3: Crear producto sin categoría', 'blue');
    await axios.post(`${API_URL}/products`, {
      id: 'TEST-3',
      nombre: 'Test',
      precio: 10000
    }, {
      headers: { Authorization: `Bearer ${adminToken}` }
    });
    log('❌ Debería rechazar producto sin categoría', 'red');
  } catch (error) {
    if (error.response && error.response.status === 400) {
      log('✅ Validación correcta (400)', 'green');
      log(`Mensaje: ${error.response.data.message}`, 'yellow');
    }
  }
}

async function runAllTests() {
  log('\n🚀 INICIANDO TESTS DE PRODUCTOS\n', 'cyan');
  log(`Base URL: ${BASE_URL}`, 'blue');
  log(`API URL: ${API_URL}\n`, 'blue');

  try {
    // Obtener token de admin
    const hasToken = await getAdminToken();
    if (!hasToken) {
      log('\n❌ No se pudo obtener token de admin. Tests cancelados.', 'red');
      return;
    }

    // Test 1: Listar productos
    await testGetProducts();

    // Test 2: Crear producto
    const newProduct = await testCreateProduct();
    if (!newProduct) {
      log('\n❌ No se pudo crear producto. Tests restantes cancelados.', 'red');
      return;
    }

    const productId = newProduct.id;

    // Esperar un poco
    await new Promise(resolve => setTimeout(resolve, 1000));

    // Test 3: Obtener producto por ID
    await testGetProductById(productId);

    // Test 4: Actualizar producto
    await testUpdateProduct(productId);

    // Test 5: Eliminar producto
    await testDeleteProduct(productId);

    // Test 6: Validaciones
    await testValidation();

    logSection('RESUMEN DE TESTS');
    log('✅ Todos los tests completados', 'green');
    log('\nVerifica que:', 'yellow');
    log('1. GET /api/products devuelve array de productos');
    log('2. POST /api/products crea con campos en español');
    log('3. GET /api/products/:id busca por campo id (no _id)');
    log('4. PUT /api/products/:id actualiza correctamente');
    log('5. DELETE /api/products/:id elimina correctamente');
    log('6. Las validaciones funcionan');

  } catch (error) {
    log('\n❌ Error en ejecución de tests', 'red');
    console.error(error);
  }
}

// Ejecutar tests
runAllTests().then(() => {
  log('\n✅ Tests finalizados\n', 'green');
  process.exit(0);
}).catch(error => {
  log('\n❌ Error fatal en tests\n', 'red');
  console.error(error);
  process.exit(1);
});

