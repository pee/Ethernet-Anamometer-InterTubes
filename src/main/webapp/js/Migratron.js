/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
// initiate a recurring data update
$(function () {

    var options =  {


        colors: ["#edc240", "#cb4b4b","#afd8f8", "#4da74d", "#9440ed"],
        lines: {
            show : true,
            fill: false
        },
        bars: {
            show: true,
            align: "center",
            //barWidth: 100,
            fill: false
        },
        xaxis: {
            mode: "time",
            timeformat: "%y/%m/%d",
            minTickSize: [2, "day"],
            tickSize: [3, "day"],
            panRange: [-10, 10]
        },
        yaxes: [
        {
            min:95000,
            max: 105000,
            position: 'right'
        },

        {
            position: 'right'
        }//,
        //{
        //    position: 'right'
        //}
        ],
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
    //        zoom: {
    //            interactive: true
    //        },
    //        pan: {
    //            interactive: true
    //        }


    };

    console.log(options);

    var plotDiv = $("#migratron");

    var migraines = [
    [1329426000000, 0],
    [1329163200000, 0],
    [1328817600000, 0],
    [1328497320000, 0],
    [1328209200000, 0],
    [1327968000000, 0],
    [1327633200000, 0],
    [1327350600000, 0],
    [1327096800000, 0],
    [1326924000000, 0],
    [1326869100000, 0],
    [1326200100000, 0],
    [1326146400000, 0],
    [1326042000000, 0],
    [1325017980000, 0],
    [1324850400000, 0],
    [1324593000000, 0],
    [1324072800000, 0],
    [1323981000000, 0],
    [1323117000000, 0],
    [1323028800000, 0],
    [1322971200000, 0],
    [1322784000000, 0],
    [1321647900000, 0],
    [1321539600000, 0],
    [1321488000000, 0],
    [1321401600000, 0],
    [1320784200000, 0],
    [1320188400000, 0],
    [1319905800000, 0],
    [1319770800000, 0],
    [1319483220000, 0],
    [1319411640000, 0],
    [1318698180000, 0],
    [1318545000000, 0],
    [1318323600000, 0],
    [1317664500000, 0],
    [1317147540000, 0],
    [1316728140000, 0],
    [1316636880000, 0],
    [1316304000000, 0],
    [1315256700000, 0],
    [1314885600000, 0],
    [1314565020000, 0],
    [1313605800000, 0],
    [1313521200000, 0],
    [1313178900000, 0],
    [1313089200000, 0],
    [1312979400000, 0],
    [1312834380000, 0],
    [1311721200000, 0],
    [1311633000000, 0],
    [1311008760000, 0],
    [1309975200000, 0],
    [1309896000000, 0],
    [1309808040000, 0],
    [1309377000000, 0],
    [1309255440000, 0],
    [1308603960000, 0],
    [1307729400000, 0],
    [1307272800000, 0],
    [1307244360000, 0],
    [1307244300000, 0],
    [1306956780000, 0],
    [1306357980000, 0],
    [1306252800000, 0],
    [1305317520000, 0],
    [1304551380000, 0],
    [1304459940000, 0],
    [1304100000000, 0],
    [1303947000000, 0],
    [1303770600000, 0],
    [1303323900000, 0],
    [1303149600000, 0],
    [1302906600000, 0],
    [1301274000000, 0],
    [1301140800000, 0],
    [1300222800000, 0],
    [1300125600000, 0],
    [1298301300000, 0],
    [1297210140000, 0],
    [1297123680000, 0],
    [1294947840000, 0],
    [1294359000000, 0],
    [1294096140000, 0],
    [1293734760000, 0],
    [1293670800000, 0],
    [1293561000000, 0],
    [1292007120000, 0],
    [1291247820000, 0],
    [1290193500000, 0],
    [1288808040000, 0],
    [1288555980000, 0],
    [1286911800000, 0],
    [1286848200000, 0],
    [1286405940000, 0],
    [1285823100000, 0],
    [1285614540000, 0],
    [1285423800000, 0],
    [1285360440000, 0],
    [1285264620000, 0],
    [1284680700000, 0],
    [1282665180000, 0],
    [1282600320000, 0],
    [1282520700000, 0],
    [1281909840000, 0],
    [1281826920000, 0],
    [1280948160000, 0],
    [1279842900000, 0],
    [1279459800000, 0],
    [1279327140000, 0],
    [1278991020000, 0],
    [1278376200000, 0],
    [1278254220000, 0],
    [1278164280000, 0],
    [1278111600000, 0],
    [1277318940000, 0],
    [1273593600000, 0],
    [1273278600000, 0],
    [1272726120000, 0],
    [1272636000000, 0],
    [1272546000000, 0],
    [1272459300000, 0],
    [1270831800000, 0],
    [1270678020000, 0],
    [1270207200000, 0],
    [1270122000000, 0],
    [1269977940000, 0],
    [1268869680000, 0],
    [1268353920000, 0],
    [1268051760000, 0],
    ];


    // reset data
    var pressuredata = [];
    var pressure2Plot = [];
    var temp0Data = [];
    var windData = [];

    plot = $.plot(plotDiv, pressuredata, options);
  
    console.log( plot.getOptions() );

    drawPlot();

    function drawPlot() {

        $.plot( plotDiv , [
        {
            data: pressure2Plot,
            label: "pressure (Pa)",
            lines: {
                show: true,
                fill: true
            },
            bars: {
                show: false
            },
            xaxis: 1,
            yaxis: 1

        }, {
            data: temp0Data,
            label: "temp (f)",
            lines: {
                show: true,
                fill: false
            },
            bars: {
                show: false
            },
            xaxis: 1,
            yaxis: 2
        },
        //        },{
        //            data: windData,
        //            label: "speed (m/s)",
        //            lines: {
        //                show: true,
        //                fill: false
        //            },
        //            bars: {
        //                show: false
        //            },
        //            xaxis: 1,
        //            yaxis: 3
        //        },
        {
            data: migraines,
            label: "ouchies",
            bars: {
                show: true,
                align: "center",
                barWidth: 60*60*1000*12,
                fill: true
            },
            xaxis: 1,
            yaxis: 1
        },{
            data: migraines,
            //label: "ouchies",
            bars: {
                show: true,
                align: "center",
                barWidth: 1,
                fill: true
            },
            xaxis: 1,
            yaxis: 1
        } ], options);

    }
    //    function showPowerTooltip(x, y, contents) {
    //
    //        $('<div id="powertooltip">' + contents + '</div>').css( {
    //            position: 'absolute',
    //            display: 'none',
    //            top: y + 10,
    //            left: x + 10,
    //            border: '1px solid #fdd',
    //            padding: '2px',
    //            'background-color': '#fee',
    //            opacity: 0.80
    //        }).appendTo("body").fadeIn(200);
    //    }

    var powerpreviousPoint = null;

    //    $("#migratron").bind("plothover", function (event, pos, item) {
    //        $("#x").text(pos.x);
    //        $("#y").text(pos.y);
    //
    //        if (item) {
    //            if (powerpreviousPoint != item.datapoint) {
    //                powerpreviousPoint = item.datapoint;
    //
    //                $("#powertooltip").remove();
    //                var x = item.datapoint[0],
    //                y = item.datapoint[1];
    //
    //                showPowerTooltip(item.pageX, item.pageY, y + " Pa");
    //            }
    //        }
    //        else {
    //            $("#powertooltip").remove();
    //            powerpreviousPoint = null;
    //        }
    //
    //    });

    function fetchData() {

        function onPressureReceived(series) {

            // we get all the data in one go, if we only got partial
            // data, we could merge it with what we already got
            pressuredata = [ series ];
            pressure2Plot = series;

            var fpoint = pressuredata[0][0]
            
            var min = fpoint[1];
            var max = fpoint[1];

            for ( point in pressuredata[0] ) {

                var thisy = pressuredata[0][point][1];

                if ( thisy < min) {
                    min = thisy;
                }

                if ( thisy > max ) {
                    max = thisy ;
                }
            }

            // set the Y for our migraines to new max+25
            for ( mg in migraines ) {
                var mig = migraines[mg];
                //console.log(mig);
                mig[1] = max + 25;
            }

            console.log(options);
            options['yaxes'][0]['min'] = min - 25;
            options['yaxes'][0]['max'] = max + 25;
            console.log(options);

            drawPlot();

        }

        function onTempReceived(series) {

            temp0Data = series;

            drawPlot();

        }

        function onWindSpeedReceived(series) {
            windData = series;
            drawPlot();
        }

        $.ajax({
            url: "pressureAll",
            method: 'GET',
            dataType: 'json',
            success: onPressureReceived
        });

        $.ajax({
            url: "temp0?startDate=2010-03-01&stopDate=now",
            method: 'GET',
            dataType: 'json',
            success: onTempReceived
        });

        //        $.ajax({
        //            url: "windSpeed?startDate=2010-03-01&stopDate=now&reductionFactor=20",
        //            method: 'GET',
        //            dataType: 'json',
        //            success: onWindSpeedReceived
        //        });

        setTimeout(fetchData, 300000 );

    }

    setTimeout( fetchData, 10 );

 

});

