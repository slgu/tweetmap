<!DOCTYPE html>
<html>
<head>
    <script src="http://maps.googleapis.com/maps/api/js?key=AIzaSyBTFest0kbG-xe7_EFh-ZHZviRZ3TeMh1A&libraries=visualization"></script>
    <script>
        function initialize() {
            var latitude_min = 40.517368;
            var latitude_max = 40.911295;
            var longtitude_min = -74.210241;
            var longtitude_max = -73.741329;
            var district_number = 70;
            var head_map_data = [];
            var string = "<%=request.getAttribute("pos")%>";
            var geoArr = string.split("\t");
            for (var idx in geoArr) {
                xx_yy  = geoArr[idx];
                console.log(xx_yy);
                xx_yy = xx_yy.split(",");
                head_map_data.push({location: new google.maps.LatLng(
                        parseFloat(xx_yy[0]), parseFloat(xx_yy[1])), weight: 2});
            }
            var center = new google.maps.LatLng(25.715193, 42.015970);
            map = new google.maps.Map(document.getElementById('googleMap'), {
                center: center,
                zoom: 2
            });
            var heatmap = new google.maps.visualization.HeatmapLayer({
                data: head_map_data
            });
            heatmap.setMap(map);
        }
        google.maps.event.addDomListener(window, 'load', initialize);
    </script>
</head>

<body>
<div id="googleMap" style="width:1000px;height:770px;"></div>
</body>

</html>