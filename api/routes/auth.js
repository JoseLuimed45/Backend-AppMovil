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
      return res.status(400).json({ message: 'name, email y password son requeridos' });
    }

    if (typeof email !== 'string' || !isValidEmail(email)) {
      return res.status(400).json({ message: 'Debe proporcionar un email válido' });
    }

    if (typeof password !== 'string' || password.length < 6) {
      return res.status(400).json({ message: 'Password debe tener al menos 6 caracteres' });
    }

    const exists = await User.findOne({ email: email.toLowerCase() });
    if (exists) {
      return res.status(409).json({ message: 'El email ya existe' });
    }

    const hashed = await bcrypt.hash(password, 10);
    const user = await User.create({ name, email: email.toLowerCase(), password: hashed });

    const secret = process.env.JWT_SECRET || 'test-secret';
    const token = jwt.sign({ user_id: user._id.toString(), email: user.email }, secret, { expiresIn: '7d' });

    // Respuesta plana que espera la app Android
    return res.status(201).json({
      _id: user._id.toString(),
      nombre: user.name,
      email: user.email,
      telefono: user.telefono || '',
      direccion: user.direccion || '',
      token: token,
      rol: user.role || 'user'
    });
  } catch (err) {
    if (err instanceof mongoose.Error.ValidationError) {
      return res.status(400).json({ message: 'Datos inválidos' });
    }
    return res.status(500).json({ message: 'Error interno' });
  }
});

router.post('/login', async (req, res) => {
  try {
    const { email, password } = req.body || {};

    if (!email || !password) {
      return res.status(400).json({ message: 'email y password son requeridos' });
    }

    if (typeof email !== 'string') {
      return res.status(400).json({ message: 'Debe proporcionar un email válido' });
    }

    const user = await User.findOne({ email: email.toLowerCase() });
    if (!user) {
      return res.status(404).json({ message: 'Usuario no existe' });
    }

    const ok = await bcrypt.compare(password, user.password);
    if (!ok) {
      return res.status(401).json({ message: 'Credenciales incorrectas' });
    }

    const secret = process.env.JWT_SECRET || 'test-secret';
    const token = jwt.sign({ user_id: user._id.toString(), email: user.email }, secret, { expiresIn: '7d' });

    // Respuesta plana que espera la app Android
    return res.status(200).json({
      _id: user._id.toString(),
      nombre: user.name,
      email: user.email,
      telefono: user.telefono || '',
      direccion: user.direccion || '',
      token: token,
      rol: user.role || 'user'
    });
  } catch (err) {
    return res.status(500).json({ message: 'Error interno' });
  }
});

module.exports = router;
