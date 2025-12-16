const mongoose = require('mongoose');

const productSchema = new mongoose.Schema({
  id: { type: String, required: true, unique: true },
  nombre: { type: String, required: true, trim: true },
  descripcion: { type: String, default: '' },
  precio: { type: Number, required: true, min: 0.01 },
  categoria: { type: String, required: true },
  stock: { type: Number, default: 0, min: 0 },
  imagenUrl: { type: String, default: '' },
}, { timestamps: true });

module.exports = mongoose.models.Product || mongoose.model('Product', productSchema);
