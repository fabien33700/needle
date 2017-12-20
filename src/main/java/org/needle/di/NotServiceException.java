package org.needle.di;

public class NotServiceException extends Exception {

	private static final long serialVersionUID = 6708705225811744093L;

	public NotServiceException(String message, Throwable cause) {
		super(message, cause);
	}

	public NotServiceException(String message) {
		super(message);
	}
}
