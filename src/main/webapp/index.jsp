<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
    "http://www.w3.org/TR/html4/loose.dtd">

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <!--[if IE]><script language="javascript" type="text/javascript" src="js/flot/excanvas.min.js"></script><![endif]-->

        <script type="text/javascript" src="js/jquery.js"></script>
        <script language="javascript" type="text/javascript" src="js/flot/jquery.flot.js"></script>
        <script language="javascript" type="text/javascript" src="js/flot/jquery.flot.crosshair.js"></script>


        <title>ErkkilaDotOrg Sensor Data</title>
    </head>

    <body>
        <h2>All times are in UTC+0</h2>


        <p>Windspeed in meters/second</p>
        <div id="placeholder" style="width:800px;height:200px"></div>
        <script id="source" language="javascript" type="text/javascript" src="js/WindSpeed.js"></script>
        
        <p> House Power Use in watts </p>
        <div id="powerplot" style="width:800px;height:200px"></div>
        <script id="source" language="javascript" type="text/javascript" src="js/PowerWatts.js"></script>

        <p> Barometric Pressure (Pa) </p>
        <div id="pressure0" style="width:800px;height:200px"></div>
        <script id="source" language="javascript" type="text/javascript" src="js/Pressure0.js"></script>

        <p> Temp0 10_F73ECB010800_temperature</p>
        <div id="temp0" style="width:800px;height:200px"></div>
        <script id="source" language="javascript" type="text/javascript" src="js/Temp0.js"></script>

        <p> Temp1 26_0148E7000000</p>
        <div id="temp1" style="width:800px;height:200px"></div>
        <script id="source" language="javascript" type="text/javascript" src="js/Temp1.js"></script>

        <p> Temp2 90A2DA0021AC </p>
        <div id="temp2" style="width:800px;height:200px"></div>
        <script id="source" language="javascript" type="text/javascript" src="js/Temp2.js"></script>

        <p>Relative Humidity (note sensor might be f*cked)</p>
        <div id="humidity" style="width:800px;height:200px"></div>
        <script id="source" language="javascript" type="text/javascript" src="js/Humidity.js"></script>


    </body>

</html>
