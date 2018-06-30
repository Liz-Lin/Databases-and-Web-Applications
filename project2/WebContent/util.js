/**
 * Retrieve parameter from request URL, matching by parameter name
 * @param target String
 * @returns {*}
 */
function getParameterByName(target) {
    // Get request URL
    var url = window.location.href;
    // Encode target parameter name to url encoding
    target = target.replace(/[\[\]]/g, "\\$&");

    // Ues regular expression to find matched parameter value
    var regex = new RegExp("[?&]" + target + "(=([^&#]*)|&|#|$)"),
        results = regex.exec(url);
    if (!results) return '';
    if (!results[2]) return '';

    // Return the decoded parameter value
    return decodeURIComponent(results[2].replace(/\+/g, " "));
}

function handleSignoutResult(resultData){

    if (resultData["status"] === "success") {
        window.location.replace("login.html");
    }
}

function onClickSignout(){
    $.ajax({
        dataType: "json",  // Setting return data type
        method: "GET",// Setting request method
        url: "/api/signout",
        success: (resultData) => handleSignoutResult(resultData)
    });
}
