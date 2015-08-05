package co.oym.geokitandroid;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * A Web Service response wrapper class.
 */
public class WSResponse<T> {

	public static final String OK = "200";
	public static final String ERROR = "500";
	public String statusCode;
	public T data;

	public WSResponse() {
	}
	
	/**
	 * 
	 * @param statusCode
	 */
	public WSResponse(String statusCode) {
		this(statusCode, null);
	}

	/**
	 * 
	 * @param statusCode
	 * @param data
	 */
	@JsonCreator
	public WSResponse(@JsonProperty("data") T data) {
		this.data = data;
	}
	
	/**
	 * 
	 * @param statusCode
	 * @param data
	 */
	public WSResponse(String statusCode, T data) {
		this.statusCode = statusCode;
		this.data = data;
	}
}
