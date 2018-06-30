

/**
 * Handles the data returned by the API, read the jsonObject and populate data into html elements
 * @param resultData jsonObject
 */

function handleResult(resultData) {
    console.log(resultData);
    let starInfoElement = jQuery("#movie_title");

    // append two html <p> created to the h3 body, which will refresh the page
    starInfoElement.append("<p>"+resultData["movie_title"] +
        '<a href="shopping-cart.html?count=1&op=add&itemId='+resultData["movie_id"]+
        '&itemName='+resultData["movie_title"]+
        '" target="_blank"><i class="fa fa-cart-arrow-down" aria-hidden="true"></i></a>'+ "</p>");

    // Populate the star table
    // Find the empty table body by id "movie_table_body"
    let movieTableBodyElement = jQuery("#movie_table_body");
    // Concatenate the html tags with resultData jsonObject to create table rows

    let rowHTML = "";
    rowHTML += "<tr>";
    rowHTML += "<td>" + resultData["movie_id"] + "</td>";
    rowHTML += "<td>" + resultData["movie_year"] + "</td>";
    rowHTML += "<td>" + resultData["movie_director"] + "</td>";

    let genresRow = "";
    for (let i = 0; i < resultData["genres"].length; i++) {
        genresRow += '<a href="list.html?genre=' + resultData["genres"][i]["genre_id"] + '">'
            + resultData["genres"][i]["genre_name"] +
            '</a>';
        if (i != resultData["genres"].length - 1) {
            genresRow += ", ";
        }
    }
    rowHTML += "<td>" + genresRow + "</td>";

    let starRow = "";
    for (let i = 0; i < resultData["stars"].length; i++) {
        starRow += '<a href="single-star.html?id=' + resultData["stars"][i]["star_id"] + '">'
            + resultData["stars"][i]["star_name"] +
            '</a>';
        if (i != resultData["stars"].length - 1) {
            starRow += ", ";
        }
    }
        rowHTML += "<td>" + starRow + "</td>";
        if (resultData["rating"]==null)
            resultData["rating"]="-";
        rowHTML += "<td>" + resultData["rating"] + "</td>";
        rowHTML += "</tr>";

        // Append the row created to the table body, which will refresh the page
        movieTableBodyElement.append(rowHTML);

}

// Makes the HTTP GET request and registers on success callback function handleResult
jQuery.ajax({
    dataType: "json",  // Setting return data type
    method: "GET",// Setting request method
    url: "/api/movie?id=" + getParameterByName("id"), // Setting request url, which is mapped by StarsServlet in Stars.java
    success: (resultData) => handleResult(resultData) // Setting callback function to handle data returned successfully by the SingleStarServlet
});

