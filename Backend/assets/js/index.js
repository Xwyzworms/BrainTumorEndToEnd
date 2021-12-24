$(document).ready(() => {
  console.log("ready");

  $("#sampis").submit(function (event) {
    alert("Data Inserted Succesfully");
  });

  $("#tolo").submit((event) => {
    event.preventDefault();

    let unindexed_array = $(event.target).serializeArray();
    let data = {};

    $.map(unindexed_array, function (n, i) {
      data[n["name"]] = n["value"];
    });

    console.table(data);
    let request = {
      url: `http://localhost:9999/api/users/${data.id}`,
      method: "PUT",
      data: data,
    };
    $.ajax(request).done((response) => {
      console.log(response);
      alert("Data Updated Succesfully");
    });
  });

  console.log(window.location.pathname);
  if (window.location.pathname == "/") {
    jQuery(".table tbody td a.delete").click((evt) => {
      evt.preventDefault();
      let id = jQuery($(evt.target)).attr("data-id");
      if (id) {
        let request = {
          url: `http://localhost:9999/api/users/${id}`,
          method: "DELETE",
        };
        console.table(request);
        if (confirm("GAs Delete ? ")) {
          jQuery.ajax(request).done((response) => {
            alert("Data Deleted Succesfully");
            location.reload();
          });
        }
      }
    });
  }
});
