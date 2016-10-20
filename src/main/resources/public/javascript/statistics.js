/* Estonian initialisation for the jQuery UI date picker plugin. */
/* Written by Mart Sõmermaa (mrts.pydev at gmail com). */
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

$.datepicker.setDefaults($.datepicker.regional['et']);


google.charts.load('current', {'packages': ['line']});
var dataFromServer;
var start = "";
var end = "";
callDrawChart(start, end);

//google.charts.setOnLoadCallback(drawChart_visits);

//Send POST request to server
function getData(start, end) {
    var jsonData = $.ajax({
        url: window.location.href,
        dataType: "json",
        type: 'POST',
        cache: false,
        async: false,
        data: {"start": start, "end": end}
    }).responseText;
    return JSON.parse(jsonData).data;
}

function callDrawChart(start, end) {
    dataFromServer = getData(start, end);
    google.charts.setOnLoadCallback(drawChart_donations);
}


function drawChart_donations() {
    var data = new google.visualization.DataTable();

    //TODO: alati ei ole päev.
    data.addColumn('number', 'Päev');
    data.addColumn('number', 'Müüdud parte');
    data.addColumn('number', 'Kogutud raha');
    data.addRows(dataFromServer);

    var options = {
        chart: {
            title: "Annetused"
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

function drawChart_visits() {
    var data = new google.visualization.DataTable();
    data.addColumn('number', 'Päev');
    data.addColumn('number', 'Külastatavus');
    data.addRows([
        [1, 20],
        [2, 30],
        [3, 40],
        [4, 50]
    ]);

    var options = {
        chart: {
            title: "Veebipoe külastatavus"
        },
        width: 900,
        height: 500
    };

    var chart = new google.charts.Line(document.getElementById("linechart_visits"));
    chart.draw(data, google.charts.Line.convertOptions(options));
}


$(document).ready(function () {
    $("#datepicker_don_start").datepicker(
        {
            dateFormat: 'dd.mm.yy',
            onSelect: function () {
                start = $(this).datepicker({dateFormat: 'dd.mm.yy'}).val();
                callDrawChart(start, end)
            }
        }
    );
});
$(document).ready(function () {
    $("#datepicker_don_end").datepicker(
        {
            dateFormat: 'dd.mm.yy',
            onSelect: function () {
                end = $(this).datepicker({dateFormat: 'dd.mm.yy'}).val();
                callDrawChart(start, end)
            }
        }
    );
});
$(document).ready(function () {
    $("#datepicker_vis_start").datepicker();
});
$(document).ready(function () {
    $("#datepicker_vis_end").datepicker();
});
