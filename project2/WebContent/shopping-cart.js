// function getParameterByName(target) {
//     // Get request URL
//     let url = window.location.href;
//     // Encode target parameter name to url encoding
//     target = target.replace(/[\[\]]/g, "\\$&");
//
//     // Ues regular expression to find matched parameter value
//     let regex = new RegExp("[?&]" + target + "(=([^&#]*)|&|#|$)"),
//         results = regex.exec(url);
//     if (!results) return null;
//     if (!results[2]) return '';
//
//     // Return the decoded parameter value
//     return decodeURIComponent(results[2].replace(/\+/g, " "));
// }

function onClickUpdate(element, id, name){
    var curCount = $(element).parent().parent().find('input[type=number]').val();
    window.location.replace('shopping-cart.html?count='+curCount + '&op=update&itemId='+ id +'&itemName='+ name);
}

function handleResult(resultData) {
    var obj = { Title: 'Fablix Shopping Cart', Url: 'shopping-cart.html' };
    history.pushState(obj, obj.Title, obj.Url);
    var cartInfoElement = jQuery("#cart-body");
    var hasItem = false;
    for (var itemId in resultData)
    {
        if (resultData.hasOwnProperty(itemId)){
            hasItem=true;
            var rowHTML='<tr>';
            rowHTML+= '<td>'+ itemId +'</td>';
            rowHTML+= '<td>'+ resultData[itemId]['item-name'] +'</td>';
            rowHTML+= '<td>'+ '<input type="number" min="0" value="'+ resultData[itemId]['count'] +'"></td>';
            rowHTML+= '<td>' +'<button class="btn btn-info" ' +
                'onclick="onClickUpdate(this, \''+itemId +'\', \''+resultData[itemId]['item-name']+
                '\')"><i class="fa fa-refresh" aria-hidden="true"></i></button>';
            rowHTML+='</tr>';
            cartInfoElement.append(rowHTML);
        }
    }


    if(hasItem){
        document.getElementById('checkout').style.display = '';
    }
    else {
        document.getElementById('checkout').style.display = 'none';
    }
}



jQuery.ajax({
    dataType: "json",  // Setting return data type
    method: "GET",// Setting request method
    url: "/api/update-item?count=" + getParameterByName("count")+"&op="+getParameterByName("op")+
    "&itemId=" + getParameterByName("itemId")+"&itemName="+getParameterByName("itemName"), // Setting request url, which is mapped by StarsServlet in Stars.java
    success: (resultData) => handleResult(resultData) // Setting callback function to handle data returned successfully by the SingleStarServlet
});