$.datepicker.setDefaults($.datepicker.regional['et']);

$(document).ready(function () {
    $("#beginning").datepicker({dateFormat: "dd-mm-yy"});
    $("#finish").datepicker({dateFormat: "dd-mm-yy"});
});