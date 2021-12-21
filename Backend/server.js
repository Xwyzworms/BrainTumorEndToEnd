const express = require("express");
const dotenv = require("dotenv");
const morgan = require("morgan");
const bodyParser = require("body-parser");
const path = require("path");

const app = express();

dotenv.config({ path: "config.env" });
const PORT = process.env.PORT || 9999;

//logging Request
app.use(morgan("tiny"));

//parase request to BodyParser
app.use(bodyParser.urlencoded({ extended: false }));

//set View Engine
app.set("view engine", "ejs");
//app.set("views", path.resolve(__dirname, "views/ejs"));

//setting paths
app.use("/css", express.static(path.resolve(__dirname, "assets/css")));
app.use("/img", express.static(path.resolve(__dirname, "assets/img")));
app.use("/js", express.static(path.resolve(__dirname, "assets/js")));

app.use("/", require("./server/routes/router"));

app.listen(PORT, () => {
  console.log(`Server started on port ${PORT}`);
});
