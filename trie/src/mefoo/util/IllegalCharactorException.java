package mefoo.util;

public class IllegalCharactorException extends RuntimeException {

	private static final long serialVersionUID = -1445552636017874859L;

	public IllegalCharactorException() {
		super();
	}

	public IllegalCharactorException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public IllegalCharactorException(String message, Throwable cause) {
		super(message, cause);
	}

	public IllegalCharactorException(String message) {
		super(message);
	}

	public IllegalCharactorException(Throwable cause) {
		super(cause);
	}

	
	
}
