function getTotal() {
    var sum = getNrOfDucks() * getPricePerDuck();
    //display the result
    document.getElementById('sum').innerHTML =
        "Summa: " + sum;
}

function getNrOfDucks() {

    var theForm = document.forms["mainform"];
    //Get a reference to the TextBox
    var quantity = theForm.elements["nrOfDucks"];
    var howmany = 0;
    //If the textbox is not blank
    if (quantity.value != "") {
        howmany = parseInt(quantity.value);
    }
    return howmany;
}

function getPricePerDuck() {
    var theForm = document.forms["mainform"];
    //Get a reference to the TextBox
    var quantity = theForm.elements["pricePerDuck"];
    var howmuch = 0;
    //If the textbox is not blank
    if (quantity.value != "") {
        howmuch = parseInt(quantity.value);
    }
    return howmuch;
}

function validateEmails() {
    if (document.mainform.buyersEmail.value != document.mainform.confirmEmail.value && document.mainform.confirmEmail.value != "") {
        document.getElementById('emailError').innerHTML = "Emailid ei ole samad";
        document.getElementById("submit").disabled = true;
        document.getElementById("emailError").className = "error";
    }
    else {
        document.getElementById("submit").disabled = false;
        document.getElementById('emailError').innerHTML = "";
        document.getElementById("emailError").className =
            document.getElementById("emailError").className.replace
            (/(?:^|\s)error(?!\S)/g, '')
    }
}

function validateFirstName() {
    if (document.mainform.firstName.value == "") {
        document.getElementById('firstNameError').innerHTML = "Sisesta nimi";
        document.getElementById("submit").disabled = true;
        document.getElementById("firstNameError").className = "error";
    }
    else {
        document.getElementById("submit").disabled = false;
        document.getElementById('firstNameError').innerHTML = "";
        document.getElementById("firstNameError").className =
            document.getElementById("firstNameError").className.replace
            (/(?:^|\s)error(?!\S)/g, '')
    }

}

function validateLastName() {
    if (document.mainform.lastName.value == "") {
        document.getElementById('lastNameError').innerHTML = "Sisesta nimi";
        document.getElementById("submit").disabled = true;
        document.getElementById("lastNameError").className = "error";
    }
    else {
        document.getElementById("submit").disabled = false;
        document.getElementById('lastNameError').innerHTML = "";
        document.getElementById("lastNameError").className =
            document.getElementById("lastNameError").className.replace
            (/(?:^|\s)error(?!\S)/g, '')
    }
}

function validatePhoneNumber() {
    if (!isPhoneNumber(document.mainform.phoneNumber.value)) {
        document.getElementById('phoneNumberError').innerHTML = "Sisesta korrektne telefoninumber";
        document.getElementById("submit").disabled = true;
        document.getElementById("phoneNumberError").className = "error";
    }
    else {
        document.getElementById("submit").disabled = false;
        document.getElementById('phoneNumberError').innerHTML = "";
        document.getElementById("phoneNumberError").className =
            document.getElementById("phoneNumberError").className.replace
            (/(?:^|\s)error(?!\S)/g, '')
    }
}

function isPhoneNumber(value) {
    if (value.length > 20 || value.length < 6) {
        return false;
    }
    value.replace(/\s+/g, "");


    if (value.charAt(0) == '+') {
        if (isNumber(value.substring(1, value.length))) {
            return true;
        }
        return false;
    }
    else {
        if (isNumber(value)) {
            return true;
        }
        return false;
    }

    return false;
}

function isNumber(n) {
    return !isNaN(parseFloat(n)) && isFinite(n);
}

function validateNrOfDucks() {
    if (parseInt(document.getElementById('nrOfDucks').value, 10) < 1 || parseInt(document.getElementById('nrOfDucks').value, 10) > 1000) {
        document.getElementById('nrOfDucksError').innerHTML = "Parte peab olema vähemalt 1 ja vähem kui 1000";
        document.getElementById("submit").disabled = true;
        document.getElementById("nrOfDucksError").className = "error";
    }
    else {
        document.getElementById("submit").disabled = false;
        document.getElementById('nrOfDucksError').innerHTML = "";
        document.getElementById("nrOfDucksError").className =
            document.getElementById("nrOfDucksError").className.replace
            (/(?:^|\s)error(?!\S)/g, '')
    }
}

function validatePricePerDuck() {
    if (parseInt(document.getElementById('pricePerDuck').value, 10) < 5 || (parseInt(document.getElementById('pricePerDuck').value, 10) % 5) != 0) {
        document.getElementById('pricePerDuckError').innerHTML = "Ühe pardi eest peab maksma kas 5, 10, 15 jne eurot";
        document.getElementById("submit").disabled = true;
        document.getElementById("pricePerDuckError").className = "error";
    }
    else {
        document.getElementById("submit").disabled = false;
        document.getElementById('pricePerDuckError').innerHTML = "";
        document.getElementById("pricePerDuckError").className =
            document.getElementById("pricePerDuckError").className.replace
            (/(?:^|\s)error(?!\S)/g, '')
    }
}

function validateHasAgreed() {
    if (!document.getElementById('hasAgreed').checked) {
        document.getElementById('hasAgreedError').innerHTML = "Pardirallil ei saa osaleda, kui tingimustega ei nõustuta";
        document.getElementById("submit").disabled = true;
        document.getElementById("hasAgreedError").className = "error";
    }
    else {
        document.getElementById("submit").disabled = false;
        document.getElementById('hasAgreedError').innerHTML = "";
        document.getElementById("hasAgreedError").className =
            document.getElementById("hasAgreedError").className.replace
            (/(?:^|\s)error(?!\S)/g, '')
    }
}


