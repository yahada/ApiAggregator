package dev.malashenko.aggregator.core.http;

import java.net.http.HttpClient;
import java.time.Duration;

public class HttpClientProvider {

	private final HttpClient httpClient;

	public HttpClientProvider() {
		this.httpClient = HttpClient.newBuilder()
			                  .connectTimeout(Duration.ofSeconds(5))
			                  .build();
	}

	public HttpClient getClient() {
		return httpClient;
	}
}

