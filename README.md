Android SDK providing a set of tools for mapping, routing, geocoding and reverse geocoding.



# geokit-android

## Introduction

The Geokit is an SDK providing a set of tools performing requests on the OnYourMap platform. Those requests include mapping, geocoding, reverse geocoding and routing. The MapBox mapping SDK is in charge of the map interaction, so this SDK must be included in your project.

More details about MapBox Android sdk can be found here:
- Documentation: https://www.mapbox.com/mapbox-android-sdk/
- Source code: https://github.com/mapbox/mapbox-android-sdk

For more details about OnYourMap, please contact our customer support here: contact@onyourmap.com

The project is divided in two parts:
- The OnYourMap Web Services library for Android. (androidsdkformapbox)
- A simple Test application demonstrating both MapBox and OnYourMap sdk integration. (demo)

## Installation

We recommend using Android Studio and Gradle when using OnYourMap Android SDK for Mapbox.
Note: Please find below important configuration lines (in red) to add to your gradle files.

In general settings.gradle:
```smalltalk
  include ':geokitandroid', ':demo'
```
 
In your application build.gradle:
```smalltalk
  android {
    compileSdkVersion 22
    buildToolsVersion "22.0.1"

    defaultConfig {
        applicationId "co.oym.demo"
        minSdkVersion 9
        targetSdkVersion 22
        versionCode 1
        versionName "1.0"
    }

    compileOptions {
        sourceCompatibility JavaVersion.VERSION_1_7
        targetCompatibility JavaVersion.VERSION_1_7
    }
  }
  
  dependencies {
    compile fileTree(dir: 'libs', include: ['*.jar'])
    compile 'com.android.support:appcompat-v7:22.2.0'
    compile('com.mapbox.mapboxsdk:mapbox-android-sdk:0.7.4@aar') {
        transitive = true
    }
    compile project(':geokitandroid')
  }
```
Be careful with JavaVersion and also compile and target sdk versions.

## Creating a map-centric application

In your AndroidManifest.xml, you must define the following lines:
```smalltalk
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
<uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
```
This will give access to Internet (downloading tiles and access Web Services), storage (storing tiles locally), and GPS for getting user's location.

## Mapping

The MapBox SDK is in charge of the mapping part. The OnYourMap tiles can be used thanks to the class *co.oym.android_sdk_for_mapbox.TileLayer* and can be used as a tile source for the MapBox SDK. This is how a map can be created:

First add a Map view in your application. This can be done by code or xml definition like any other UI class, like this:

```smalltalk
<com.mapbox.mapboxsdk.views.MapView xmlns:mapbox="http://schemas.android.com/apk/res-auto"
	android:id="@+id/mapview"
	android:layout_width="fill_parent"
	android:layout_height="fill_parent"
	mapbox:mapid=""
	mapbox:accessToken=""/>
```

To get access to the MapView:

```smalltalk
// mapbox view is configured in activity layout xml file
MapView mv = (MapView) this.findViewById(R.id.mapview);
```

Setup OnYourMap tiles for the MapView:

```smalltalk
// add onyourmap tile layer as default layer instead of mapbox layer
String OYM_MAPPING_TEMPLATE = OYM_URL + "?f=m&ft=" + OYM_TILE_FORMAT + "&x={x}&y={y}&z={z}&key=" + OYM_APP_KEY + "&Referer=" + OYM_APP_REFERER;
TileLayer oymTileLayer = new TileLayer(OYM_MAPPING_TEMPLATE, 512);

// depending on chosen onyourmap map tile format, adjust mininum and maximum zoom levels (actually min = 2 and max = 18)
oymTileLayer.setMinimumZoomLevel(2).setMaximumZoomLevel(18);

// set copyright as agreed with content providers
oymTileLayer.setName("onyourmap").setAttribution("© onyourmap");
mv.setTileSource(oymTileLayer);
```

The values for *OYM_MAP_URL*, *OYM_TILE_FORMAT*, *OYM_APP_KEY*, *OYM_APP_REFERER* should have been given to you by your OnYourMap.

Map view is ready, define center and zoom level

```smalltalk
mv.setCenter(new LatLng(48.86, 2.34));
mv.setZoom(16f);
```

## OnYourMap webservices		

Using OnYourMap webservices for geocoding and routing is done through the class *co.oym.android_sdk_for_mapbox.WSClient*. This class is initialised like this:

```smalltalk
WSClient oymClient = new WSClient(OYM_URL, OYM_APP_KEY, OYM_APP_REFERER);
```
	
## Geocoding

You can search places/adresses using the *search* function.

```smalltalk
Place.SearchRequest req = new Place.SearchRequest();
req.address = "rivoli paris";
```

All the parameters available for the request are described in the javadoc.

The request can be processed in a sync or async way:

```smalltalk
/* Sync request */
Place.SearchResponse resp = oymClient.PlaceWS.search(req, null);

/* Asnyc request */
oymClient.PlaceWS.search(req, new WSCallback<Place.SearchResponse>() {
	@Override
	public void onResponse(final Place.SearchResponse resp) {
		// do something with the places found
	}

	@Override
	public void onFailure(final String errorMessage) {
		// handle error
	}
});
```

## Reverse geocoding

Places can be retrieved around a location using the *nearest* function.
All the parameters available for the request are described in the javadoc.

```smalltalk
final Place.NearestRequest req = new Place.NearestRequest();
req.location = new LatLng(48.866598, 2.322464);
req.radius = 100;
```

If the radius is 0, only the nearest place will be returned. Otherwise, everything in the radius will be returned.

```smalltalk
/* Sync request */
Place.NearestResponse resp = oymClient.PlaceWS.nearest(req, null);

/* Async request */
oymClient.PlaceWS.nearest(req, new WSCallback<Place.NearestResponse>() {
	@Override
	public void onResponse(final Place.NearestResponse resp) {
		// do something with the places found
	}

	@Override
	public void onFailure(final String errorMessage) {
		// handle error
	}
});
```

## Routing

The function *directions* provides a route between two coordinates.
All the parameters available for the request are described in the javadoc.

```smalltalk
Route.Request req = new Route.Request();
req.start = new LatLng(48.866598, 2.322464);
req.end = new LatLng(48.858620, 2.293961);
req.distanceUnit = Route.Request.UNIT_KM;
req.transportMode = Route.Request.TM_FASTEST_CAR;
```

Like the geocoding, route can be computed in a sync or async way.

```smalltalk
/* Sync request */
Route.Response resp = oymClient.RouteWS.directions(req, null); 

/* Async request */
oymClient.RouteWS.directions(req, new WSCallback<Route.Response>() {
	@Override
	public void onResponse(final Route.Response resp) {
		// do something with the computed route
	}

	@Override
	public void onFailure(final String errorMessage) {
		// handle error
	}
});
```

## Routing utilities

Some functions are here to simplify the developers life when using the *directions* function.
They can be found in *Route.Utility* class.


### checkDisplayLevel

The route geometry is provided with the maximum accuracy available. In order to speed up the route shape rendering, an array called "levels" is returned in the *Route.Response* object. This array contains, for each point of the route, all the zoom levels this point should be displayed. The static method *Route.Utility.checkDisplayLevel()* will tell you if a point should be displayed or not, based on this value. That way, the number of points to display can be greatly reduced, with a huge speed boost when rendering long routes.

```smalltalk
if (Route.Utility.checkDisplayLevel(displayLevelValue, currentZoomLevel)) {
	// keep the point for this zoom level
}
```

### renderInstruction

The instructions returned by the *directions* function must be processed before being displayed on screen. The static method *Route.Utility.renderInstruction()* will transform an encoded instruction into a human readable string.

```smalltalk
/* Display all the route instructions */
for (Route.Instruction instruction : resp.instructions) {
	System.out.println(Route.Utility.renderInstruction(instruction, getResources()));
}
```

The file listing all the possible instructions is located in the provided bundle geokit/res/values/oym-route.xml. This file can be modified if needed. A translation can be added following Android resources bundle way.


	