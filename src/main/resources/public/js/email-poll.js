
function queryEmailSucessful() {
    console.log('Checking for successful confirmation email...');

    $.ajax({
        url: window.location.protocol + '//' + $('#root_url').text() + "/rest/conf-email-check",
        type: 'GET',
        data: { tid : $('#tid').text() },
        cache: false,
        success: function (data) {
            console.log('Response: ' + data.emailSent);
            if (data.emailSent === true) {
                end = true;
                $('#success').removeAttr('style');  // remove 'display: none' style
            } else if (data.emailSent === false) {
                end = true;
                $('#fail').removeAttr('style');  // remove 'display: none' style
            }
        },
        complete: function () {
            if (!end) {
                n++;
                const timeout = 1000 + 500 * n;
                console.log('Checking again in ' + timeout.toString() + ' ms.');
                setTimeout(queryEmailSucessful, timeout);
            }
        }
    });
}

var n = 0;
var end = false;
$(document).ready(function () {
        queryEmailSucessful();
    }
);
