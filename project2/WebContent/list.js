

/**
 * Handles the data returned by the API, read the jsonObject and populate data into html elements
 * @param resultData jsonObject
 */

var genre;
var all;
var orderBy;
var offset;
var limit;
var isASC;
var title;
var year;
var director;
var starName;
var startsWith;
var is_android;

function getNewPage()
{
    window.location.replace("/list.html"+generateParms());
}

function changeLimit(newLimit) {
    limit=Math.max(Number(newLimit),0);
    getNewPage();
}

function setSortBy(newSortBy)
{
    if(orderBy===newSortBy){
        isASC= isASC==1?0:1;
    }
    else{
        orderBy=newSortBy;
        isASC=1;
    }
    offset=0;

    getNewPage();
}

function nextPage(isNext) {
    if (isNext===1){
        offset+=1;
    }
    else if(isNext===0){
        offset=Math.max(offset-1, 0);
    }
    getNewPage();
}

function setPagination(dataLength)
{
    if (offset===0) {
        $("#prev").addClass('disabled');
    }
    if (dataLength < limit)
    {
        $("#next").addClass('disabled');
    }
    $("#limitButton").text("display: " +limit);
}


function handleResult(resultData) {
    console.log(resultData);
    if (resultData["status"]!=="success") alert("failed to get data, error: "+resultData["message"]);
    else{
        resultData=resultData["data"];
    }
    // Find the empty table body by id "movie_table_body"
    setPagination(resultData.length);
    var movieTableBodyElement = jQuery("#movie_table_body");
    // Concatenate the html tags with resultData jsonObject to create table rows
    for (var i = 0; i < resultData.length; i++) {
        var rowHTML = "<tr>";
        rowHTML += "<td>" + resultData[i]["movie_id"] +'&nbsp;'+
            '<a href="shopping-cart.html?count=1&op=add&itemId='+resultData[i]["movie_id"]+
            '&itemName='+resultData[i]["movie_title"]+
        '" target="_blank"><i class="fa fa-cart-arrow-down" aria-hidden="true"></i></a>' +"</td>";
        rowHTML +=
            "<td>" +

            '<a href="single-movie.html?id=' + resultData[i]['movie_id'] + ' " target="_blank">'+
            resultData[i]["movie_title"] +
            '</a>' +
            "</td>";
        rowHTML += "<td>" + resultData[i]["movie_year"] + "</td>";
        rowHTML += "<td>" + resultData[i]["movie_director"] + "</td>";
        rowHTML += "<td>" + resultData[i]["genres"] + "</td>";

        let starRow = "";
        for (let j = 0; j < resultData[i]["stars"].length; j++)
        {
            starRow += '<a href="single-star.html?id=' + resultData[i]["stars"][j]["star_id"] + '" target="_blank">'
                + resultData[i]["stars"][j]["star_name"] +
                '</a>';
            if (j != resultData[i]["stars"].length-1 )
            {
                starRow+=", ";
            }
        }
        rowHTML += "<td>" + starRow +  "</td>";
        // rowHTML += "<th>" + resultData[i]["stars"] + "</th>";
        if(resultData[i]["rating"] == null)
            resultData[i]["rating"] = '-';
        rowHTML += "<td>" + resultData[i]["rating"] + "</td>";
        rowHTML += "</tr>";

        // Append the row created to the table body, which will refresh the page
        movieTableBodyElement.append(rowHTML);
    }
    // // create table with pagination
    // var table = $('#movie_table').DataTable({
    //     dom: 'Alfrtip',
    //
    //     alphabetSearch: {
    //         column: 1
    //     },
    //     "columnDefs": [
    //         { "orderable": false, "targets": [2,3,4,5] }
    //
    //     ],
    //     "order": [[ 1, "asc" ]]//why it doesnt do anything??!!!
    //
    // });

}

function getParms() {
    year=getParameterByName("Movieyear");
    genre = getParameterByName("genre");
    all = getParameterByName("all");
    if(all!=="1") all="0";
    is_android = "0";
    orderBy = getParameterByName("orderBy");
    if (orderBy==='') orderBy="title";
    offset = getParameterByName("offset");
    if(offset==='') offset=0;
    else offset=Math.max(Number(offset),0);
    limit = getParameterByName("limit");
    if (limit==='') limit=20;
    else limit=Math.max(Number(limit), 0);
    isASC = getParameterByName("isASC");
    if (isASC==='') isASC=1;
    title=getParameterByName("MovieTitle");
    director=getParameterByName("Director");
    starName=getParameterByName("StarName");
    startsWith=getParameterByName("startsWith");

}

function generateParms()
{
    console.log("starts with: "+startsWith);
    var parms ="?all="+all +"&genre="+genre+"&MovieTitle="+title+"&MovieYear="+year+
        "&Director="+director+"&StarName="+starName+"&genre=&all=0"+"&orderBy=" + orderBy
        +"&offset="+offset.toString() +"&limit="+limit.toString()+"&isASC="+isASC+"&startsWith="+startsWith+ "&is_android="+is_android;
    return parms;
}
function requestContent(parms)
{
    // Makes the HTTP GET request and registers on success callback function handleResult
    jQuery.ajax({
        dataType: "json",  // Setting return data type
        method: "GET",// Setting request method
        url: "api/list"+parms, // Setting request url, which is mapped by StarsServlet in Stars.java
        success: (resultData) => handleResult(resultData) // Setting callback function to handle data returned successfully by the SingleStarServlet
});
}
/**
 * Once this .js is loaded, following scripts will be executed by the browser\
 */


getParms();
requestContent(generateParms());


