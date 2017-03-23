function queryEmailSucessful() {
    console.log('Checking for successful confirmation email...');

    $.ajax({
        url: window.location.protocol + '//' + $('#root_url').text() + "/rest/conf-email-check",
        type: 'GET',
        data: {tid: $('#tid').text()},
        crossDomain: true,
        cache: false,
        success: function (data) {
            if (data.emailSent === true) {
                console.log('Email response: ' + data.emailSent);
                end_email = true;
                $('#success_email').slideDown('fast');
            } else if (data.emailSent === false) {
                console.log('Email response: ' + data.emailSent);
                end_email = true;
                $('#fail_email').slideDown('fast');
            }
        },
        complete: function () {
            if (!end_email) {
                n_email++;
                const timeout = 1000 + 500 * n_email;
                console.log('Checking email again in ' + timeout.toString() + ' ms.');
                setTimeout(queryEmailSucessful, timeout);
            }
        }
    });
}

function querySmsSucessful() {
    console.log('Checking for successful confirmation sms...');

    $.ajax({
        url: window.location.protocol + '//' + $('#root_url').text() + "/rest/conf-sms-check",
        type: 'GET',
        data: {tid: $('#tid').text()},
        crossDomain: true,
        cache: false,
        success: function (data) {
            if (data.smsSent === true) {
                console.log('Sms response: ' + data.smsSent);
                end_sms = true;
                $('#success_sms').slideDown('fast');
            } else if (data.smsSent === false) {
                console.log('Sms response: ' + data.smsSent);
                end_sms = true;
                $('#fail_sms').slideDown('fast');
            }
        },
        complete: function () {
            if (!end_sms) {
                n_sms++;
                const timeout = 1000 + 500 * n_sms;
                console.log('Checking sms again in ' + timeout.toString() + ' ms.');
                setTimeout(querySmsSucessful, timeout);
            }
        }
    });
}

var n_email = 0;
var end_email = false;
var n_sms = 0;
var end_sms = false;
$(document).ready(function () {
        queryEmailSucessful();
        // querySmsSucessful();
    }
);
