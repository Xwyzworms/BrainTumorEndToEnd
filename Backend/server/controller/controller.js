const utils = require("../controller/utils");

let Users = require("../model/model");
const objectID = require("mongodb").ObjectId;

exports.create = (req, res) => {
  if (req.body === null) {
    return res.send(utils.ResponseBad("400", "Bad Request"));
  }
  let user = new Users({
    nama: req.body.nama,
    email: req.body.email,
    password: req.body.password,
    nomor_hp: req.body.nomor_hp,
    confidence: req.body.confidence,
  });
  user
    .save(user)
    .then((data) => {
      return res.redirect("/add-user");
    })
    .catch((err) => {
      return res.status(500).send({ message: err.message + "sdh" });
    });
};
exports.find = (req, res) => {
  if (req.query.id) {
    Users.findById(objectID(req.query.id))
      .then((data) => {
        if (!data) {
          return res.status(404).send({ message: "Cannot Find Data" });
        }
        return res.status(200).send(data);
      })
      .catch((err) => {
        return res.status(500).send({ message: err.message });
      });
  } else {
    Users.find()
      .then((user) => {
        return res.send(user);
      })
      .catch((err) => {
        return res.status(500).send({ message: err.message });
      });
  }
};
exports.delete = (req, res) => {
  if (req.body === null) {
    return res.status(400).send({ message: "Bad Request" });
  }
  const id = req.params.id;
  Users.findByIdAndDelete(objectID(id))
    .then((data) => {
      if (!data) {
        return res.status(404).send({ message: "Cannot Delete Data" });
      }
      return res.status(200).send({ message: "Success Delete Data" });
    })
    .catch((err) => {
      return res.status(500).send({ message: err.message });
    });
};
exports.update = (req, res) => {
  if (req.body === null) {
    return res.send(utils.ResponseBad("400", "Bad Request"));
  }
  console.log("SAMPIS");
  const id = req.params.id;
  console.table(req.body);
  Users.findByIdAndUpdate(objectID(id), req.body)
    .then((data) => {
      if (data === null) {
        return res.status(404).send(`User Not Exists ${id}`);
      }
      return res.send(data);
    })
    .catch((err) => {
      return res.status(500).send({ message: err.message });
    });
};
