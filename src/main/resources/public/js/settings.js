$.datepicker.setDefaults($.datepicker.regional['et']);

$(document).ready(function () {
    $("#start_date").datepicker({dateFormat: "dd-mm-yy"});
    $("#end_date").datepicker({dateFormat: "dd-mm-yy"});
});