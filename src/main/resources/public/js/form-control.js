
$(document).ready(function() {
    var prices = document.getElementsByClassName("prc");
    var qties = document.getElementsByClassName("qty");

    for (var i = 0; i < prices.length; i++) {
        prices.item(i).addEventListener("change", updateTotal);
        qties.item(i).addEventListener("change", updateTotal);
    }

    updateTotal();
});

function updateTotal() {
    console.log("Updating total sum");
    var sum = 0;
    var prices = document.getElementsByClassName("prc");
    var qties = document.getElementsByClassName("qty");
    for (var i = 0; i < prices.length; i++) {
        sum += prices.item(i).value * qties.item(i).value;
    }
    document.getElementsByClassName("total-sum-amount").item(0).textContent = sum.toString() + ".00";
}
