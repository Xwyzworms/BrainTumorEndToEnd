const express = require("express");
const app = express();
const mongoClient = require("mongodb").MongoClient;

const url = "mongodb://localhost:27017/";

app.use(express.json());
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
        name: req.body.nama,
        email: req.body.email,
        password: req.body.password,
      };
      console.table(newUser);
      const query = { email: newUser.email };
      collection.findOne(query, (err, result) => {
        if (result === null) {
          collection.insertOne(newUser, (err, result) => {
            console.log("Succesfully Insert");
          });
          console.log(res.status(200).send(JSON.stringify(getSuccess())));
          return res.status(200).send(getSuccess());
        } else {
          return res.status(400).send(getFailed());
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
          if (result.nomor_hp === null) {
            result.nomor_hp = "";
          }
          const objToSend = {
            message: "Success",
            code: 200,
            data: {
              name: result.name,
              email: result.email,
              password: result.password,
              nomor_hp: result.nomor_hp,
            },
          };
          console.table(objToSend);
          return res.send(
            JSON.stringify(getResponse(200, "Success", objToSend))
          );
        } else {
          return res.send(JSON.stringify(getResponse(404, "Not Found", {})));
        }
      });
    });
  }
});

app.listen(9999, () => {
  console.log("Server is running on port 9999");
});
