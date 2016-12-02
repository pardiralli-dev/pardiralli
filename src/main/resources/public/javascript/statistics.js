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

$(document).ready(function () {
        google.charts.load('current', {'packages': ['line']});
        var dataFromServer;
        callDrawDonationChart();

//google.charts.setOnLoadCallback(drawChart_visits);
        var gifDiv = $("#loadingGif");


        $(document).ajaxStop(function () {
            $(".loadingDiv").remove();
        });

//Send POST request to server
        function callDrawDonationChart() {
            $.ajax({
                url: window.location.href,
                type: 'POST',
                cache: false,
                data: $("#don_chart").serialize(),
                beforeSend: function (xhr) {
                    xhr.setRequestHeader(header, token);
                },
                success: function (data) {
                    dataFromServer = data.data;//JSON.parse(data.responseText).data;
                    google.charts.setOnLoadCallback(drawChart_donations);
                }
            });
        }

        function drawChart_donations() {
            var data = new google.visualization.DataTable();

            data.addColumn('string', 'Päev');
            data.addColumn('number', 'Müüdud parte');
            data.addColumn('number', 'Kogutud raha');
            data.addRows(dataFromServer);

            var options = {
                chart: {
                    title: "Annetused ja müüdud pardid"
                },
                width: 900,
                height: 500,
                series: {
                    0: {axis: "Parte"},
                    1: {axis: "Annetusi"}
                },
                axes: {
                    y: {
                        Parte: {label: "Müüdud parte"},
                        Annetusi: {label: "Kogutud raha (€)"}
                    }
                }
            };

            var chart = new google.charts.Line(document.getElementById("linechart_donations"));
            chart.draw(data, google.charts.Line.convertOptions(options));
        }

        $(document).ready(function () {
            $("#datepicker_don_start").datepicker(
                {
                    dateFormat: "dd-mm-yy",
                    onSelect: function () {
                        gifDiv.append('<div class="loader loadingDiv container-fluid"></div>');
                        callDrawDonationChart()
                    }
                }
            );
        });
        $(document).ready(function () {
            $("#datepicker_don_end").datepicker(
                {
                    dateFormat: "dd-mm-yy",
                    onSelect: function () {
                        gifDiv.append('<div class="loader loadingDiv container-fluid"></div>');
                        callDrawDonationChart()
                    }
                }
            );
        });

        $(document).ready(function () {
            $("#datepicker_exp_start").datepicker(
                {
                    dateFormat: "dd-mm-yy"
                }
            );
        });

        $(document).ready(function () {
            $("#datepicker_exp_end").datepicker(
                {
                    dateFormat: "dd-mm-yy"
                }
            );
        });

    }
);