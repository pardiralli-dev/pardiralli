/* Estonian initialisation for the jQuery UI date picker plugin. Written by Mart Sõmermaa (mrts.pydev at gmail com). */
$.datepicker.regional['et'] = {
    closeText: "Sulge",
    prevText: "Eelnev",
    nextText: "Järgnev",
    currentText: "Täna",
    monthNames: ["Jaanuar", "Veebruar", "Märts", "Aprill", "Mai", "Juuni",
        "Juuli", "August", "September", "Oktoober", "November", "Detsember"],
    monthNamesShort: ["Jaan", "Veebr", "Märts", "Apr", "Mai", "Juuni",
        "Juuli", "Aug", "Sept", "Okt", "Nov", "Dets"],
    dayNames: [
        "Pühapäev",
        "Esmaspäev",
        "Teisipäev",
        "Kolmapäev",
        "Neljapäev",
        "Reede",
        "Laupäev"
    ],
    dayNamesShort: ["Pühap", "Esmasp", "Teisip", "Kolmap", "Neljap", "Reede", "Laup"],
    dayNamesMin: ["P", "E", "T", "K", "N", "R", "L"],
    weekHeader: "näd",
    dateFormat: "dd.mm.yy",
    firstDay: 1,
    isRTL: false,
    showMonthAfterYear: false,
    yearSuffix: ""
};

var token = $("meta[name='_csrf']").attr("content");
var header = $("meta[name='_csrf_header']").attr("content");

$.datepicker.setDefaults($.datepicker.regional['et']);

function parseDate(input) {
    var parts = input.split('-');
    return new Date(parts[2], parts[1] - 1, parts[0]); // Note: months are 0-based
}
$(document).ready(function () {
        google.charts.load('current', {'packages': ['line']});
        var donationData;
        var duckData;
        var subtitle;
        var errorMessage;
        var errorMsgDiv;
        var csvStartDatePicker = $('#datepicker_exp_start');
        var csvEndDatePicker = $('#datepicker_exp_end');
        var csvStartDate = parseDate(csvStartDatePicker.val());
        var csvEndDate = parseDate(csvEndDatePicker.val());

        callDrawCharts();

        var gifDiv = $("#loadingGif");

        $(document).ajaxStop(function () {
            $(".loadingDiv").remove();
        });

        function callDrawCharts() {
            $.ajax({
                url: window.location.href,
                type: 'POST',
                cache: false,
                data: $("#don_chart").serialize(),
                beforeSend: function (xhr) {
                    xhr.setRequestHeader(header, token);
                    $("#msg").remove();
                },
                success: function (data) {
                    $("#msg").remove();
                    errorMsgDiv = $("#errorMessage");

                    donationData = data.donations;
                    duckData = data.ducks;
                    subtitle = data.subtitle;
                    errorMessage = data.errorMessage;

                    google.charts.setOnLoadCallback(drawChart_donations);
                    google.charts.setOnLoadCallback(drawChart_ducks);

                    if (errorMessage !== null) {
                        errorMsgDiv.append('<div id="msg" class="alert alert-danger">' + errorMessage + '</div>');
                    }
                }
            });
        }

        function drawChart_ducks() {
            var data = new google.visualization.DataTable();
            data.addColumn('string', 'Päev');
            data.addColumn('number', 'Müüdud parte');
            data.addRows(duckData);

            var options = {
                chart: {
                    title: "Müüdud pardid",
                    subtitle: subtitle
                },
                colors: ["#FF9900"],
                width: 900,
                height: 500,
                vAxis: {viewWindowMode: "explicit", viewWindow: {min: 0}}
            };
            var chart = new google.charts.Line(document.getElementById("linechart_ducks"));
            chart.draw(data, google.charts.Line.convertOptions(options));
        }

        function drawChart_donations() {
            var data = new google.visualization.DataTable();

            data.addColumn('string', 'Päev');
            data.addColumn('number', 'Kogutud raha');
            data.addRows(donationData);

            var options = {
                chart: {
                    title: "Annetused",
                    subtitle: subtitle
                },
                width: 900,
                height: 500,
                vAxis: {viewWindowMode: "explicit", viewWindow: {min: 0}}
            };

            var chart = new google.charts.Line(document.getElementById("linechart_donations"));
            chart.draw(data, google.charts.Line.convertOptions(options));
        }

        function checkCSVDates(startDate, endDate) {
            if (startDate > endDate) {
                $("#submit").attr("disabled", "disabled");
            }
            else {
                $("#submit").removeAttr("disabled");
            }
        }

        $("#datepicker_don_start").datepicker(
            {
                dateFormat: "dd-mm-yy",
                onSelect: function () {
                    gifDiv.append('<div class="loader loadingDiv container-fluid"></div>');
                    callDrawCharts()
                }
            }
        );

        $("#datepicker_don_end").datepicker(
            {
                dateFormat: "dd-mm-yy",
                onSelect: function () {
                    gifDiv.append('<div class="loader loadingDiv container-fluid"></div>');
                    callDrawCharts()
                }
            }
        );

        csvStartDatePicker.datepicker(
            {
                dateFormat: "dd-mm-yy",
                onSelect: function () {
                    csvStartDate = csvStartDatePicker.datepicker("getDate");
                    checkCSVDates(csvStartDate, csvEndDate)
                }
            }
        );

        csvEndDatePicker.datepicker(
            {
                dateFormat: "dd-mm-yy",
                onSelect: function () {
                    csvEndDate = csvEndDatePicker.datepicker("getDate");
                    checkCSVDates(csvStartDate, csvEndDate)
                }
            }
        );


    }
);