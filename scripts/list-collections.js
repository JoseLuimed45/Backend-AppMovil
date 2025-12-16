const mongoose = require('mongoose');

async function listCollections(uri) {
  try {
    console.log('Conectando a:', uri);
    await mongoose.connect(uri, { serverSelectionTimeoutMS: 10000 });
    const db = mongoose.connection.db;
    const cols = await db.listCollections().toArray();
    console.log('Colecciones:');
    for (const c of cols) {
      console.log('-', c.name);
    }
  } catch (err) {
    console.error('Error:', err.message);
  } finally {
    await mongoose.disconnect();
  }
}

const uri = process.env.MONGO_URI || process.env.MONGODB_URI;
if (!uri) {
  console.error('Definir MONGO_URI/MONGODB_URI');
  process.exit(1);
}
listCollections(uri);
