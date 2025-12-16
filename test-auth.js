/**
 * Script de prueba para el endpoint de autenticación
 * Testea login y register con la estructura corregida
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

async function testRegister() {
  logSection('TEST 1: REGISTRO DE USUARIO');

  const testUser = {
    name: 'Usuario Test',
    email: `test_${Date.now()}@example.com`,
    password: 'password123'
  };

  try {
    log(`Registrando usuario: ${testUser.email}`, 'blue');
    const response = await axios.post(`${API_URL}/auth/register`, testUser);

    log('✅ Registro exitoso', 'green');
    log('Respuesta:', 'yellow');
    console.log(JSON.stringify(response.data, null, 2));

    // Validar estructura de respuesta
    const { _id, nombre, email, telefono, direccion, token, rol } = response.data;

    if (!_id || !nombre || !email || !token) {
      throw new Error('Respuesta incompleta: faltan campos requeridos');
    }

    log('✅ Estructura de respuesta correcta', 'green');

    return { email: testUser.email, password: testUser.password, token };

  } catch (error) {
    log('❌ Error en registro', 'red');
    if (error.response) {
      console.log('Status:', error.response.status);
      console.log('Data:', error.response.data);
    } else {
      console.log('Error:', error.message);
    }
    return null;
  }
}

async function testLogin(email, password) {
  logSection('TEST 2: LOGIN DE USUARIO');

  try {
    log(`Intentando login con: ${email}`, 'blue');
    const response = await axios.post(`${API_URL}/auth/login`, {
      email,
      password
    });

    log('✅ Login exitoso', 'green');
    log('Respuesta:', 'yellow');
    console.log(JSON.stringify(response.data, null, 2));

    // Validar estructura de respuesta
    const { _id, nombre, email: userEmail, telefono, direccion, token, rol } = response.data;

    if (!_id || !nombre || !userEmail || !token) {
      throw new Error('Respuesta incompleta: faltan campos requeridos');
    }

    log('✅ Estructura de respuesta correcta', 'green');
    log(`✅ Token JWT generado correctamente`, 'green');

    return response.data;

  } catch (error) {
    log('❌ Error en login', 'red');
    if (error.response) {
      console.log('Status:', error.response.status);
      console.log('Data:', error.response.data);
    } else {
      console.log('Error:', error.message);
    }
    return null;
  }
}

async function testLoginWithInvalidCredentials() {
  logSection('TEST 3: LOGIN CON CREDENCIALES INCORRECTAS');

  try {
    log('Intentando login con password incorrecta', 'blue');
    await axios.post(`${API_URL}/auth/login`, {
      email: 'test@example.com',
      password: 'wrongpassword'
    });

    log('❌ No debería llegar aquí', 'red');

  } catch (error) {
    if (error.response && error.response.status === 401) {
      log('✅ Error 401 correcto para credenciales incorrectas', 'green');
      log('Mensaje de error:', 'yellow');
      console.log(JSON.stringify(error.response.data, null, 2));

      if (error.response.data.message) {
        log('✅ Mensaje de error correcto en formato { message: "..." }', 'green');
      }
    } else if (error.response && error.response.status === 404) {
      log('✅ Error 404 correcto (usuario no existe)', 'green');
      log('Mensaje de error:', 'yellow');
      console.log(JSON.stringify(error.response.data, null, 2));
    } else {
      log('❌ Error inesperado', 'red');
      console.log('Error:', error.message);
    }
  }
}

async function testRegisterDuplicate(email) {
  logSection('TEST 4: REGISTRO DUPLICADO');

  try {
    log(`Intentando registrar email duplicado: ${email}`, 'blue');
    await axios.post(`${API_URL}/auth/register`, {
      name: 'Usuario Duplicado',
      email,
      password: 'password123'
    });

    log('❌ No debería permitir registro duplicado', 'red');

  } catch (error) {
    if (error.response && error.response.status === 409) {
      log('✅ Error 409 correcto para email duplicado', 'green');
      log('Mensaje de error:', 'yellow');
      console.log(JSON.stringify(error.response.data, null, 2));
    } else {
      log('❌ Error inesperado', 'red');
      if (error.response) {
        console.log('Status:', error.response.status);
        console.log('Data:', error.response.data);
      }
    }
  }
}

async function testValidation() {
  logSection('TEST 5: VALIDACIÓN DE CAMPOS');

  // Test sin email
  try {
    log('Test 5.1: Login sin email', 'blue');
    await axios.post(`${API_URL}/auth/login`, { password: 'test' });
    log('❌ Debería rechazar request sin email', 'red');
  } catch (error) {
    if (error.response && error.response.status === 400) {
      log('✅ Validación correcta (400)', 'green');
    }
  }

  // Test sin password
  try {
    log('\nTest 5.2: Login sin password', 'blue');
    await axios.post(`${API_URL}/auth/login`, { email: 'test@test.com' });
    log('❌ Debería rechazar request sin password', 'red');
  } catch (error) {
    if (error.response && error.response.status === 400) {
      log('✅ Validación correcta (400)', 'green');
    }
  }

  // Test password corta
  try {
    log('\nTest 5.3: Register con password corta', 'blue');
    await axios.post(`${API_URL}/auth/register`, {
      name: 'Test',
      email: 'test@test.com',
      password: '123'
    });
    log('❌ Debería rechazar password corta', 'red');
  } catch (error) {
    if (error.response && error.response.status === 400) {
      log('✅ Validación correcta (400)', 'green');
    }
  }
}

async function runAllTests() {
  log('\n🚀 INICIANDO TESTS DE AUTENTICACIÓN\n', 'cyan');
  log(`Base URL: ${BASE_URL}`, 'blue');
  log(`API URL: ${API_URL}\n`, 'blue');

  try {
    // Test 1: Registro
    const userData = await testRegister();

    if (!userData) {
      log('\n❌ No se pudo continuar con los tests (registro falló)', 'red');
      return;
    }

    // Esperar un poco para que se guarde en BD
    await new Promise(resolve => setTimeout(resolve, 1000));

    // Test 2: Login
    await testLogin(userData.email, userData.password);

    // Test 3: Credenciales incorrectas
    await testLoginWithInvalidCredentials();

    // Test 4: Email duplicado
    await testRegisterDuplicate(userData.email);

    // Test 5: Validaciones
    await testValidation();

    logSection('RESUMEN DE TESTS');
    log('✅ Todos los tests completados', 'green');
    log('\nVerifica que:', 'yellow');
    log('1. El registro devuelve: _id, nombre, email, telefono, direccion, token, rol');
    log('2. El login devuelve la misma estructura');
    log('3. Los errores usan el formato { message: "..." }');
    log('4. El token JWT se genera correctamente');

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

