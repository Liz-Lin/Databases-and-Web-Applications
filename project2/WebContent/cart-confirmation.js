
function handleResult(resultData) {

    var cartInfoElement = jQuery("#cart-body");
    var hasData = false;
    for (var itemId in resultData)
    {
        if (resultData.hasOwnProperty(itemId)){
            hasData=true;
            var rowHTML='<tr>';
            rowHTML+= '<td>'+ itemId +'</td>';
            rowHTML+= '<td>'+ resultData[itemId]['item-name'] +'</td>';
            rowHTML+= '<td>'+ resultData[itemId]['count'] +'</td>';
            rowHTML+='</tr>';
            cartInfoElement.append(rowHTML);
        }
    }
    if(!hasData){// cannot access this page
        window.location.replace("shopping-cart.html");
    }
}


jQuery.ajax({
    dataType: "json",  // Setting return data type
    method: "GET",// Setting request method
    url: "/api/update-item", // Setting request url, which is mapped by StarsServlet in Stars.java
    success: (resultData) => handleResult(resultData) // Setting callback function to handle data returned successfully by the SingleStarServlet
});