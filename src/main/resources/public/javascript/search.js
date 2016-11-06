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

var lastQuery = null;
var url = window.location.href + "_async"; // the script where you handle the form input.
var table;
$(document).ready(function () {
        table = $("#results");
        table.tablesorter();

        addFormListener($("#exact"));
        addFormListener($("#general"));

        window.onscroll = function (ev) {
            if ((window.innerHeight + window.scrollY) >= document.body.offsetHeight && lastQuery != null) {
                queryNewRows(lastQuery + "&new=false");
            }
        };

        $("#search_quick_date").datepicker(
            {
                dateFormat: "dd-mm-yy"
            }
        );

        $("#search_date_long").datepicker(
            {
                dateFormat: "dd-mm-yy",
            }
        );

    }
);
/**
 * Override default posting of form and use AJAX instead
 */
function addFormListener(form) {
    form.submit(function (e) {
        table.find("td").remove();
        lastQuery = form.serialize();
        queryNewRows(lastQuery);
        e.preventDefault(); // avoid to execute the actual submit of the form.
    });
}

function queryNewRows(query) {
    $.ajax({
        type: "POST",
        url: url,
        data: query, // serializes the form's elements.
        success: function (data) {
            table.find('tbody').append(data);
            table.trigger("update");
        }
    });
}

