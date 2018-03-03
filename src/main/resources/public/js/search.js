$.datepicker.setDefaults($.datepicker.regional['et']);
$(document).ready(function () {
        $("#results").DataTable();
        $("#search_quick_date").datepicker({dateFormat: "dd-mm-yy"});
        $("#search_date_long").datepicker({dateFormat: "dd-mm-yy"});
    }
);