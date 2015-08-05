package co.oym.geokitandroid;

/**
 * A Web Service exception.
 */
public class WSException extends Exception {

	private static final long serialVersionUID = -8411541817140551506L;

	/** The error code from the Web Service **/
	public String code;
	/** The error message from the Web Service **/
	public String message;

	public WSException() {
	}

	/**
	 * 
	 * @param code
	 * @param message
	 */
    public WSException(String code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

}
