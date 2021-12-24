const Utils = require("../controller/utils");

let Users = require("../model/model");
const objectID = require("mongodb").ObjectId;

exports.create = (req, res) => {
  if (req.body === null) {
    return res.send(Utils.Response("400", "Bad Request", {}));
  }
  console.table(req.body);
  let user = new Users({
    nama: req.body.nama,
    email: req.body.email,
    password: req.body.password,
    nomor_hp: req.body.nomor_hp,
    confidence: Number(req.body.confidence),
    status: Boolean(req.body.status),
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
        return res.send(Utils.ResponseNoData(404, "Cannot Find Data"));
      }
      return res.send(Utils.ResponseNoData(200, "Success Delete Data"));
    })
    .catch((err) => {
      return res.status(500).send({ message: err.message });
    });
};
exports.update = (req, res) => {
  console.table(req);
  if (req.body === null) {
    return res.send(Utils.Response("400", "Bad Request", {}));
  }
  const id = req.params.id;
  Users.findByIdAndUpdate(objectID(id), req.body)
    .then((data) => {
      console.table(data);
      if (data === null) {
        return res.status(404).send(`User Not Exists ${id}`);
      }
      return res.send(Utils.Response(200, "Success", data));
    })
    .catch((err) => {
      return res.status(500).send({ message: err.message });
    });
};

exports.getByEmail = (req, res) => {
  if (!req.body) {
    return res.status(400).send({ message: "Bad Request" });
  } else {
    const reqGans = { email: req.body.email, password: req.body.password };
    Users.findOne(reqGans, (request, result) => {
      if (!result) {
        return res.send(Utils.Response(400, "No Data Found", result));
      } else {
        result.status = req.body.status;
        result.save(result);
        return res.send(Utils.Response(200, "Success", result));
      }
    });
  }
};

exports.createDataForMobile = (req, res) => {
  console.table(req.body);
  if (!req.body) {
    return res.send(Utils.ResponseNoData(400, "Bad Request"));
  }
  let user = new Users({
    nama: req.body.nama,
    email: req.body.email,
    password: req.body.password,
    nomor_hp: req.body.nomor_hp,
    confidence: req.body.confidence,
    status: req.body.status,
  });
  Users.findOne({ email: req.body.email }, (err, result) => {
    if (!result) {
      user
        .save(user)
        .then((data) => {
          return res.send(Utils.ResponseNoData(200, "Success"));
        })
        .catch((err) => {
          return res.send(Utils.ResponseNoData(500, "Bad Request"));
        });
    } else {
      return res.send(Utils.ResponseNoData(400, "Email Already Exists"));
    }
  });
};
