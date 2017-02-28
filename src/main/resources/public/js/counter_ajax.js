function queryDonationCounter() {
    $.ajax({
        url: window.location.protocol + '//' + window.location.host + "/counter_ajax",
        type: 'GET',
        cache: false,
        success: function (data) {
            updateCounters(data.donationSum, data.duckCount);
        },
        complete: function () {
            setTimeout(queryDonationCounter, 5000);
        }
    });
}

function updateCounters(donationSum, duckCount) {
    document.getElementById("counter_donation_sum").textContent = donationSum;
    document.getElementById("counter_duck_count").textContent = duckCount;
}

$(document).ready(function () {
        queryDonationCounter();
    }
);