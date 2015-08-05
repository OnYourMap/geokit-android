package co.oym.geokitandroid;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mapbox.mapboxsdk.geometry.BoundingBox;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * OnYourMap GIS Web Services Client. <br>
 * Features: <br>
 *  - Geocoding (address lookup) <br>
 *  - Reverse geocoding (get address from coordinate) <br>
 *  - Routing (get directions between 2 locations) <br>
 *  - Mapping utility class for MapBox SDK <br><br>
 *  Before starting using this client, you must have first contacted OnYourMap support in order to get credentials for accessing its Web Services.
 *  Three parameters are mandatory: <br>
 *  - webServiceUrl: The url of OnYourMap Web Services <br>
 *  - appReferer: Your application Identifier when using OnYourMap Web Services <br>
 *  - appKey: Your application key when using OnYourMap Web Services <br>
 *
 */
public class WSClient {

	public static final String TAG = "mapbox_oym";
	private final ExecutorService executor = Executors.newSingleThreadExecutor();
	private OkHttpClient client = new OkHttpClient();

	private String webServiceUrl;
	private String appKey;
	private String appReferer;

	/**
	 * The client entry point.
	 * @param webServiceUrl
	 */
	public WSClient(String webServiceUrl) {
		this(webServiceUrl, null, null);
	}

	/**
	 * The client entry point.
	 * @param webServiceUrl
	 * @param appKey
	 * @param appReferer
	 */
	public WSClient(String webServiceUrl, String appKey, String appReferer) {
		this.webServiceUrl = webServiceUrl;
		this.appKey = appKey;
		this.appReferer = appReferer;
	}

	/**
	 * Get access to the OkHttpClient object for settings additional parameters like proxy
	 * @return
	 */
	public OkHttpClient getOkHttpClient() {
		return client;
	}

	/**
	 * You can replace the OkHttpClient object used internally by this library if you already use another OkHttpClient in your code and want to reduce memory footprint for httpclients.
	 * @param client
	 */
	public void setOkHttpClient(OkHttpClient client) {
		this.client = client;
	}

	/**
	 * Call this method if you need to kill OkHttpClient thread pool when you need to exit your application
	 */
	public void shutdown() {
		client.getDispatcher().getExecutorService().shutdown();
	}

	private String jsonify(Object obj) throws Exception {
		java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
		JSON.mapper.writeValue(out, obj);

		final byte[] data = out.toByteArray();
		String str = new String(data);
		return str;
	}

	/**
	 * Do not use manually. Only used internally for decoding Web Services content.
	 * @param json
	 * @param outputClass
	 * @param <T>
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public static <T> T decodeContent(String json, Class<T> outputClass) throws Exception {
		
		WSResponse<T> resp = null;
		try {
			JavaType javaType = JSON.mapper.getTypeFactory().constructParametrizedType(WSResponse.class, WSResponse.class, outputClass);
			resp = JSON.mapper.readValue(json, javaType);
		} catch (Exception ex) {
			JavaType javaType = JSON.mapper.getTypeFactory().constructParametrizedType(WSResponse.class, WSResponse.class, String.class);
			resp = JSON.mapper.readValue(json, javaType);
		}
		if (resp == null || !resp.statusCode.equals("200")) {
			throw new WSException(resp.statusCode, (String) resp.data);
		}
		return resp.data;
	}

	private String execute(com.squareup.okhttp.Request request) throws Exception {
		// POST
		try {
			com.squareup.okhttp.Response response = client.newCall(request).execute();
			if (response.isSuccessful()) {
        		String content = response.body().string();
				return content;
				
			} else {
				throw new Exception("network error: " + response.code());
			}

		} catch (Exception ex) {
			throw ex;

		} finally {
		}
	}

	public static final MediaType JSON_TYPE = MediaType.parse("application/json; charset=utf-8");

	public RouteWS RouteWS = new RouteWS();
	public PlaceWS PlaceWS = new PlaceWS();

	/**
	 * Place Web Service allows to: <br>
	 *  - Search for an address (and get its WGS84 coordinate) <br>
	 *  - Get nearest address from a WGS84 coordinate <br>
	 */
	public class PlaceWS {

		/**
		 * Search for an address and get its WGS84 coordinate. <br>
		 * If a callback object is provided, then the method will be executed asynchronously. If callback is null, then the method will be executed synchronously.
		 * @param request
		 * @param callback
		 * @return
		 * @throws Exception
		 */
		public Place.SearchResponse search(Place.SearchRequest request, final WSCallback<Place.SearchResponse> callback) throws Exception {
			return search(appKey, request, callback);
		}

		/**
 		 * Search for an address and get its WGS84 coordinate.
		 * If a callback object is provided, then the method will be executed asynchronously. If callback is null, then the method will be executed synchronously.
		 * @param appKey
		 * @param request
		 * @param callback
		 * @return
		 * @throws Exception
		 */
		public Place.SearchResponse search(String appKey, Place.SearchRequest request, final WSCallback<Place.SearchResponse> callback) throws Exception {
			
			String jsonObject = jsonify(request);
			
			com.squareup.okhttp.Request okReq = new com.squareup.okhttp.Request.Builder()
			.url(webServiceUrl + "/place/search")
			.post(RequestBody.create(JSON_TYPE, jsonObject))
	        .addHeader("Content-Type", "application/json; charset=utf-8")
			.addHeader("appKey", appKey)
	        .addHeader("Referer", appReferer)
			.build();

			if (callback == null) {
				String content = execute(okReq);
				return decodeContent(content, Place.SearchResponse.class);
				
			} else {
				DownloadTask<Place.SearchResponse> task = new DownloadTask<Place.SearchResponse>(client, DownloadTask.PLACE_SEARCH, okReq, callback);
				executor.submit(task);
				return null;
			}
		}

		/**
		 * Get nearest address from a WGS84 coordinate.
		 * If a callback object is provided, then the method will be executed asynchronously. If callback is null, then the method will be executed synchronously.
		 * @param request
		 * @param callback
		 * @return
		 * @throws Exception
		 */
		public Place.NearestResponse nearest(Place.NearestRequest request, final WSCallback<Place.NearestResponse> callback) throws Exception {
			return nearest(appKey, request, callback);
		}

		/**
		 * Get nearest address from a WGS84 coordinate.
		 * If a callback object is provided, then the method will be executed asynchronously. If callback is null, then the method will be executed synchronously.
		 * @param appKey
		 * @param request
		 * @param callback
		 * @return
		 * @throws Exception
		 */
		public Place.NearestResponse nearest(String appKey, Place.NearestRequest request, final WSCallback<Place.NearestResponse> callback) throws Exception {

			String jsonObject = jsonify(request);

			com.squareup.okhttp.Request okReq = new com.squareup.okhttp.Request.Builder()
					.url(webServiceUrl + "/place/nearest")
					.post(RequestBody.create(JSON_TYPE, jsonObject))
					.addHeader("Content-Type", "application/json; charset=utf-8")
					.addHeader("appKey", appKey)
					.addHeader("Referer", appReferer)
					.build();

			if (callback == null) {
				String content = execute(okReq);
				return decodeContent(content, Place.NearestResponse.class);

			} else {
				DownloadTask<Place.NearestResponse> task = new DownloadTask<Place.NearestResponse>(client, DownloadTask.PLACE_NEAREST, okReq, callback);
				executor.submit(task);
				return null;
			}
		}
	}

	/**
	 * Route Web Service allows to: <br>
	 *  - Get directions between 2 WGS84 coordinates
	 */
	public class RouteWS {

		/**
		 * Get directions between 2 WGS84 coordinates.
		 * If a callback object is provided, then the method will be executed asynchronously. If callback is null, then the method will be executed synchronously.
		 * @param request
		 * @param callback
		 * @return
		 * @throws Exception
		 */
		public Route.Response directions(Route.Request request, final WSCallback<Route.Response> callback) throws Exception {
			return directions(appKey, request, callback);
		}

		/**
		 * Get directions between 2 WGS84 coordinates.
		 * If a callback object is provided, then the method will be executed asynchronously. If callback is null, then the method will be executed synchronously.
		 * @param appKey
		 * @param request
		 * @param callback
		 * @return
		 * @throws Exception
		 */
		public Route.Response directions(String appKey, Route.Request request, final WSCallback<Route.Response> callback) throws Exception {

			String jsonObject = jsonify(request);

			com.squareup.okhttp.Request okReq = new com.squareup.okhttp.Request.Builder()
					.url(webServiceUrl + "/route/directions")
					.post(RequestBody.create(JSON_TYPE, jsonObject))
					.addHeader("Content-Type", "application/json; charset=utf-8")
					.addHeader("appKey", appKey)
					.addHeader("Referer", appReferer)
					.build();

			if (callback == null) {
				String content = execute(okReq);
				return decodeContent(content, Route.Response.class);

			} else {
				DownloadTask<Route.Response> task = new DownloadTask<Route.Response>(client, DownloadTask.ROUTE_DIRECTIONS, okReq, callback);
				executor.submit(task);
				return null;
			}
		}
	}


	private static abstract class MixinLatLng {
		MixinLatLng(@JsonProperty("lat") double latitude, @JsonProperty("lng") double longitude) { }

		@JsonProperty("lat") abstract double getLatitude();
		@JsonProperty("lng") abstract double getLongitude();
		@JsonIgnore abstract double getAltitude();
	}

	private static abstract  class MixinBoundingBox {

		MixinBoundingBox(@JsonProperty("north") double north, @JsonProperty("east") double east, @JsonProperty("south") double south, @JsonProperty("west") double west) { }
	}

	/**
	 * Utility class for serializing/deserializing JSON<->Class
	 */
	public static class JSON {
		public static ObjectMapper mapper = new ObjectMapper();

		static {
			mapper.addMixIn(LatLng.class, MixinLatLng.class);
			mapper.addMixIn(BoundingBox.class, MixinBoundingBox.class);
		}

		public static String toString(Object obj) {
			try {
				return mapper.writeValueAsString(obj);
				
			} catch (Exception ex) {
			}
			return "{}";
		}
	}
	
	private static class DownloadTask<T> implements Runnable {

		public static final int PLACE_SEARCH = 1;
		public static final int PLACE_NEAREST = 2;
		public static final int ROUTE_DIRECTIONS = 3;

		private final OkHttpClient client; 
		private final int type;
		private final Request req;
		private final WSCallback<T> listener;
		
		public DownloadTask(OkHttpClient client, int type, Request req, WSCallback<T> listener) {
			this.client = client;
			this.type = type;
			this.req = req;
			this.listener = listener;
		}

		public void run() {
			client.newCall(req).enqueue(new Callback() {

				@SuppressWarnings("unchecked")
				public void onResponse(Response response) throws IOException {
					String content = null;
					try {
						if (!response.isSuccessful()) {
							throw new IOException("Unexpected code " + response);
						}

						content = response.body().string();

						if (type == PLACE_SEARCH) {
							listener.onResponse((T) WSClient.decodeContent(content, Place.SearchResponse.class));

						} else if (type == PLACE_NEAREST) {
							listener.onResponse((T) WSClient.decodeContent(content, Place.NearestResponse.class));

						} else if (type == ROUTE_DIRECTIONS) {
							listener.onResponse((T) WSClient.decodeContent(content, Route.Response.class));

						}

					} catch (Exception ex) {
						// parsing error or real error
						String errorMessage = "oym response parsing error";
						try {
							errorMessage = WSClient.decodeContent(content, String.class);

						} catch (Exception ex2) {
						}
						listener.onFailure(errorMessage);
					}
				}

				public void onFailure(Request request, IOException e) {
					e.printStackTrace();
					listener.onFailure("network error: " + e.getMessage());
				}

			});		
		}

	}

}
