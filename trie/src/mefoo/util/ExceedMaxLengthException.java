package mefoo.util;

public class ExceedMaxLengthException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ExceedMaxLengthException() {
	}

	public ExceedMaxLengthException(String message) {
		super(message);
	}

	public ExceedMaxLengthException(Throwable cause) {
		super(cause);
	}

	public ExceedMaxLengthException(String message, Throwable cause) {
		super(message, cause);
	}

	public ExceedMaxLengthException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
