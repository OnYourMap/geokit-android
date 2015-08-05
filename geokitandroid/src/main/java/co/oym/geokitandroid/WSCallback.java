package co.oym.geokitandroid;

/**
 * A Web Service callback interface for asynchronous call.
 */
public interface WSCallback<T> {

	void onResponse(T T);
	void onFailure(String errorMessage);
}
