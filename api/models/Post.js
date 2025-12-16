const mongoose = require('mongoose');

const postSchema = new mongoose.Schema({
  user: { type: mongoose.Schema.Types.ObjectId, required: true },
  title: { type: String, required: true, trim: true },
  content: { type: String, required: true },
  image: { type: String },
  isVisible: { type: Boolean, default: true },
}, { timestamps: true });

module.exports = mongoose.models.Post || mongoose.model('Post', postSchema);
