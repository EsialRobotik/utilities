package action;

public class ActionPoolException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ActionPoolException(String reason, Throwable previous) {
		super (reason, previous);
	}
	
}
