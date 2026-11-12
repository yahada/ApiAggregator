package dev.malashenko.aggregator.core.http;


import lombok.Getter;

import java.time.Duration;

public class RetryPolicy {

	@Getter
	private final int maxAttempts;
	private final Duration initialDelay;
	private final Duration maxDelay;

	public RetryPolicy(int maxAttempts, Duration initialDelay, Duration maxDelay) {
		if (maxAttempts < 1) {
			throw new IllegalArgumentException("maxAttempts must be positive");
		}

		if (initialDelay.isNegative() || maxDelay.isNegative() || initialDelay.compareTo(maxDelay) > 0) {
			throw new IllegalArgumentException("Invalid delay configuration");
		}

		this.maxAttempts = maxAttempts;
		this.initialDelay = initialDelay;
		this.maxDelay = maxDelay;
	}

	public Duration getDelay(int attempt) {
		long multiplier = 1L << Math.min(attempt - 1, 30);

		Duration delay = initialDelay.multipliedBy(multiplier);

		return delay.compareTo(maxDelay) > 0 ? maxDelay : delay;
	}
}