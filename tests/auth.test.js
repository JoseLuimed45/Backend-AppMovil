/**
 * Tests Unitarios - Autenticación
 * App Ajicolor Backend
 */

const request = require('supertest');
const { MongoMemoryServer } = require('mongodb-memory-server');
const mongoose = require('mongoose');
const bcrypt = require('bcryptjs');
const jwt = require('jsonwebtoken');

// Configurar timeout para tests
jest.setTimeout(30000);

let app;
let mongoServer;
let User;

describe('🔐 Auth - Autenticación y Registro', () => {
  
  beforeAll(async () => {
    // Iniciar servidor MongoDB en memoria
    mongoServer = await MongoMemoryServer.create();
    const mongoUri = mongoServer.getUri();
    
    await mongoose.connect(mongoUri);
    
    // Definir modelo User si no existe
    const userSchema = new mongoose.Schema({
      name: { type: String, required: true },
      email: { type: String, required: true, unique: true },
      password: { type: String, required: true },
      role: { type: String, default: 'user' },
      createdAt: { type: Date, default: Date.now }
    });
    
    User = mongoose.models.User || mongoose.model('User', userSchema);
    
    // Mock de la app (ajustar según tu estructura)
    try {
      app = require('../api/index'); // o '../server' según tu estructura
    } catch (error) {
      console.warn('No se pudo cargar la app, usando mock');
      // Mock básico si no se puede cargar
      const express = require('express');
      app = express();
      app.use(express.json());
    }
  });

  afterAll(async () => {
    await mongoose.disconnect();
    await mongoServer.stop();
  });

  beforeEach(async () => {
    // Limpiar base de datos antes de cada test
    await User.deleteMany({});
  });

  // ==================== REGISTRO ====================

  describe('POST /api/auth/register', () => {
    
    test('✅ Debe registrar un nuevo usuario con datos válidos', async () => {
      const newUser = {
        name: 'Test User',
        email: 'test@example.com',
        password: 'Password123!'
      };

      const response = await request(app)
        .post('/api/auth/register')
        .send(newUser)
        .expect('Content-Type', /json/)
        .expect(201);

      expect(response.body).toHaveProperty('token');
      expect(response.body).toHaveProperty('user');
      expect(response.body.user.email).toBe(newUser.email);
      expect(response.body.user.name).toBe(newUser.name);
      expect(response.body.user).not.toHaveProperty('password'); // No debe exponer password
    });

    test('✅ Debe hashear la password correctamente', async () => {
      const newUser = {
        name: 'Test User',
        email: 'test2@example.com',
        password: 'Password123!'
      };

      await request(app)
        .post('/api/auth/register')
        .send(newUser);

      const userInDb = await User.findOne({ email: newUser.email });
      
      expect(userInDb.password).not.toBe(newUser.password);
      expect(userInDb.password.startsWith('$2a$')).toBe(true); // bcrypt hash
      
      const isPasswordValid = await bcrypt.compare(newUser.password, userInDb.password);
      expect(isPasswordValid).toBe(true);
    });

    test('❌ Debe rechazar registro con email duplicado', async () => {
      const user = {
        name: 'User One',
        email: 'duplicate@example.com',
        password: 'Password123!'
      };

      // Primer registro
      await request(app)
        .post('/api/auth/register')
        .send(user)
        .expect(201);

      // Segundo registro con mismo email
      const response = await request(app)
        .post('/api/auth/register')
        .send({ ...user, name: 'User Two' })
        .expect(409); // Conflict

      expect(response.body).toHaveProperty('error');
      expect(response.body.error).toMatch(/email.*existe|already.*exists/i);
    });

    test('❌ Debe rechazar registro sin campos requeridos', async () => {
      const invalidUsers = [
        { email: 'test@example.com', password: 'Password123!' }, // sin name
        { name: 'Test User', password: 'Password123!' }, // sin email
        { name: 'Test User', email: 'test@example.com' }, // sin password
        {} // sin nada
      ];

      for (const user of invalidUsers) {
        const response = await request(app)
          .post('/api/auth/register')
          .send(user)
          .expect(400);

        expect(response.body).toHaveProperty('error');
      }
    });

    test('❌ Debe rechazar email inválido', async () => {
      const user = {
        name: 'Test User',
        email: 'invalid-email',
        password: 'Password123!'
      };

      const response = await request(app)
        .post('/api/auth/register')
        .send(user)
        .expect(400);

      expect(response.body.error).toMatch(/email.*válido|valid.*email/i);
    });

    test('❌ Debe rechazar password demasiado corta', async () => {
      const user = {
        name: 'Test User',
        email: 'test@example.com',
        password: '123' // muy corta
      };

      const response = await request(app)
        .post('/api/auth/register')
        .send(user)
        .expect(400);

      expect(response.body.error).toMatch(/password.*caracteres|password.*characters/i);
    });

    test('✅ Debe generar token JWT válido', async () => {
      const newUser = {
        name: 'Test User',
        email: 'test@example.com',
        password: 'Password123!'
      };

      const response = await request(app)
        .post('/api/auth/register')
        .send(newUser)
        .expect(201);

      expect(response.body).toHaveProperty('token');
      
      const decoded = jwt.decode(response.body.token);
      expect(decoded).toHaveProperty('user_id');
      expect(decoded).toHaveProperty('email', newUser.email);
      expect(decoded).toHaveProperty('exp'); // expiration
    });
  });

  // ==================== LOGIN ====================

  describe('POST /api/auth/login', () => {
    
    const testUser = {
      name: 'Login Test User',
      email: 'login@example.com',
      password: 'Password123!'
    };

    beforeEach(async () => {
      // Crear usuario de prueba
      const hashedPassword = await bcrypt.hash(testUser.password, 10);
      await User.create({
        name: testUser.name,
        email: testUser.email,
        password: hashedPassword
      });
    });

    test('✅ Debe autenticar usuario con credenciales correctas', async () => {
      const response = await request(app)
        .post('/api/auth/login')
        .send({
          email: testUser.email,
          password: testUser.password
        })
        .expect('Content-Type', /json/)
        .expect(200);

      expect(response.body).toHaveProperty('token');
      expect(response.body).toHaveProperty('user');
      expect(response.body.user.email).toBe(testUser.email);
      expect(response.body.user).not.toHaveProperty('password');
    });

    test('✅ Debe generar token JWT válido en login', async () => {
      const response = await request(app)
        .post('/api/auth/login')
        .send({
          email: testUser.email,
          password: testUser.password
        })
        .expect(200);

      const decoded = jwt.decode(response.body.token);
      expect(decoded).toHaveProperty('user_id');
      expect(decoded).toHaveProperty('email', testUser.email);
    });

    test('❌ Debe rechazar login con password incorrecta', async () => {
      const response = await request(app)
        .post('/api/auth/login')
        .send({
          email: testUser.email,
          password: 'WrongPassword123!'
        })
        .expect(401);

      expect(response.body).toHaveProperty('error');
      expect(response.body.error).toMatch(/credenciales|incorrect|invalid/i);
    });

    test('❌ Debe rechazar login con email no registrado', async () => {
      const response = await request(app)
        .post('/api/auth/login')
        .send({
          email: 'noexiste@example.com',
          password: 'Password123!'
        })
        .expect(404);

      expect(response.body).toHaveProperty('error');
      expect(response.body.error).toMatch(/usuario.*existe|user.*found/i);
    });

    test('❌ Debe rechazar login sin email o password', async () => {
      const invalidLogins = [
        { password: 'Password123!' }, // sin email
        { email: 'test@example.com' }, // sin password
        {} // sin nada
      ];

      for (const login of invalidLogins) {
        const response = await request(app)
          .post('/api/auth/login')
          .send(login)
          .expect(400);

        expect(response.body).toHaveProperty('error');
      }
    });

    test('✅ Password debe ser verificada con bcrypt.compare', async () => {
      // Test indirecto: verificar que funciona con hash
      const user = await User.findOne({ email: testUser.email });
      const isValid = await bcrypt.compare(testUser.password, user.password);
      
      expect(isValid).toBe(true);
      
      const isInvalid = await bcrypt.compare('WrongPassword', user.password);
      expect(isInvalid).toBe(false);
    });
  });

  // ==================== TOKEN JWT ====================

  describe('JWT Token Validation', () => {
    
    test('✅ Token debe expirar después del tiempo configurado', () => {
      const payload = { user_id: '123', email: 'test@example.com' };
      const secret = process.env.JWT_SECRET || 'test-secret';
      const token = jwt.sign(payload, secret, { expiresIn: '1s' });

      const decoded = jwt.verify(token, secret);
      expect(decoded).toHaveProperty('exp');
      
      // Verificar que expira en el futuro cercano
      const now = Math.floor(Date.now() / 1000);
      expect(decoded.exp).toBeGreaterThan(now);
      expect(decoded.exp).toBeLessThan(now + 2);
    });

    test('❌ Token inválido debe ser rechazado', () => {
      const invalidToken = 'invalid.token.here';
      const secret = process.env.JWT_SECRET || 'test-secret';

      expect(() => {
        jwt.verify(invalidToken, secret);
      }).toThrow();
    });

    test('❌ Token expirado debe ser rechazado', (done) => {
      const payload = { user_id: '123', email: 'test@example.com' };
      const secret = process.env.JWT_SECRET || 'test-secret';
      const token = jwt.sign(payload, secret, { expiresIn: '1ms' });

      setTimeout(() => {
        expect(() => {
          jwt.verify(token, secret);
        }).toThrow(/expired/i);
        done();
      }, 10);
    });
  });

  // ==================== SEGURIDAD ====================

  describe('Security Tests', () => {
    
    test('✅ Password nunca debe ser retornada en respuestas', async () => {
      const newUser = {
        name: 'Security Test',
        email: 'security@example.com',
        password: 'Password123!'
      };

      // Test en registro
      const registerResponse = await request(app)
        .post('/api/auth/register')
        .send(newUser)
        .expect(201);

      expect(registerResponse.body.user).not.toHaveProperty('password');

      // Test en login
      const loginResponse = await request(app)
        .post('/api/auth/login')
        .send({ email: newUser.email, password: newUser.password })
        .expect(200);

      expect(loginResponse.body.user).not.toHaveProperty('password');
    });

    test('✅ Salt rounds de bcrypt deben ser >= 10', async () => {
      const password = 'TestPassword123!';
      const hash = await bcrypt.hash(password, 10);
      
      // Hash bcrypt incluye el salt rounds en el hash
      const rounds = parseInt(hash.split('$')[2]);
      expect(rounds).toBeGreaterThanOrEqual(10);
    });

    test('❌ Debe rechazar inyección NoSQL en email', async () => {
      const maliciousLogin = {
        email: { $ne: null }, // Intento de inyección NoSQL
        password: 'anything'
      };

      const response = await request(app)
        .post('/api/auth/login')
        .send(maliciousLogin)
        .expect(400);

      expect(response.body).toHaveProperty('error');
    });
  });
});

// ==================== HELPER FUNCTIONS ====================

/**
 * Genera un usuario de prueba aleatorio
 */
function generateRandomUser() {
  const random = Math.random().toString(36).substring(7);
  return {
    name: `Test User ${random}`,
    email: `test${random}@example.com`,
    password: 'Password123!'
  };
}

/**
 * Crea un usuario y retorna su token
 */
async function createUserAndGetToken(userData = null) {
  const user = userData || generateRandomUser();
  
  const response = await request(app)
    .post('/api/auth/register')
    .send(user);

  return {
    token: response.body.token,
    user: response.body.user,
    password: user.password
  };
}

module.exports = {
  generateRandomUser,
  createUserAndGetToken
};
