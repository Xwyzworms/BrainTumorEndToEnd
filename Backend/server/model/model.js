const mongoose = require("mongoose");

let data = {
  nama: { type: String, required: true },
  email: { type: String, required: true, unique: true },
  password: { type: String, required: true },
  nomor_hp: { type: String, required: false },
  confidence: { type: Number, required: false },
};
let Schema = new mongoose.Schema(data);

const Users = mongoose.model("users", Schema);

module.exports = Users;
