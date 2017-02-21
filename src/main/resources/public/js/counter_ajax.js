function queryDonationCounter() {
    $.ajax({
        url: window.location.href + "/counter_ajax",
        type: 'GET',
        cache: false,
        success: function (data) {
            var donationSum = data.donationSum;
            var duckCount = data.duckCount;
            updateCounters(donationSum, duckCount);
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