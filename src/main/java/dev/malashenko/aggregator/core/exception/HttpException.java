package dev.malashenko.aggregator.core.exception;

public class HttpException extends AppException {
	public HttpException(String message) {
		super(message);
	}

	public HttpException(String message, Throwable cause) {
		super(message, cause);
	}
}
