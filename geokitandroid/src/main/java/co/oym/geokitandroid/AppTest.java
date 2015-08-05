package co.oym.geokitandroid;

import com.mapbox.mapboxsdk.geometry.LatLng;

/**
 * A Test class. (for debugging purpose)
 */
public class AppTest {
	
	public static void main(String[] args) {
		
		try {
		//	final WSClient oymClient = new WSClient("http://192.168.0.3:8099/oym2", "ML52G2591315H115600J25AN1", "http://metrolab.fr");
			final WSClient oymClient = new WSClient("http://ratp-staging.onyourmap.com/oym2", "ML52G2591315H115600J25AN1", "http://metrolab.fr");
			/*
			Place.SearchRequest req = new Place.SearchRequest();
			req.address = "paris";

			Place.SearchResponse resp = oymClient.PlaceWS.search(req, null);
			System.out.println(resp);
			*/

			Place.NearestRequest req = new Place.NearestRequest();
			req.location = new LatLng(48.8, 2.2);
			req.radius = 1000;

			Place.NearestResponse resp = oymClient.PlaceWS.nearest(req, null);
			System.out.println(resp);

			
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
