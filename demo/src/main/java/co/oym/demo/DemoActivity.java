package co.oym.demo;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.overlay.Marker;
import com.mapbox.mapboxsdk.overlay.PathOverlay;
import com.mapbox.mapboxsdk.views.MapView;

import co.oym.geokitandroid.Place;
import co.oym.geokitandroid.Route;
import co.oym.geokitandroid.TileLayer;
import co.oym.geokitandroid.WSCallback;
import co.oym.geokitandroid.WSClient;

/**
 * A simple demo application demonstrating geocoding, reverse geocoding, routing and mapping from OnYourMap Web Services, on top on Mapbox SDK.
 *
 */

public class DemoActivity extends Activity {

    // onyourmap server url and credentials. contact onyourmap support for more details
    private final String OYM_URL = "";
    private final String OYM_APP_KEY = "";
    private final String OYM_APP_REFERER = "";
    private final String OYM_TILE_FORMAT = "";
    private final String OYM_MAPPING_TEMPLATE = OYM_URL + "?f=m&ft=" + OYM_TILE_FORMAT + "&x={x}&y={y}&z={z}&key=" + OYM_APP_KEY + "&Referer=" + OYM_APP_REFERER;
    private final WSClient oymClient = new WSClient(OYM_URL, OYM_APP_KEY, OYM_APP_REFERER);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo);

        final Activity _this = this;

        // mapbox view is configured in activity layout xml file
        MapView mv = (MapView) _this.findViewById(R.id.mapview);

        // add onyourmap tile layer as default layer instead of mapbox layer
        TileLayer oymTileLayer = new TileLayer(OYM_MAPPING_TEMPLATE, 512);
        // depending on chosen onyourmap map tile format, adjust mininum and maximum zoom levels (actually min = 2 and max = 18)
        oymTileLayer.setMinimumZoomLevel(2).setMaximumZoomLevel(18);
        // set copyright as agreed with content providers
        oymTileLayer.setName("onyourmap").setAttribution("© onyourmap");
        mv.setTileSource(oymTileLayer);

        // activate user loc (from gps)
        mv.setUserLocationEnabled(true);
        // get coordinate
        LatLng userLocation = mv.getUserLocation();
        mv.setCenter(userLocation);
        mv.setZoom(16f);

        // get address from user location coordinates
        final Place.NearestRequest req = new Place.NearestRequest();
        req.location = userLocation;
        req.radius = 1000;

        try {
            // async call to oym nearest api
            oymClient.PlaceWS.nearest(req, new WSCallback<Place.NearestResponse>() {
                @Override
                public void onResponse(final Place.NearestResponse resp) {

                    if (!resp.places.isEmpty()) {
                        // center and put a marker on the map at first address location
                        final Place place = resp.places.get(0);

                        _this.runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(_this, "user located at " + place.description, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }

                @Override
                public void onFailure(final String errorMessage) {
                    _this.runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(_this, "oym error: " + errorMessage, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void geocode(View view) {

        Place.SearchRequest req = new Place.SearchRequest();
        req.address = "rivoli paris";

        final Activity _this = this;

        try {
            // async call to oym search api
            oymClient.PlaceWS.search(req, new WSCallback<Place.SearchResponse>() {
                @Override
                public void onResponse(final Place.SearchResponse resp) {

                    runOnUiThread(new Runnable() {
                        public void run() {
                            MapView mv = (MapView) _this.findViewById(R.id.mapview);

                            if (!resp.places.isEmpty()) {
                                // center and put a marker on the map at first address location
                                Place place = resp.places.get(0);
                                LatLng location = place.geometry.location;
                                mv.setCenter(location);
                                mv.setZoom(16f);

                                Marker marker = new Marker(mv, "test1", "test2", location);
                                mv.addMarker(marker);

                                Toast.makeText(_this, "geocoded at " + place.description, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }

                @Override
                public void onFailure(final String errorMessage) {
                    _this.runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(_this, "oym error: " + errorMessage, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void directions(View view) {

        Route.Request req = new Route.Request();
        req.start = new LatLng(48.866598, 2.322464);
        req.end = new LatLng(48.858620, 2.293961);
        req.distanceUnit = Route.Request.UNIT_KM;
        req.transportMode = Route.Request.TM_FASTEST_CAR;

        final Activity _this = this;

        try {
            // async call to oym directions api
            oymClient.RouteWS.directions(req, new WSCallback<Route.Response>() {
                @Override
                public void onResponse(final Route.Response resp) {

                    runOnUiThread(new Runnable() {
                        public void run() {


                            PathOverlay polyline = new PathOverlay(Color.GREEN, 20);
                            polyline.addPoints(resp.positions);

                            MapView mv = (MapView) _this.findViewById(R.id.mapview);
                            // add polyline to map
                            mv.getOverlays().add(polyline);
                            // zoom to polyline bounds
                            mv.zoomToBoundingBox(resp.bounds, true, true);
/*
                            // decode instructions
                            for (Route.Instruction instruction : resp.instructions) {
                                System.out.println(Route.Utility.renderInstruction(instruction, getResources()));
                            }
*/
                            Toast.makeText(_this, "route computed", Toast.LENGTH_SHORT).show();

                        }

                    });
                }

                    @Override
                public void onFailure(final String errorMessage) {
                    _this.runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(_this, "oym error: " + errorMessage, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });

        } catch (Exception ex) {
            ex.printStackTrace();
        }




    }
}
