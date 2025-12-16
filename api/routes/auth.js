const express = require('express');
const bcrypt = require('bcryptjs');
const jwt = require('jsonwebtoken');
const mongoose = require('mongoose');
const User = require('../models/User');

const router = express.Router();

function isValidEmail(email) {
  return /.+@.+\..+/.test(email);
}

router.post('/register', async (req, res) => {
  try {
    const { name, email, password } = req.body || {};

    if (!name || !email || !password) {
      return res.status(400).json({ error: 'name, email y password son requeridos' });
    }

    if (typeof email !== 'string' || !isValidEmail(email)) {
      return res.status(400).json({ error: 'Debe proporcionar un email válido' });
    }

    if (typeof password !== 'string' || password.length < 6) {
      return res.status(400).json({ error: 'Password debe tener al menos 6 caracteres' });
    }

    const exists = await User.findOne({ email: email.toLowerCase() });
    if (exists) {
      return res.status(409).json({ error: 'El email ya existe' });
    }

    const hashed = await bcrypt.hash(password, 10);
    const user = await User.create({ name, email: email.toLowerCase(), password: hashed });

    const secret = process.env.JWT_SECRET || 'test-secret';
    const token = jwt.sign({ user_id: user._id.toString(), email: user.email }, secret, { expiresIn: '7d' });

    const safeUser = { _id: user._id, name: user.name, email: user.email, role: user.role, createdAt: user.createdAt };

    return res.status(201).json({ token, user: safeUser });
  } catch (err) {
    if (err instanceof mongoose.Error.ValidationError) {
      return res.status(400).json({ error: 'Datos inválidos' });
    }
    return res.status(500).json({ error: 'Error interno' });
  }
});

router.post('/login', async (req, res) => {
  try {
    const { email, password } = req.body || {};

    if (!email || !password) {
      return res.status(400).json({ error: 'email y password son requeridos' });
    }

    if (typeof email !== 'string') {
      return res.status(400).json({ error: 'Debe proporcionar un email válido' });
    }

    const user = await User.findOne({ email: email.toLowerCase() });
    if (!user) {
      return res.status(404).json({ error: 'Usuario no existe' });
    }

    const ok = await bcrypt.compare(password, user.password);
    if (!ok) {
      return res.status(401).json({ error: 'Credenciales incorrectas' });
    }

    const secret = process.env.JWT_SECRET || 'test-secret';
    const token = jwt.sign({ user_id: user._id.toString(), email: user.email }, secret, { expiresIn: '7d' });

    const safeUser = { _id: user._id, name: user.name, email: user.email, role: user.role, createdAt: user.createdAt };

    return res.status(200).json({ token, user: safeUser });
  } catch (err) {
    return res.status(500).json({ error: 'Error interno' });
  }
});

module.exports = router;
