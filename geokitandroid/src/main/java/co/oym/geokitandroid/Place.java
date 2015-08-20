package co.oym.geokitandroid;

import com.mapbox.mapboxsdk.geometry.LatLng;

/**
 * A Place represents an Address or a Point Of Interest (POI)
 */
public class Place {
	
	public String id;
	/** The representation of the place as one String. For example can the full address. **/
	public String description;
	/** A list of key/value pairs for this place. Actually used for POIs **/
	public java.util.Map<String, String> properties = new java.util.HashMap<String, String>();
	/** The dataset can be "ADDR" or "POI" **/
	public String dataset;
	/** The type of place. Can be "C" for a city, "0" for a street, or a POI type **/
	public String type;
	/** The administrative levels of the address **/
	public java.util.Map<String, String> components = new java.util.HashMap<String, String>();
	/** The geometry of the place **/
	public Geometry geometry = new Geometry();

	/**
	 * A Geometry represents a place's location and may also contain some additional place geometry.
	 */
	public static class Geometry {
		/** The location in WGS84 **/
		public LatLng location;
		/** The type of location **/
		public int type;
		/** Some additional geometry for a place, like the complete street geometry of the external polygon of a city **/
		public java.util.List<java.util.List<LatLng>> raw = new java.util.ArrayList<java.util.List<LatLng>>();

		@Override
		public String toString() {
			return "Geometry{" +
					"location=" + location +
					", type=" + type +
					", raw=" + raw +
					'}';
		}
	}

	@Override
	public String toString() {
		return "Place{" +
				"id='" + id + '\'' +
				", description='" + description + '\'' +
				", properties=" + properties +
				", dataset='" + dataset + '\'' +
				", type='" + type + '\'' +
				", components=" + components +
				", geometry=" + geometry +
				'}';
	}

	/**
	 * A Place Search request.
	 */
	public static class SearchRequest {
		/** The maximum number of places **/
		public int maxResponses = 10;
		/** The iso country code in 2 letters **/
		public String country;
		/** The city **/
		public String locality;
		/** the state or county **/
		public String adminArea;
		/** The postal code **/
		public String postcode;
		/** The address: full or just street part **/
		public String address;
		/** The output language **/
		public String lang;
		/** Places inside the viewport will be better ranked **/
		public Box viewport;
		/** Places with provided favorite country will be better ranked **/
		public String favoriteCountry;

		@Override
		public String toString() {
			return "SearchRequest{" +
					"maxResponses=" + maxResponses +
					", country='" + country + '\'' +
					", locality='" + locality + '\'' +
					", adminArea='" + adminArea + '\'' +
					", postcode='" + postcode + '\'' +
					", address='" + address + '\'' +
					", lang='" + lang + '\'' +
					", viewport=" + viewport +
					", favoriteCountry='" + favoriteCountry + '\'' +
					'}';
		}
	}

	/**
	 * A Place Search response.
	 */
	public static class SearchResponse {
		/** The response time in milliseconds **/
		public long time;
		/** The number of places **/
		public long totalHits;
		/** The status of the response **/
		public String status;
		/** The nearest places **/
		public java.util.List<Place> places = new java.util.ArrayList<Place>();

		@Override
		public String toString() {
			return "SearchResponse{" +
					"time=" + time +
					", totalHits=" + totalHits +
					", status='" + status + '\'' +
					", predictions=" + places +
					'}';
		}
	}

	/**
	 * A Place Nearest request.
	 */
	public static class NearestRequest {
		/** The maximum number of places **/
		public int maxResponses = 10;
		/** The location in WGS84**/
		public LatLng location = null;
		/** The radius of search in meters **/
		public int radius = 0;
		/** The output language **/
		public String lang;

		@Override
		public String toString() {
			return "NearestRequest{" +
					"maxResponses=" + maxResponses +
					", location=" + location +
					", radius=" + radius +
					", lang='" + lang + '\'' +
					'}';
		}
	}

	/**
	 * A Place Nearest response.
	 */
	public static class NearestResponse {
		/** The response time in milliseconds **/
		public long time;
		/** The number of places **/
		public long totalHits;
		/** The status of the response **/
		public String status;
		/** The nearest places **/
		public java.util.List<Place> places = new java.util.ArrayList<Place>();

		@Override
		public String toString() {
			return "NearestResponse{" +
					"time=" + time +
					", totalHits=" + totalHits +
					", status='" + status + '\'' +
					", places=" + places +
					'}';
		}
	}

	/**
	 * A Place Autocomplete request.
	 */
	public static class AutocompleteRequest {
		/** The maximum number of suggested places **/
		public int maxResponses = 10;
		/** The address string to complete **/
		public String place = null;
		/** The server side profile id to rank suggests (sort/filtering) **/
		public String profile = null;

		@Override
		public String toString() {
			return "AutocompleteRequest{" +
					"maxResponses=" + maxResponses +
					", place='" + place + '\'' +
					", profile='" + profile + '\'' +
					'}';
		}
	}

	/**
	 * A Place suggest from autocomplete request.
	 */
	public static class Suggest {
		/** The suggested place as a string **/
		public String place;
		/** The range of matching characters in the suggest place **/
		public String range;
		/** The score of the suggested place **/
		public int score;
		/** The type of suggested place **/
		public int type;

		@Override
		public String toString() {
			return "Suggest{" +
					"place='" + place + '\'' +
					", range='" + range + '\'' +
					", score=" + score +
					", type=" + type +
					'}';
		}
	}

	/**
	 * A Place Autocomplete response.
	 */
	public static class AutocompleteResponse {
		/** The response time in milliseconds **/
		public long time;
		/** The number of suggested places **/
		public long totalHits;
		/** The status of the response **/
		public String status;
		/** The suggested places **/
		public java.util.List<Place.Suggest> suggests = new java.util.ArrayList<Place.Suggest>();

		@Override
		public String toString() {
			return "AutocompleteResponse{" +
					"time=" + time +
					", totalHits=" + totalHits +
					", status='" + status + '\'' +
					", suggests=" + suggests +
					'}';
		}
	}

}

