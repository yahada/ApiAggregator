package dev.malashenko.aggregator.core.http;

import java.time.Duration;

public record HttpInfrastructure(HttpResponder httpResponder) {

	public static HttpInfrastructure createDefault() {
		HttpClientProvider clientProvider = new HttpClientProvider();
		HttpRequestFactory requestFactory = new HttpRequestFactory();

		RetryPolicy retryPolicy = new RetryPolicy(3, Duration.ofSeconds(1), Duration.ofSeconds(10));

		HttpResponder responder = new HttpResponder(clientProvider, requestFactory, retryPolicy);

		return new HttpInfrastructure(responder);
	}
}
