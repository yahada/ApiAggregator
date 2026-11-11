package dev.malashenko.aggregator.core.http;

import java.net.URI;
import java.net.http.HttpRequest;
import java.time.Duration;

public class HttpRequestFactory {

	public HttpRequest createGetRequest(String url) {
		return HttpRequest.newBuilder()
			       .uri(URI.create(url))
			       .timeout(Duration.ofSeconds(10))
			       .GET()
			       .build();
	}
}