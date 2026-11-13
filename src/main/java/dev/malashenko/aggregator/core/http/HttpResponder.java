package dev.malashenko.aggregator.core.http;

import dev.malashenko.aggregator.core.exception.ApiException;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

@Slf4j
public class HttpResponder {

	private static final HttpClient httpClient = HttpClient.newHttpClient();
	private final RetryPolicy retryPolicy;

	public HttpResponder(RetryPolicy retryPolicy) {
		this.retryPolicy = retryPolicy;
	}

	public static HttpResponder getResponder() {
		RetryPolicy retryPolicy = new RetryPolicy(3, Duration.ofSeconds(1), Duration.ofSeconds(10));

		return new HttpResponder(retryPolicy);
	}

	public String getResponse(String url) {
		HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).GET().build();

		for (int attempt = 1; attempt <= retryPolicy.getMaxAttempts(); attempt++) {
			log.info("HTTP request attempt {}/{} to: {}", attempt, retryPolicy.getMaxAttempts(), url);
			try {
				HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

				int statusCode = response.statusCode();

				if (statusCode >= 400) {
					throw new ApiException("HTTP " + statusCode + " from " + url + ": " + response.body());
				}

				log.info("Got response from URL: {}", url);

				return response.body();

			} catch (IOException e) {
				log.warn("Attempt {} failed. URL: {}. Error: {}", attempt, url, e.getMessage());

				if (attempt == retryPolicy.getMaxAttempts()) {
					throw new ApiException("Failed to get HTTP response from: " + url, e);
				}

				waitBeforeRetry(retryPolicy.getDelay(attempt));

			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();

				throw new ApiException("HTTP request was interrupted: " + url, e);
			}
		}

		throw new ApiException("Unexpected retry state");
	}

	private void waitBeforeRetry(Duration delay) {
		log.info("Retrying in {} ms", delay.toMillis());
		try {
			Thread.sleep(delay.toMillis());
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new ApiException("Retry was interrupted", e);
		}
	}
}