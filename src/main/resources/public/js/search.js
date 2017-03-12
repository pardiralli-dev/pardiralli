$.datepicker.setDefaults($.datepicker.regional['et']);
$(document).ready(function () {
        var table = $("#results");
        table.tablesorter();
        $("#search_quick_date").datepicker({dateFormat: "dd-mm-yy"});
        $("#search_date_long").datepicker({dateFormat: "dd-mm-yy"});
    }
);