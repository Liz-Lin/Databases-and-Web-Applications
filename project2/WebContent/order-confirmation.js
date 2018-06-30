
function handleResult(resultData) {
    var tableElement = jQuery("#order_table");
    for (var i = 0; i < resultData.length; i++)
    {
        var rowHTML = "<tr>";
        rowHTML += "<td>" + resultData[i]["movie-id"] + "</td>";
        rowHTML += "<td>" + resultData[i]["movie-title"] + "</td>";
        rowHTML += "<td>" + resultData[i]["qty"] + "</td>";
        rowHTML += "<td>"+ resultData[i]["sale-id-list"].join(", ")+"</td>";
        rowHTML += "</tr>";
        tableElement.append(rowHTML);
    }

}


jQuery.ajax({
    dataType: "json",  // Setting return data type
    method: "GET",// Setting request method
    url: "/api/order", // Setting request url, which is mapped by StarsServlet in Stars.java
    success: (resultData) => handleResult(resultData) // Setting callback function to handle data returned successfully by the SingleStarServlet
});