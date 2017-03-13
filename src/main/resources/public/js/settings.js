var token = $("meta[name='_csrf']").attr("content");
var header = $("meta[name='_csrf_header']").attr("content");

$.datepicker.setDefaults($.datepicker.regional['et']);

$(document).ready(function () {
    $("#start_date").datepicker({dateFormat: "dd-mm-yy"});
    $("#end_date").datepicker({dateFormat: "dd-mm-yy"});

    var current_start;
    var current_end;
    var current_row_id;
    var donationData;
    var duckData;
    var subtitle;
    var current_description;

    // Remove spinner
    $(document).ajaxStop(function () {
        $(".loadingDiv").remove();
        $(".btn").removeAttr("disabled");
    });

    $('.race').click(function () {
        current_row_id = $(this).data('selector');
        current_start = $(this).data('start');
        current_end = $(this).data('end');
        current_description = $(this).data('desc');

        var chart_row = $('#info' + current_row_id);
        var loader_row = $('#info2' + current_row_id);
        var description_row = $('#info3' + current_row_id);

        $(".chart_").remove();
        chart_row.append('<br>');
        chart_row.append('<div id="linechart_donations" class="chart_ container-fluid"></div>');
        chart_row.append('<div id="linechart_ducks" class="chart_ container-fluid"></div>');
        loader_row.append('<div class="loader loadingDiv container-fluid"></div>');
        description_row.append('<div class="chart_">Täpsustus: ' + current_description + '</div>');
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
                subtitle = data.subtitle;
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
                title: "Müüdud pardid",
                subtitle: subtitle
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
                title: "Annetused",
                subtitle: subtitle
            },
            width: 900,
            height: 500,
            vAxis: {viewWindowMode: "explicit", viewWindow:{ min: 0 }}
        };

        var chart = new google.charts.Line(document.getElementById("linechart_donations"));
        chart.draw(data, google.charts.Line.convertOptions(options));
    }
});