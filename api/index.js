const express = require('express');
const cors = require('cors');
const mongoose = require('mongoose');
const { connectDB } = require('./config/db');

const authRoutes = require('./routes/auth');
const productRoutes = require('./routes/products');
const orderRoutes = require('./routes/orders');

const app = express();
app.use(cors());
app.use(express.json());

app.get('/api/health', async (req, res) => {
  const dbState = mongoose.connection.readyState === 1 ? 'connected' : 'disconnected';
  return res.status(200).json({ status: 'ok', database: dbState, timestamp: new Date().toISOString() });
});

app.use('/api/auth', authRoutes);
app.use('/api/products', productRoutes);
app.use('/api/orders', orderRoutes);

if (process.env.NODE_ENV !== 'test') {
  connectDB().catch(err => {
    console.error('Error conectando a MongoDB:', err.message);
  });
  const PORT = process.env.PORT || 3000;
  app.listen(PORT, () => console.log(`[Server] Escuchando en puerto ${PORT}`));
}

module.exports = app;
