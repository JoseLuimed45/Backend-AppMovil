const express = require('express');
const mongoose = require('mongoose');
const Product = require('../models/Product');
const auth = require('../middleware/auth');

const router = express.Router();

router.get('/', async (req, res) => {
  const products = await Product.find({}).lean();
  return res.status(200).json(products);
});

router.get('/:id', async (req, res) => {
  const { id } = req.params;
  if (!mongoose.Types.ObjectId.isValid(id)) {
    return res.status(400).json({ error: 'ID inválido' });
  }
  const product = await Product.findById(id).lean();
  if (!product) return res.status(404).json({ error: 'Producto no encontrado' });
  return res.status(200).json(product);
});

router.post('/', auth, async (req, res) => {
  const { name, description, price, category, stock, image } = req.body || {};
  if (!name || typeof name !== 'string' || name.trim() === '') {
    return res.status(400).json({ error: 'Nombre no debe estar vacío' });
  }
  if (typeof price !== 'number' || isNaN(price) || price <= 0) {
    return res.status(400).json({ error: 'Precio debe ser un número válido' });
  }
  if (stock != null && (!Number.isInteger(stock) || stock < 0)) {
    return res.status(400).json({ error: 'Stock debe ser un entero válido' });
  }
  if (image && !/^https?:\/\//.test(image)) {
    return res.status(400).json({ error: 'Image debe ser URL válida' });
  }

  const product = await Product.create({ name: name.trim(), description, price, category, stock, image });
  return res.status(201).json(product);
});

router.put('/:id', auth, async (req, res) => {
  const { id } = req.params;
  if (!mongoose.Types.ObjectId.isValid(id)) {
    return res.status(400).json({ error: 'ID inválido' });
  }
  const updates = req.body || {};
  if (updates.price != null && (typeof updates.price !== 'number' || isNaN(updates.price) || updates.price <= 0)) {
    return res.status(400).json({ error: 'Precio inválido' });
  }
  if (updates.stock != null && (!Number.isInteger(updates.stock) || updates.stock < 0)) {
    return res.status(400).json({ error: 'Stock inválido' });
  }

  const product = await Product.findByIdAndUpdate(id, updates, { new: true });
  if (!product) return res.status(404).json({ error: 'Producto no encontrado' });
  return res.status(200).json(product);
});

router.delete('/:id', auth, async (req, res) => {
  const { id } = req.params;
  if (!mongoose.Types.ObjectId.isValid(id)) {
    return res.status(400).json({ error: 'ID inválido' });
  }
  const result = await Product.findByIdAndDelete(id);
  if (!result) return res.status(404).json({ error: 'Producto no encontrado' });
  return res.status(200).json({ message: 'Producto eliminado' });
});

module.exports = router;
