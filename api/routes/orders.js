const express = require('express');
const mongoose = require('mongoose');
const Order = require('../models/Order');
const Product = require('../models/Product');
const auth = require('../middleware/auth');

const router = express.Router();

router.post('/', auth, async (req, res) => {
  const { products } = req.body || {};
  if (!Array.isArray(products) || products.length === 0) {
    return res.status(400).json({ error: 'productos requeridos' });
  }

  // Validar cantidades
  for (const p of products) {
    if (!p.product_id || !mongoose.Types.ObjectId.isValid(p.product_id)) {
      return res.status(400).json({ error: 'product_id inválido' });
    }
    if (!Number.isInteger(p.quantity) || p.quantity <= 0) {
      return res.status(400).json({ error: 'cantidad debe ser válida' });
    }
  }

  // Verificar stock y calcular total
  let total = 0;
  const items = [];

  for (const p of products) {
    const product = await Product.findById(p.product_id);
    if (!product) return res.status(404).json({ error: 'Producto no encontrado' });
    if (product.stock < p.quantity) {
      return res.status(409).json({ error: 'Stock insuficiente' });
    }
    total += product.price * p.quantity;
    items.push({ product_id: product._id, quantity: p.quantity, price: product.price });
  }

  // Descontar stock
  for (const item of items) {
    await Product.findByIdAndUpdate(item.product_id, { $inc: { stock: -item.quantity } });
  }

  const order = await Order.create({ user_id: req.user.user_id, products: items, total, status: 'pending' });
  return res.status(201).json(order);
});

router.get('/', auth, async (req, res) => {
  const orders = await Order.find({ user_id: req.user.user_id }).lean();
  return res.status(200).json(orders);
});

router.get('/:id', auth, async (req, res) => {
  const { id } = req.params;
  if (!mongoose.Types.ObjectId.isValid(id)) {
    return res.status(400).json({ error: 'ID inválido' });
  }
  const order = await Order.findById(id).lean();
  if (!order) return res.status(404).json({ error: 'Pedido no encontrado' });
  if (order.user_id.toString() !== req.user.user_id) {
    return res.status(403).json({ error: 'Sin permisos para ver este pedido' });
  }
  return res.status(200).json(order);
});

router.put('/:id/status', auth, async (req, res) => {
  const { id } = req.params;
  const { status } = req.body || {};
  if (!mongoose.Types.ObjectId.isValid(id)) {
    return res.status(400).json({ error: 'ID inválido' });
  }
  const valid = ['pending', 'processing', 'shipped', 'delivered', 'cancelled'];
  if (!valid.includes(status)) {
    return res.status(400).json({ error: 'Estado inválido' });
  }
  const order = await Order.findByIdAndUpdate(id, { status }, { new: true });
  if (!order) return res.status(404).json({ error: 'Pedido no encontrado' });
  return res.status(200).json(order);
});

module.exports = router;
