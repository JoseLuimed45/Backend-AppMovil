const mongoose = require('mongoose');

const productSchema = new mongoose.Schema({
  name: { type: String, required: true, trim: true },
  description: { type: String },
  price: { type: Number, required: true, min: 0.01 },
  category: { type: String },
  stock: { type: Number, default: 0, min: 0 },
  image: { type: String },
}, { timestamps: true });

module.exports = mongoose.models.Product || mongoose.model('Product', productSchema);
