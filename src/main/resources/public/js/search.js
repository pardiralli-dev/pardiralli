$(document).ready(function () {
    $("#results").DataTable({
        columnDefs: [
            {'orderData': 0, 'targets': 'td-time-of-payment'} // Sort time of payment by 0-th column (serial number)
        ]
    });
});