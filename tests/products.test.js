/**
 * Tests Unitarios - Productos
 * App Ajicolor Backend
 */

const request = require('supertest');
const { MongoMemoryServer } = require('mongodb-memory-server');
const mongoose = require('mongoose');
const { createUserAndGetToken } = require('./auth.test');

jest.setTimeout(30000);

let app;
let mongoServer;
let Product;
let authToken;

describe('🛍️ Products - CRUD de Productos', () => {
  
  beforeAll(async () => {
    mongoServer = await MongoMemoryServer.create();
    const mongoUri = mongoServer.getUri();
    await mongoose.connect(mongoUri);
    
    // Definir modelo Product
    const productSchema = new mongoose.Schema({
      name: { type: String, required: true },
      description: String,
      price: { type: Number, required: true },
      category: String,
      stock: { type: Number, default: 0 },
      image: String,
      createdAt: { type: Date, default: Date.now }
    });
    
    Product = mongoose.models.Product || mongoose.model('Product', productSchema);
    
    try {
      app = require('../api/index');
    } catch (error) {
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
    await Product.deleteMany({});
    
    // Obtener token de autenticación
    const { token } = await createUserAndGetToken();
    authToken = token;
  });

  // ==================== LISTAR PRODUCTOS ====================

  describe('GET /api/products', () => {
    
    test('✅ Debe retornar lista de productos', async () => {
      // Crear productos de prueba
      await Product.create([
        { name: 'Producto 1', price: 100, stock: 10 },
        { name: 'Producto 2', price: 200, stock: 5 },
        { name: 'Producto 3', price: 300, stock: 15 }
      ]);

      const response = await request(app)
        .get('/api/products')
        .expect('Content-Type', /json/)
        .expect(200);

      expect(Array.isArray(response.body)).toBe(true);
      expect(response.body).toHaveLength(3);
      expect(response.body[0]).toHaveProperty('name');
      expect(response.body[0]).toHaveProperty('price');
      expect(response.body[0]).toHaveProperty('_id');
    });

    test('✅ Debe retornar array vacío si no hay productos', async () => {
      const response = await request(app)
        .get('/api/products')
        .expect(200);

      expect(Array.isArray(response.body)).toBe(true);
      expect(response.body).toHaveLength(0);
    });

    test('✅ Productos deben tener estructura correcta', async () => {
      await Product.create({
        name: 'Test Product',
        description: 'Test Description',
        price: 99.99,
        category: 'Test Category',
        stock: 20,
        image: 'https://example.com/image.jpg'
      });

      const response = await request(app)
        .get('/api/products')
        .expect(200);

      const product = response.body[0];
      expect(product).toHaveProperty('_id');
      expect(product).toHaveProperty('name', 'Test Product');
      expect(product).toHaveProperty('price', 99.99);
      expect(product).toHaveProperty('stock', 20);
      expect(product).toHaveProperty('category', 'Test Category');
    });
  });

  // ==================== OBTENER PRODUCTO POR ID ====================

  describe('GET /api/products/:id', () => {
    
    test('✅ Debe retornar producto específico por ID', async () => {
      const product = await Product.create({
        name: 'Producto Específico',
        price: 150,
        stock: 10
      });

      const response = await request(app)
        .get(`/api/products/${product._id}`)
        .expect(200);

      expect(response.body).toHaveProperty('_id', product._id.toString());
      expect(response.body).toHaveProperty('name', 'Producto Específico');
      expect(response.body).toHaveProperty('price', 150);
    });

    test('❌ Debe retornar 404 si producto no existe', async () => {
      const fakeId = new mongoose.Types.ObjectId();
      
      const response = await request(app)
        .get(`/api/products/${fakeId}`)
        .expect(404);

      expect(response.body).toHaveProperty('error');
      expect(response.body.error).toMatch(/no.*encontrado|not.*found/i);
    });

    test('❌ Debe rechazar ID inválido', async () => {
      const response = await request(app)
        .get('/api/products/invalid-id')
        .expect(400);

      expect(response.body).toHaveProperty('error');
      expect(response.body.error).toMatch(/id.*inválido|invalid.*id/i);
    });
  });

  // ==================== CREAR PRODUCTO ====================

  describe('POST /api/products', () => {
    
    test('✅ Debe crear producto con datos válidos', async () => {
      const newProduct = {
        name: 'Nuevo Producto',
        description: 'Descripción del producto',
        price: 299.99,
        category: 'Electrónica',
        stock: 50,
        image: 'https://example.com/producto.jpg'
      };

      const response = await request(app)
        .post('/api/products')
        .set('Authorization', `Bearer ${authToken}`)
        .send(newProduct)
        .expect(201);

      expect(response.body).toHaveProperty('_id');
      expect(response.body).toHaveProperty('name', newProduct.name);
      expect(response.body).toHaveProperty('price', newProduct.price);
      expect(response.body).toHaveProperty('stock', newProduct.stock);

      // Verificar en BD
      const productInDb = await Product.findById(response.body._id);
      expect(productInDb).toBeTruthy();
      expect(productInDb.name).toBe(newProduct.name);
    });

    test('❌ Debe rechazar creación sin autenticación', async () => {
      const newProduct = {
        name: 'Producto Sin Auth',
        price: 100,
        stock: 10
      };

      const response = await request(app)
        .post('/api/products')
        .send(newProduct)
        .expect(401);

      expect(response.body).toHaveProperty('error');
      expect(response.body.error).toMatch(/autenticación|authentication|token/i);
    });

    test('❌ Debe rechazar creación sin campos requeridos', async () => {
      const invalidProducts = [
        { price: 100, stock: 10 }, // sin name
        { name: 'Producto', stock: 10 }, // sin price
        {} // sin nada
      ];

      for (const product of invalidProducts) {
        const response = await request(app)
          .post('/api/products')
          .set('Authorization', `Bearer ${authToken}`)
          .send(product)
          .expect(400);

        expect(response.body).toHaveProperty('error');
      }
    });

    test('❌ Debe rechazar precio negativo o cero', async () => {
      const invalidPrices = [-10, 0, -0.01];

      for (const price of invalidPrices) {
        const response = await request(app)
          .post('/api/products')
          .set('Authorization', `Bearer ${authToken}`)
          .send({ name: 'Test', price, stock: 10 })
          .expect(400);

        expect(response.body.error).toMatch(/precio.*válido|valid.*price/i);
      }
    });

    test('❌ Debe rechazar stock negativo', async () => {
      const response = await request(app)
        .post('/api/products')
        .set('Authorization', `Bearer ${authToken}`)
        .send({ name: 'Test', price: 100, stock: -5 })
        .expect(400);

      expect(response.body.error).toMatch(/stock.*válido|valid.*stock/i);
    });
  });

  // ==================== ACTUALIZAR PRODUCTO ====================

  describe('PUT /api/products/:id', () => {
    
    test('✅ Debe actualizar producto existente', async () => {
      const product = await Product.create({
        name: 'Producto Original',
        price: 100,
        stock: 10
      });

      const updates = {
        name: 'Producto Actualizado',
        price: 150,
        stock: 20
      };

      const response = await request(app)
        .put(`/api/products/${product._id}`)
        .set('Authorization', `Bearer ${authToken}`)
        .send(updates)
        .expect(200);

      expect(response.body).toHaveProperty('name', updates.name);
      expect(response.body).toHaveProperty('price', updates.price);
      expect(response.body).toHaveProperty('stock', updates.stock);

      // Verificar en BD
      const updatedProduct = await Product.findById(product._id);
      expect(updatedProduct.name).toBe(updates.name);
      expect(updatedProduct.price).toBe(updates.price);
    });

    test('❌ Debe rechazar actualización sin autenticación', async () => {
      const product = await Product.create({
        name: 'Producto',
        price: 100,
        stock: 10
      });

      const response = await request(app)
        .put(`/api/products/${product._id}`)
        .send({ name: 'Nuevo Nombre' })
        .expect(401);

      expect(response.body).toHaveProperty('error');
    });

    test('❌ Debe retornar 404 si producto no existe', async () => {
      const fakeId = new mongoose.Types.ObjectId();

      const response = await request(app)
        .put(`/api/products/${fakeId}`)
        .set('Authorization', `Bearer ${authToken}`)
        .send({ name: 'Nuevo Nombre' })
        .expect(404);

      expect(response.body.error).toMatch(/no.*encontrado|not.*found/i);
    });

    test('✅ Debe actualizar solo campos proporcionados', async () => {
      const product = await Product.create({
        name: 'Producto',
        price: 100,
        stock: 10,
        category: 'Original'
      });

      await request(app)
        .put(`/api/products/${product._id}`)
        .set('Authorization', `Bearer ${authToken}`)
        .send({ price: 200 }) // Solo actualizar precio
        .expect(200);

      const updated = await Product.findById(product._id);
      expect(updated.price).toBe(200);
      expect(updated.name).toBe('Producto'); // No cambió
      expect(updated.category).toBe('Original'); // No cambió
    });
  });

  // ==================== ELIMINAR PRODUCTO ====================

  describe('DELETE /api/products/:id', () => {
    
    test('✅ Debe eliminar producto existente', async () => {
      const product = await Product.create({
        name: 'Producto a Eliminar',
        price: 100,
        stock: 10
      });

      const response = await request(app)
        .delete(`/api/products/${product._id}`)
        .set('Authorization', `Bearer ${authToken}`)
        .expect(200);

      expect(response.body).toHaveProperty('message');
      expect(response.body.message).toMatch(/eliminado|deleted/i);

      // Verificar que fue eliminado
      const deletedProduct = await Product.findById(product._id);
      expect(deletedProduct).toBeNull();
    });

    test('❌ Debe rechazar eliminación sin autenticación', async () => {
      const product = await Product.create({
        name: 'Producto',
        price: 100,
        stock: 10
      });

      const response = await request(app)
        .delete(`/api/products/${product._id}`)
        .expect(401);

      expect(response.body).toHaveProperty('error');

      // Verificar que NO fue eliminado
      const notDeleted = await Product.findById(product._id);
      expect(notDeleted).toBeTruthy();
    });

    test('❌ Debe retornar 404 si producto no existe', async () => {
      const fakeId = new mongoose.Types.ObjectId();

      const response = await request(app)
        .delete(`/api/products/${fakeId}`)
        .set('Authorization', `Bearer ${authToken}`)
        .expect(404);

      expect(response.body.error).toMatch(/no.*encontrado|not.*found/i);
    });
  });

  // ==================== VALIDACIÓN DE DATOS ====================

  describe('Data Validation', () => {
    
    test('✅ Price debe ser número', async () => {
      const response = await request(app)
        .post('/api/products')
        .set('Authorization', `Bearer ${authToken}`)
        .send({ name: 'Test', price: 'not-a-number', stock: 10 })
        .expect(400);

      expect(response.body.error).toMatch(/precio.*número|price.*number/i);
    });

    test('✅ Stock debe ser número entero', async () => {
      const response = await request(app)
        .post('/api/products')
        .set('Authorization', `Bearer ${authToken}`)
        .send({ name: 'Test', price: 100, stock: 10.5 })
        .expect(400);

      expect(response.body.error).toMatch(/stock.*entero|stock.*integer/i);
    });

    test('✅ Name no debe estar vacío', async () => {
      const response = await request(app)
        .post('/api/products')
        .set('Authorization', `Bearer ${authToken}`)
        .send({ name: '   ', price: 100, stock: 10 })
        .expect(400);

      expect(response.body.error).toMatch(/nombre.*vacío|name.*empty/i);
    });

    test('✅ Image debe ser URL válida (si se proporciona)', async () => {
      const response = await request(app)
        .post('/api/products')
        .set('Authorization', `Bearer ${authToken}`)
        .send({ 
          name: 'Test', 
          price: 100, 
          stock: 10,
          image: 'not-a-valid-url'
        })
        .expect(400);

      expect(response.body.error).toMatch(/url.*válida|valid.*url/i);
    });
  });
});
