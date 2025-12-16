const mongoose = require('mongoose');

async function connectDB(uri) {
  const mongoUri = uri || process.env.MONGO_URI || process.env.MONGODB_URI;
  if (!mongoUri) {
    console.warn('[DB] MONGO_URI no definido. Saltando conexión.');
    return;
  }

  if (mongoose.connection.readyState === 1) {
    return;
  }

  await mongoose.connect(mongoUri, {
    serverSelectionTimeoutMS: 10000,
  });

  console.log('[DB] Conectado a MongoDB');
}

module.exports = { connectDB };
