/**
 * Tests Unitarios - Pedidos (Orders)
 * App Ajicolor Backend
 */

const request = require('supertest');
const { MongoMemoryServer } = require('mongodb-memory-server');
const mongoose = require('mongoose');
const { createUserAndGetToken } = require('./auth.test');

jest.setTimeout(30000);

let app;
let mongoServer;
let Order;
let Product;
let authToken;
let userId;

describe('🛒 Orders - Gestión de Pedidos', () => {
  
  beforeAll(async () => {
    mongoServer = await MongoMemoryServer.create();
    const mongoUri = mongoServer.getUri();
    await mongoose.connect(mongoUri);
    
    // Modelo Product
    const productSchema = new mongoose.Schema({
      name: String,
      price: Number,
      stock: Number
    });
    Product = mongoose.models.Product || mongoose.model('Product', productSchema);
    
    // Modelo Order
    const orderSchema = new mongoose.Schema({
      user_id: { type: mongoose.Schema.Types.ObjectId, required: true },
      products: [{
        product_id: mongoose.Schema.Types.ObjectId,
        quantity: Number,
        price: Number
      }],
      total: Number,
      status: { type: String, default: 'pending' },
      createdAt: { type: Date, default: Date.now }
    });
    Order = mongoose.models.Order || mongoose.model('Order', orderSchema);
    
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
    await Order.deleteMany({});
    await Product.deleteMany({});
    
    const { token, user } = await createUserAndGetToken();
    authToken = token;
    userId = user._id;
  });

  // ==================== CREAR PEDIDO ====================

  describe('POST /api/orders', () => {
    
    let product1, product2;

    beforeEach(async () => {
      // Crear productos de prueba
      product1 = await Product.create({
        name: 'Producto 1',
        price: 100,
        stock: 50
      });
      product2 = await Product.create({
        name: 'Producto 2',
        price: 200,
        stock: 30
      });
    });

    test('✅ Debe crear pedido con productos válidos', async () => {
      const newOrder = {
        products: [
          { product_id: product1._id, quantity: 2 },
          { product_id: product2._id, quantity: 1 }
        ]
      };

      const response = await request(app)
        .post('/api/orders')
        .set('Authorization', `Bearer ${authToken}`)
        .send(newOrder)
        .expect(201);

      expect(response.body).toHaveProperty('_id');
      expect(response.body).toHaveProperty('products');
      expect(response.body).toHaveProperty('total');
      expect(response.body).toHaveProperty('status', 'pending');
      expect(response.body.products).toHaveLength(2);
    });

    test('✅ Debe calcular total correctamente', async () => {
      const newOrder = {
        products: [
          { product_id: product1._id, quantity: 2 }, // 100 * 2 = 200
          { product_id: product2._id, quantity: 3 }  // 200 * 3 = 600
        ]
      };

      const response = await request(app)
        .post('/api/orders')
        .set('Authorization', `Bearer ${authToken}`)
        .send(newOrder)
        .expect(201);

      const expectedTotal = (100 * 2) + (200 * 3); // 200 + 600 = 800
      expect(response.body.total).toBe(expectedTotal);
    });

    test('✅ Debe actualizar stock después de pedido', async () => {
      const initialStock1 = product1.stock;
      const initialStock2 = product2.stock;

      const newOrder = {
        products: [
          { product_id: product1._id, quantity: 5 },
          { product_id: product2._id, quantity: 3 }
        ]
      };

      await request(app)
        .post('/api/orders')
        .set('Authorization', `Bearer ${authToken}`)
        .send(newOrder)
        .expect(201);

      const updatedProduct1 = await Product.findById(product1._id);
      const updatedProduct2 = await Product.findById(product2._id);

      expect(updatedProduct1.stock).toBe(initialStock1 - 5);
      expect(updatedProduct2.stock).toBe(initialStock2 - 3);
    });

    test('✅ Debe guardar user_id del comprador', async () => {
      const newOrder = {
        products: [{ product_id: product1._id, quantity: 1 }]
      };

      const response = await request(app)
        .post('/api/orders')
        .set('Authorization', `Bearer ${authToken}`)
        .send(newOrder)
        .expect(201);

      expect(response.body).toHaveProperty('user_id', userId);
    });

    test('❌ Debe rechazar pedido sin autenticación', async () => {
      const newOrder = {
        products: [{ product_id: product1._id, quantity: 1 }]
      };

      const response = await request(app)
        .post('/api/orders')
        .send(newOrder)
        .expect(401);

      expect(response.body).toHaveProperty('error');
    });

    test('❌ Debe rechazar pedido con stock insuficiente', async () => {
      const newOrder = {
        products: [
          { product_id: product1._id, quantity: 1000 } // Más que el stock disponible
        ]
      };

      const response = await request(app)
        .post('/api/orders')
        .set('Authorization', `Bearer ${authToken}`)
        .send(newOrder)
        .expect(409); // Conflict

      expect(response.body.error).toMatch(/stock.*insuficiente|insufficient.*stock/i);
    });

    test('❌ Debe rechazar pedido sin productos', async () => {
      const response = await request(app)
        .post('/api/orders')
        .set('Authorization', `Bearer ${authToken}`)
        .send({ products: [] })
        .expect(400);

      expect(response.body.error).toMatch(/productos.*requeridos|products.*required/i);
    });

    test('❌ Debe rechazar pedido con producto inexistente', async () => {
      const fakeProductId = new mongoose.Types.ObjectId();
      
      const newOrder = {
        products: [{ product_id: fakeProductId, quantity: 1 }]
      };

      const response = await request(app)
        .post('/api/orders')
        .set('Authorization', `Bearer ${authToken}`)
        .send(newOrder)
        .expect(404);

      expect(response.body.error).toMatch(/producto.*encontrado|product.*found/i);
    });

    test('❌ Debe rechazar cantidad cero o negativa', async () => {
      const invalidQuantities = [0, -1, -10];

      for (const quantity of invalidQuantities) {
        const response = await request(app)
          .post('/api/orders')
          .set('Authorization', `Bearer ${authToken}`)
          .send({ products: [{ product_id: product1._id, quantity }] })
          .expect(400);

        expect(response.body.error).toMatch(/cantidad.*válida|valid.*quantity/i);
      }
    });
  });

  // ==================== LISTAR PEDIDOS DEL USUARIO ====================

  describe('GET /api/orders', () => {
    
    test('✅ Debe retornar solo pedidos del usuario autenticado', async () => {
      // Crear pedidos para el usuario actual
      await Order.create([
        { user_id: userId, products: [], total: 100, status: 'pending' },
        { user_id: userId, products: [], total: 200, status: 'delivered' }
      ]);

      // Crear pedido de otro usuario
      const otherUserId = new mongoose.Types.ObjectId();
      await Order.create({
        user_id: otherUserId,
        products: [],
        total: 300,
        status: 'pending'
      });

      const response = await request(app)
        .get('/api/orders')
        .set('Authorization', `Bearer ${authToken}`)
        .expect(200);

      expect(Array.isArray(response.body)).toBe(true);
      expect(response.body).toHaveLength(2); // Solo los 2 del usuario actual
      
      response.body.forEach(order => {
        expect(order.user_id).toBe(userId);
      });
    });

    test('✅ Debe incluir detalles de productos', async () => {
      const product = await Product.create({
        name: 'Test Product',
        price: 100,
        stock: 10
      });

      await Order.create({
        user_id: userId,
        products: [{ product_id: product._id, quantity: 2, price: 100 }],
        total: 200,
        status: 'pending'
      });

      const response = await request(app)
        .get('/api/orders')
        .set('Authorization', `Bearer ${authToken}`)
        .expect(200);

      expect(response.body[0].products).toHaveLength(1);
      expect(response.body[0].products[0]).toHaveProperty('product_id');
      expect(response.body[0].products[0]).toHaveProperty('quantity', 2);
    });

    test('✅ Debe retornar array vacío si no hay pedidos', async () => {
      const response = await request(app)
        .get('/api/orders')
        .set('Authorization', `Bearer ${authToken}`)
        .expect(200);

      expect(Array.isArray(response.body)).toBe(true);
      expect(response.body).toHaveLength(0);
    });

    test('❌ Debe rechazar sin autenticación', async () => {
      const response = await request(app)
        .get('/api/orders')
        .expect(401);

      expect(response.body).toHaveProperty('error');
    });
  });

  // ==================== OBTENER PEDIDO POR ID ====================

  describe('GET /api/orders/:id', () => {
    
    test('✅ Debe retornar pedido específico', async () => {
      const order = await Order.create({
        user_id: userId,
        products: [],
        total: 150,
        status: 'pending'
      });

      const response = await request(app)
        .get(`/api/orders/${order._id}`)
        .set('Authorization', `Bearer ${authToken}`)
        .expect(200);

      expect(response.body).toHaveProperty('_id', order._id.toString());
      expect(response.body).toHaveProperty('total', 150);
    });

    test('❌ Debe verificar que pedido pertenece al usuario', async () => {
      const otherUserId = new mongoose.Types.ObjectId();
      const order = await Order.create({
        user_id: otherUserId,
        products: [],
        total: 100,
        status: 'pending'
      });

      const response = await request(app)
        .get(`/api/orders/${order._id}`)
        .set('Authorization', `Bearer ${authToken}`)
        .expect(403); // Forbidden

      expect(response.body.error).toMatch(/permiso|permission|forbidden/i);
    });

    test('❌ Debe retornar 404 si pedido no existe', async () => {
      const fakeId = new mongoose.Types.ObjectId();

      const response = await request(app)
        .get(`/api/orders/${fakeId}`)
        .set('Authorization', `Bearer ${authToken}`)
        .expect(404);

      expect(response.body.error).toMatch(/no.*encontrado|not.*found/i);
    });
  });

  // ==================== ACTUALIZAR ESTADO ====================

  describe('PUT /api/orders/:id/status', () => {
    
    test('✅ Debe actualizar estado de pedido', async () => {
      const order = await Order.create({
        user_id: userId,
        products: [],
        total: 100,
        status: 'pending'
      });

      const validStatuses = ['processing', 'shipped', 'delivered', 'cancelled'];

      for (const status of validStatuses) {
        const response = await request(app)
          .put(`/api/orders/${order._id}/status`)
          .set('Authorization', `Bearer ${authToken}`) // Requiere admin en producción
          .send({ status })
          .expect(200);

        expect(response.body).toHaveProperty('status', status);
      }
    });

    test('❌ Debe rechazar estado inválido', async () => {
      const order = await Order.create({
        user_id: userId,
        products: [],
        total: 100,
        status: 'pending'
      });

      const response = await request(app)
        .put(`/api/orders/${order._id}/status`)
        .set('Authorization', `Bearer ${authToken}`)
        .send({ status: 'invalid_status' })
        .expect(400);

      expect(response.body.error).toMatch(/estado.*válido|valid.*status/i);
    });

    test('❌ Debe requerir autenticación de admin', async () => {
      const order = await Order.create({
        user_id: userId,
        products: [],
        total: 100,
        status: 'pending'
      });

      const response = await request(app)
        .put(`/api/orders/${order._id}/status`)
        .send({ status: 'delivered' })
        .expect(401);

      expect(response.body).toHaveProperty('error');
    });
  });
});
