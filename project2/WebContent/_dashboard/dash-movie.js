
function handleLoginResult(resultData) {
    // If login success, redirect to index.html page
    var resultMsgElement = jQuery("#result-msg");
    if (resultData["status"] === "success") {
        resultMsgElement.text(resultData["message"]);
    }
    // If login fail, display error message on <div> with id "login_error_message"
    else {
        resultMsgElement.text("Add movie failed. Error: "+ resultData["message"]);
    }
}
/**
 * Submit the form content with POST method
 * @param formSubmitEvent
 */
function submitLoginForm(formSubmitEvent) {

    // Important: disable the default action of submitting the form
    //   which will cause the page to refresh
    //   see jQuery reference for details: https://api.jquery.com/submit/
    formSubmitEvent.preventDefault();

    jQuery.post(
        "/api/_dashboard/add-movie",
        // Serialize the login form to the data sent by POST request
        jQuery("#add-movie-form").serialize(),
        (resultData) => handleLoginResult(resultData));

}

// Bind the submit action of the form to a handler function
jQuery("#add-movie-form").submit((event) => submitLoginForm(event));


