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
  try {
    // Buscar por el campo 'id' personalizado
    const product = await Product.findOne({ id }).lean();
    if (!product) return res.status(404).json({ message: 'Producto no encontrado' });
    return res.status(200).json(product);
  } catch (error) {
    return res.status(500).json({ message: 'Error al buscar producto' });
  }
});

router.post('/', auth, async (req, res) => {
  const { id, nombre, descripcion, precio, categoria, stock, imagenUrl } = req.body || {};

  if (!id || typeof id !== 'string' || id.trim() === '') {
    return res.status(400).json({ message: 'ID es requerido' });
  }
  if (!nombre || typeof nombre !== 'string' || nombre.trim() === '') {
    return res.status(400).json({ message: 'Nombre no debe estar vacío' });
  }
  if (!categoria || typeof categoria !== 'string' || categoria.trim() === '') {
    return res.status(400).json({ message: 'Categoría es requerida' });
  }
  if (typeof precio !== 'number' || isNaN(precio) || precio <= 0) {
    return res.status(400).json({ message: 'Precio debe ser un número válido mayor a 0' });
  }
  if (stock != null && (!Number.isInteger(stock) || stock < 0)) {
    return res.status(400).json({ message: 'Stock debe ser un entero no negativo' });
  }
  if (imagenUrl && typeof imagenUrl === 'string' && imagenUrl.trim() !== '' && !/^https?:\/\//.test(imagenUrl)) {
    return res.status(400).json({ message: 'imagenUrl debe ser una URL válida' });
  }

  try {
    // Verificar si ya existe un producto con ese ID
    const existingProduct = await Product.findOne({ id: id.trim() });
    if (existingProduct) {
      return res.status(409).json({ message: 'Ya existe un producto con ese ID' });
    }

    const product = await Product.create({
      id: id.trim(),
      nombre: nombre.trim(),
      descripcion: descripcion || '',
      precio,
      categoria: categoria.trim(),
      stock: stock || 0,
      imagenUrl: imagenUrl || ''
    });
    return res.status(201).json(product);
  } catch (error) {
    if (error.code === 11000) {
      return res.status(409).json({ message: 'Ya existe un producto con ese ID' });
    }
    return res.status(500).json({ message: 'Error al crear producto' });
  }
});

router.put('/:id', auth, async (req, res) => {
  const { id } = req.params;

  const updates = req.body || {};
  if (updates.precio != null && (typeof updates.precio !== 'number' || isNaN(updates.precio) || updates.precio <= 0)) {
    return res.status(400).json({ message: 'Precio inválido' });
  }
  if (updates.stock != null && (!Number.isInteger(updates.stock) || updates.stock < 0)) {
    return res.status(400).json({ message: 'Stock inválido' });
  }
  if (updates.imagenUrl && typeof updates.imagenUrl === 'string' && updates.imagenUrl.trim() !== '' && !/^https?:\/\//.test(updates.imagenUrl)) {
    return res.status(400).json({ message: 'imagenUrl debe ser una URL válida' });
  }

  try {
    // Buscar por el campo 'id' personalizado, no por _id de MongoDB
    const product = await Product.findOneAndUpdate({ id }, updates, { new: true });
    if (!product) return res.status(404).json({ message: 'Producto no encontrado' });
    return res.status(200).json(product);
  } catch (error) {
    return res.status(500).json({ message: 'Error al actualizar producto' });
  }
});

router.delete('/:id', auth, async (req, res) => {
  const { id } = req.params;
  try {
    // Buscar y eliminar por el campo 'id' personalizado
    const result = await Product.findOneAndDelete({ id });
    if (!result) return res.status(404).json({ message: 'Producto no encontrado' });
    return res.status(200).json({ message: 'Producto eliminado correctamente' });
  } catch (error) {
    return res.status(500).json({ message: 'Error al eliminar producto' });
  }
});

module.exports = router;
