
function handleResult(resultData) {
    console.log(resultData);
    var metadataElement = $("#metadata-group");
    for (var i = 0; i<resultData.length;i++){
        var table = resultData[i];
        var tableHTML = "<h5>" + table["table-name"] + "</h5>"+
        '<table class="table table-striped" style="width: 50%">\n' +
            '        <thead>\n' +
            '        <tr >\n' +
            '            <th style="width: 25%">name</th>\n' +
            '            <th style="width: 25%">type</th>\n' +
            '        </tr>\n' +
            '        </thead><tbody>';
        var attrs = table["attrs"];
        for (var j = 0; j < attrs.length; j++)
        {
            var rowHTML = "<tr>";
            rowHTML += "<td style=\"width: 25%\">" + attrs[j]["col-name"] + "</td>";
            rowHTML += "<td style=\"width: 25%\">" + attrs[j]["data-type"]+"</td>";
            rowHTML+= "</tr>";
            tableHTML += rowHTML;
        }
        tableHTML+= "</tbody></table>";
        metadataElement.append(tableHTML);
    }

}

jQuery.ajax({
    dataType: "json",  // Setting return data type
    method: "GET",// Setting request method
    url: "/api/_dashboard/metadata", // Setting request url, which is mapped by StarsServlet in Stars.java
    success: (resultData) => handleResult(resultData) // Setting callback function to handle data returned successfully by the SingleStarServlet
});