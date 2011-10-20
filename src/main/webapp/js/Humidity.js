/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
// initiate a recurring data update
$(function () {

    var plotOptions =  {
        lines: {
            show : true,
            fill: true
        },
        xaxis: {
            mode: "time"
        },
        selection: {
            mode: "x"
        },
        grid: {
            hoverable: true,
            clickable: true
        }

    };

    var placeHolder = $("#humidity");

    // reset data
    var data = [];

    var plot = $.plot(placeHolder, data, plotOptions);

    function showTooltip(x, y, contents) {

        $('<div id="tooltip">' + contents + '</div>').css( {
            position: 'absolute',
            display: 'none',
            top: y + 5,
            left: x + 5,
            border: '1px solid #fdd',
            padding: '2px',
            'background-color': '#fee',
            opacity: 0.80
        }).appendTo("body").fadeIn(200);
    }

    var previousPoint = null;

    $("#humidity").bind("plothover", function (event, pos, item) {
        $("#x").text(pos.x.toFixed(2));
        $("#y").text(pos.y.toFixed(2));

        if (item) {
            if (previousPoint != item.datapoint) {
                previousPoint = item.datapoint;

                $("#tooltip").remove();
                var x = item.datapoint[0].toFixed(2),
                y = item.datapoint[1].toFixed(2);

                showTooltip(item.pageX, item.pageY, y + "%RH");
            }
        }
        else {
            $("#tooltip").remove();
            previousPoint = null;
        }

    });

    function fetchHumidityData() {

        function onDataReceived(series) {

            data = [ series ];

            $.plot($("#humidity"), data, plotOptions);

        }

        $.ajax({

            url: "humidity",
            method: 'GET',
            dataType: 'json',
            success: onDataReceived

        });

        setTimeout(fetchHumidityData, 120000);

    }

    setTimeout(fetchHumidityData, 10);

});

