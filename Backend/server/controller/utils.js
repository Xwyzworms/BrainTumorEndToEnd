exports.ResponseBad = (code, message) => {
  return JSON.stringify({ message: message, code: code });
};

exports.ResponseOk = (code, message, data) => {
  return JSON.stringify({ message: message, code: code, data: data });
};
