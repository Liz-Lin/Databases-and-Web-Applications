function handleLoginResult(resultData) {
    // If login success, redirect to index.html page
    if (resultData["status"] === "success") {
        window.location.replace("index.html");
        //window.location.replace("addstar.html");
    }
    // If login fail, display error message on <div> with id "login_error_message"
    else {
        jQuery("#login_error_message").text(resultData["message"]);
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
        "/api/_dashboard/login",
        // Serialize the login form to the data sent by POST request
        jQuery("#login_form").serialize(),
        (resultData) => handleLoginResult(resultData));

}

// Bind the submit action of the form to a handler function
jQuery("#login_form").submit((event) => submitLoginForm(event));

