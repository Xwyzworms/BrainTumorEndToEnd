const express = require("express");
const app = express();
const mongoClient = require("mongodb").MongoClient;
const objectId = require("mongodb").ObjectId;
const url = "mongodb://localhost:27017/";

const getSuccess = () => {
  obj = {
    code: 200,
    message: "Success",
  };
  return obj;
};

const getFailed = () => {
  obj = {
    code: 400,
    message: "Bad Request",
  };
  return obj;
};

const getResponse = (code, message, data) => {
  obj = {
    code: code,
    message: message,
    data: data,
  };
  return obj;
};
mongoClient.connect(url, (err, db) => {
  if (err) {
    console.log("error While Cnnection mongo Client");
  } else {
    const myDb = db.db("Tumor");
    const collection = myDb.collection("Users");

    app.post("/signup", (req, res) => {
      const newUser = {
        nama: req.body.nama,
        email: req.body.email,
        password: req.body.password,
        nomor_hp: "",
        alamat: "",
        hasil_prediksi: 0,
      };
      console.table(newUser);
      const query = { email: newUser.email };
      collection.findOne(query, (err, result) => {
        if (result === null) {
          collection.insertOne(newUser, (err, result) => {
            console.log("Succesfully Insert");
          });
          return res.status(200).send(JSON.stringify(getSuccess()));
        } else {
          return res.status(400).send(JSON.stringify(getFailed()));
        }
      });
    });

    app.post("/login", (req, res) => {
      const query = {
        email: req.body.email,
        password: req.body.password,
      };
      collection.findOne(query, (err, result) => {
        if (result !== null) {
          const objToSend = {
            id: result._id,
            nama: result.nama,
            email: result.email,
            password: result.password,
            nomor_hp: "",
            alamat: "",
            hasil_prediksi: 0,
          };
          console.table(result.id);
          return res.send(
            JSON.stringify(getResponse(200, "Success", objToSend))
          );
        } else {
          return res.send(JSON.stringify(getResponse(404, "Not Found", {})));
        }
      });
    });
    app.post("/update", (req, res) => {
      const data = {
        email: req.body.email,
        password: req.body.password,
        nama: req.body.nama,
        nomor_hp: req.body.nomor_hp,
        alamat: req.body.alamat,
        hasil_prediksi: req.body.hasil_prediksi,
      };
      var id = req.body.id;
      collection.updateOne(
        { _id: objectId(id) },
        { $set: data },
        (err, result) => {
          if (!err) {
            console.table(data);
            return res.send(JSON.stringify(getResponse(200, "Success", data)));
          }
          return res.send(JSON.stringify(getResponse(400, "Bad Request", {})));
        }
      );
    });
app.post("/delete", (req, res) => {
  collection.deleteOne({_id : objectId(req.body.id)}, (err,result) => {
    if(!err) {
      return res.status(200).send(JSON.stringify(getSuccess()));
    }
    return res.status(200).send(JSON.stringify(getFailed()));
  })
}

  }
});

app.listen(9999, () => {
  console.log("Server is running on port 9999");
});
