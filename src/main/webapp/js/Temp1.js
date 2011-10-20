/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
// initiate a recurring data update
$(function () {

    var options =  {
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
        },
        crosshair: {
            mode: "x"
        }


    };

    var powerplaceHolder = $("#temp1");

    // reset data
    var powerdata = [];

    var powerplot = $.plot(powerplaceHolder, powerdata, options);

    function showPowerTooltip(x, y, contents) {

        $('<div id="powertooltip">' + contents + '</div>').css( {
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

    var powerpreviousPoint = null;

    $("#temp1").bind("plothover", function (event, pos, item) {
        $("#x").text(pos.x.toFixed(2));
        $("#y").text(pos.y.toFixed(2));

        if (item) {
            if (powerpreviousPoint != item.datapoint) {
                powerpreviousPoint = item.datapoint;

                $("#powertooltip").remove();
                var x = item.datapoint[0].toFixed(2),
                y = item.datapoint[1].toFixed(2);

                showPowerTooltip(item.pageX, item.pageY, y + " degrees");
            }
        }
        else {
            $("#powertooltip").remove();
            powerpreviousPoint = null;
        }

    });

    function fetchTemp1() {

        function onDataReceived(series) {
            // we get all the data in one go, if we only got partial
            // data, we could merge it with what we already got
            powerdata = [ series ];

            $.plot($("#temp1"), powerdata, options);

        }

        $.ajax({
            // usually, we'll just call the same URL, a script
            // connected to a database, but in this case we only
            // have static example files so we need to modify the
            // URL
            url: "temp1",
            method: 'GET',
            dataType: 'json',
            success: onDataReceived
        });

        setTimeout(fetchTemp1, 120000);

    }

    setTimeout(fetchTemp1, 10);
});

