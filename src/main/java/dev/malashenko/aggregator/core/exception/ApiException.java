package dev.malashenko.aggregator.core.exception;

public class ApiException extends AppException {
	public ApiException(String message) {
		super(message);
	}

	public ApiException(String message, Throwable cause) {
		super(message, cause);
	}
}
