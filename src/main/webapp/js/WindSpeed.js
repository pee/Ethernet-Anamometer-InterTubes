//
//
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

    var placeHolder = $("#placeholder");

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

    $("#placeholder").bind("plothover", function (event, pos, item) {
        $("#x").text(pos.x.toFixed(2));
        $("#y").text(pos.y.toFixed(2));

        if (item) {
            if (previousPoint != item.datapoint) {
                previousPoint = item.datapoint;

                $("#tooltip").remove();
                //var x = item.datapoint[0].toFixed(2),
                //y = item.datapoint[1].toFixed(2);
                y = item.datapoint[1];

                var ws = y * 2.23693629;

                var tt = y.toFixed(2) + "m/s = " + ws.toFixed(2) + "mph";

                showTooltip(item.pageX, item.pageY, tt);
            }
        }
        else {
            $("#tooltip").remove();
            previousPoint = null;
        }

    });

    function fetchWindSpeedData() {

        function onDataReceived(series) {

            data = [ series ];

            $.plot($("#placeholder"), data, plotOptions);

        }

        $.ajax({

            url: "windSpeed",
            method: 'GET',
            dataType: 'json',
            success: onDataReceived

        });

        setTimeout(fetchWindSpeedData, 120000);

    }

    setTimeout(fetchWindSpeedData, 10);

});

