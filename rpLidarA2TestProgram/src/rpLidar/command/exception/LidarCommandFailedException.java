package rpLidar.command.exception;

import rpLidar.command.LidarCommandException;

public class LidarCommandFailedException extends LidarCommandException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public LidarCommandFailedException(String msg) {
		super(msg);
	}

	public LidarCommandFailedException(String msg, Throwable previous) {
		super(msg, previous);
	}
}
