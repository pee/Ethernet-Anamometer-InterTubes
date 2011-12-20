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
        yaxis: {
                min: 95000,
                max: 105000
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

    var powerplaceHolder = $("#pressure0");

    // reset data
    var pressuredata = [];

    var powerplot = $.plot(powerplaceHolder, pressuredata, options);

    function showPowerTooltip(x, y, contents) {

        $('<div id="powertooltip">' + contents + '</div>').css( {
            position: 'absolute',
            display: 'none',
            top: y + 10,
            left: x + 10,
            border: '1px solid #fdd',
            padding: '2px',
            'background-color': '#fee',
            opacity: 0.80
        }).appendTo("body").fadeIn(200);
    }

    var powerpreviousPoint = null;

    $("#pressure0").bind("plothover", function (event, pos, item) {
        $("#x").text(pos.x);
        $("#y").text(pos.y);

        if (item) {
            if (powerpreviousPoint != item.datapoint) {
                powerpreviousPoint = item.datapoint;

                $("#powertooltip").remove();
                var x = item.datapoint[0],
                y = item.datapoint[1];

                showPowerTooltip(item.pageX, item.pageY, y + " Pa");
            }
        }
        else {
            $("#powertooltip").remove();
            powerpreviousPoint = null;
        }

    });

    function fetchPressure0() {

        function onDataReceived(series) {
            // we get all the data in one go, if we only got partial
            // data, we could merge it with what we already got
            //console.log(series);
            pressuredata = [ series ];
            //console.log(pressuredata);

            //console.log(pressuredata[0]);
            //console.log(pressuredata[0][0])
            var fpoint = pressuredata[0][0]
            //console.log(fpoint[1]);
            
            var min = fpoint[1];
            var max = fpoint[1];

            for ( point in pressuredata[0] ) {

                //console.log(pressuredata[0][point]);

                var thisy = pressuredata[0][point][1];
                //console.log(thisy);

                if ( thisy < min) {
                  min = thisy;
                }

                if ( thisy > max ) {
                    max = thisy ;
                }
            }

            options['yaxis']['min'] = min - 25;
            options['yaxis']['max'] = max + 25;

            $.plot($("#pressure0"), pressuredata, options);

        }

        $.ajax({
            // usually, we'll just call the same URL, a script
            // connected to a database, but in this case we only
            // have static example files so we need to modify the
            // URL
            url: "pressure0",
            method: 'GET',
            dataType: 'json',
            success: onDataReceived
        });

        setTimeout(fetchPressure0, 120000);

    }

    setTimeout(fetchPressure0, 10);
});

