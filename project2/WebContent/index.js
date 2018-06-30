
function handleResult(resultData) {

    var genreListElement = jQuery("#genre-group");
    var count = Math.ceil(resultData.length/3) ;
    for (var i = 0; i < 3; i++ )
    {   var colHTML = "";
        colHTML+='<div class="col"><ul class="list-group">';
        for (var j = 0; j < count && i*count + j < resultData.length; j++)
        {
            colHTML+='<li class="list-group-item">'+
                '<a href=list.html?genre='+resultData[i*count + j ]['genre_id'].toString() +'&orderBy=title&offset=0&limit=20&isASC=1 >'+
                 resultData[i*count + j]['genre_name']+' </li>';
        }
        colHTML+='</ul></div>';
        genreListElement.append(colHTML);
    }

}








// Makes the HTTP GET request and registers on success callback function handleResult
jQuery.ajax({
    dataType: "json",  // Setting return data type
    method: "GET",// Setting request method
    url: "api/genres", // Setting request url, which is mapped by StarsServlet in Stars.java
    success: (resultData) => handleResult(resultData) // Setting callback function to handle data returned successfully by the SingleStarServlet
});