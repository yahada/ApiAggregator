package dev.malashenko.aggregator.core.exception;

public class ParsingException extends AppException {
	public ParsingException(String message) {
		super(message);
	}

	public ParsingException(String message, Throwable cause) {
		super(message, cause);
	}
}
