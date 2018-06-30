var card = new Card({
    form: 'form',
    container: '.card-wrapper',
    formatting:true,
    formSelectors: {
        nameInput: 'input[name="first-name"], input[name="last-name"]'
    }
});

function handleLoginResult(resultDataString) {
    resultDataJson = JSON.parse(resultDataString);
    // // If login success, redirect to index.html page
    if (resultDataJson["status"] === "success") {
        window.location.replace("order-confirmation.html");
    }
    // If login fail, display error message on <div> with id "login_error_message"
    else {
        alert("Transaction failed. "+ resultDataJson["message"])
    }
}


function submitLoginForm(formSubmitEvent) {
    // Important: disable the default action of submitting the form
    //   which will cause the page to refresh
    //   see jQuery reference for details: https://api.jquery.com/submit/
    formSubmitEvent.preventDefault();

    jQuery.post(
        "api/checkout",
        // Serialize the login form to the data sent by POST request
        jQuery("#checkout-form").serialize(),
        (resultDataString) => handleLoginResult(resultDataString));

}


jQuery("#checkout-form").submit((event) => submitLoginForm(event));