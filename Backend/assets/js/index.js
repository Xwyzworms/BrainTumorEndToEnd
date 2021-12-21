$("#sampis").submit(function (event) {
  alert("Data Inserted Succesfully");
});

$("#tolo").submit((event) => {
  event.preventDefault();

  let unindexed_array = $("#tolo").serializeArray();
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
console.log("SAMPIS");
if (window.location.pathname == "/") {
  let $ondelete = $(".table tbody td a.delete");
  $ondelete.click((event) => {
    console.table($ondelete.attr);
    let id = $ondelete.attr("data-id");

    let request = {
      url: `http://localhost:9999/api/users/${id}`,
      method: "DELETE",
    };
    console.table(request);
    if (confirm("GAs Delete ? ")) {
      $.ajax(request).done((response) => {
        alert("Data Deleted Succesfully");
        location.reload();
      });
    }
  });
}
