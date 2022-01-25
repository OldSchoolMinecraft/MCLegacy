function handleCredentialResponse(response)
{
    console.log("Encoded JWT ID token: " + response.credential);
}

window.onload = function()
{
    google.accounts.id.initialize({
        client_id: "688693554777-im28st0bmi3l6r2hlvur2i5lpk5i4795.apps.googleusercontent.com",
        callback: handleCredentialResponse
    });
    google.accounts.id.renderButton(document.getElementById("buttonDiv"),
    {
        theme: "outline",
        size: "large" 
    });
    google.accounts.id.prompt(); // also display the One Tap dialog
}
