var token = $("meta[name='_csrf']").attr("content");
var header = $("meta[name='_csrf_header']").attr("content");

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
    firstDay: 1,
    dateFormat: "dd-mm-yy",
    isRTL: false,
    showMonthAfterYear: false,
    yearSuffix: ""
};

$.datepicker.setDefaults($.datepicker.regional['et']);

$(document).ready(function () {
    $("#start_date").datepicker();
    $("#end_date").datepicker();

    var current_start;
    var current_end;
    var current_row_id;
    var donationData;
    var duckData;
    var current_descrition;

    // Remove spinner
    $(document).ajaxStop(function () {
        $(".loadingDiv").remove();
        $(".btn").removeAttr("disabled");
    });

    $('.race').click(function () {
        current_row_id = $(this).data('selector');
        current_start = $(this).data('start');
        current_end = $(this).data('end');
        current_descrition = $(this).data('desc');

        var chart_row = $('#info' + current_row_id);
        var loader_row = $('#info2' + current_row_id);
        var description_row = $('#info3' + current_row_id);

        $(".chart_").remove();
        chart_row.append('<br>');
        chart_row.append('<div id="linechart_donations" class="chart_ container-fluid"></div>');
        chart_row.append('<div id="linechart_ducks" class="chart_ container-fluid"></div>');
        loader_row.append('<div class="loader loadingDiv container-fluid"></div>');
        description_row.append('<div class="chart_">Täpsustus: ' + current_descrition + '</div>');
        callDrawCharts();
    });

    google.charts.load('current', {'packages': ['line']});

    //Send POST request to server
    function callDrawCharts() {
        $(".btn").attr("disabled", "disabled");
        $.ajax({
            url: window.location.href + 'chart',
            type: 'POST',
            cache: false,
            data: {beginning: current_start, finish: current_end},
            beforeSend: function (xhr) {
                xhr.setRequestHeader(header, token);
            },
            success: function (data) {
                donationData = data.donations;
                duckData = data.ducks;
                google.charts.setOnLoadCallback(drawChart_donations);
                google.charts.setOnLoadCallback(drawChart_ducks);
            }
        });
    }

    function drawChart_ducks(){
        var data = new google.visualization.DataTable();
        data.addColumn('string', 'Päev');
        data.addColumn('number', 'Müüdud parte');
        data.addRows(duckData);

        var options = {
            chart:  {
                title: "Müüdud pardid"
            },
            colors: ["#FF9900"],
            width: 900,
            height: 500,
            vAxis: {viewWindowMode: "explicit", viewWindow:{ min: 0 }}
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
                title: "Annetused"
            },
            width: 900,
            height: 500,
            vAxis: {viewWindowMode: "explicit", viewWindow:{ min: 0 }}
        };

        var chart = new google.charts.Line(document.getElementById("linechart_donations"));
        chart.draw(data, google.charts.Line.convertOptions(options));
    }
});