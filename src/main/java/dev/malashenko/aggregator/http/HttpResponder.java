package dev.malashenko.aggregator.http;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

@Slf4j
public class HttpResponder {

	private final HttpClient client;

	public HttpResponder(HttpClient client) {
		this.client = client;
	}

	public String get(String url) throws IOException, InterruptedException {
		log.info("HTTP GET request to: {}", url);

		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(url))
				.timeout(Duration.ofSeconds(10))
				.GET()
				.build();

		HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

		int statusCode = response.statusCode();
		log.info("HTTP response received: status {} from {}", statusCode, url);

		if (statusCode >= 400) {
			log.warn("HTTP error {} from {}: {}", statusCode, url, response.body());
			throw new IOException("HTTP error: " + statusCode);
		}

		log.info("Successfully got response from URL: {}", url);
		return response.body();
	}
}