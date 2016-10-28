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