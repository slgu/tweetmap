<!DOCTYPE html>
<html>
<head>
    <style type="text/css">
    </style>
    <script src="http://maps.googleapis.com/maps/api/js?key=AIzaSyBTFest0kbG-xe7_EFh-ZHZviRZ3TeMh1A&libraries=visualization"></script>
    <!-- Latest compiled and minified CSS -->
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/css/bootstrap.min.css">
    <!-- Optional theme -->
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/css/bootstrap-theme.min.css">
    <!-- Latest compiled and minified JavaScript -->
    <script src="http://code.jquery.com/jquery-2.1.4.min.js"></script>
    <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/js/bootstrap.min.js"></script>
    <script>
        //america center
        var center = new google.maps.LatLng(38.572643,-96.5236265);
        var heat_map_data = [];
        var map;
        var heatmap;
        var tweetData;
        var ws;
        var realTimeInfo = [];
        var received_msg;
        //realTimeInfo[idx][0] is context
        //realTimeInfo[idx][1] is string of author place time
        function generate() {
            //clean first
            $("#realtime").html("");
            for (idx in realTimeInfo) {
                var blockquote = $("<blockquote/>");
                $("<p/>").html(realTimeInfo[idx][0]).appendTo(blockquote);
                $("<footer/>").html(realTimeInfo[idx][1]).appendTo(blockquote);
                blockquote.appendTo($("#realtime"));
            }
        }
        function overview() {
            $("#category").show();
            $("#realtime").hide();
            $.get("getdata", function (data,status) {
                heatmap.setMap(null);
                heat_map_data = [];
                tweetData = data;
                for (idx in tweetData) {
                    var item = tweetData[idx];
                    heat_map_data.push({location: new google.maps.LatLng(parseFloat(item.lat), parseFloat(item.lon)), weight: 0.5});
                }
                heatmap = new google.maps.visualization.HeatmapLayer({
                    data: heat_map_data
                });
                heatmap.setMap(map);
            } );
        }
        function wssend() {
            ws.send("get");
        }
        function realtime() {
            $("#category").hide();
            $("#realtime").show();
            generate();
            heatmap.setMap(null);
            ws = new WebSocket("ws://localhost:8080/tweetmap/push");
            window.setInterval(wssend, 3000);
            ws.onopen = function()
            {
                // Web Socket is connected, send data using send()
                wssend();
            };

            ws.onmessage = function (evt)
            {
                received_msg = JSON.parse(evt.data);
                var lat = parseFloat(received_msg.lat);
                var lon = parseFloat(received_msg.lon);
                console.log(received_msg["text"]);
                console.log(received_msg["username"]);
                realTimeInfo.push([received_msg["text"], received_msg["username"]]);
                if (realTimeInfo.length > 4) {
                    realTimeInfo.shift();
                }
                generate();
            };

            ws.onclose = function()
            {
                // websocket is closed.
            };
        }
        function initialize() {
            //init raw map
            map = new google.maps.Map(document.getElementById('googleMap'), {
                center: center,
                zoom: 4
            });
            heatmap = new google.maps.visualization.HeatmapLayer({
                data: heat_map_data
            });
            heatmap.setMap(map);
        }
        google.maps.event.addDomListener(window, 'load', initialize);
    </script>
</head>

<body>
<div class="row">
    <div class="col-md-3" id="view_choice">
        <div class="btn-group" role="group" aria-label="...">
            <button type="button" class="btn btn-default" onclick="overview()">Overview</button>
            <button type="button" class="btn btn-default" onclick="realtime()">Real-Time</button>
        </div>
        <div id="category">
            <div><h3>Choose Category to be shown in Map</h3></div>
            <div class="dropdown">
                <button class="btn btn-default dropdown-toggle" type="button" id="dropdownMenu1" data-toggle="dropdown" aria-haspopup="true" aria-expanded="true">
                    Category
                    <span class="caret"></span>
                </button>
                <ul class="dropdown-menu" aria-labelledby="dropdownMenu1">
                    <li><a href="#">Food</a></li>
                    <li><a href="#">Music</a></li>
                    <li><a href="#">Sport</a></li>
                    <li><a href="#">health</a></li>
                </ul>
            </div>
        </div>
        <div id="realtime" style="display: none">
        </div>
    </div>
    <div class="col-md-9">
        <div id="googleMap" style="width:1000px;height:770px;"></div>
    </div>
</div>
</body>
</html>