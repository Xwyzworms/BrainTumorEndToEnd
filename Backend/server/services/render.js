const axios = require("axios");

exports.home = (req, res) => {
  axios
    .get("http://localhost:9999/api/users")
    .then((response) => {
      return res.render("index", { users: response.data });
    })
    .catch((err) => {
      return res.send(err);
    });
};
exports.add_user = (req, res) => {
  return res.render("add_user");
};
exports.update_user = (req, res) => {
  axios
    .get("http://localhost:9999/api/users", { params: { id: req.query.id } })
    .then((response) => {
      return res.render("update_user", { user: response.data });
    })
    .catch((err) => {
      return res.send(err);
    });
};
