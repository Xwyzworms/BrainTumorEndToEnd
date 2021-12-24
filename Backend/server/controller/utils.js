exports.ResponseNoData = (code, message) => {
  return JSON.stringify({ message: message, code: code });
};

exports.Response = (code, message, data) => {
  return JSON.stringify({ message: message, code: code, data: data });
};
