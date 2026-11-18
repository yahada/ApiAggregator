package dev.malashenko.aggregator.core.http;

import dev.malashenko.aggregator.core.exception.HttpException;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

@Slf4j
public class HttpResponder {

	private final  HttpClient httpClient;
	private final RetryPolicy retryPolicy;

	public HttpResponder(RetryPolicy retryPolicy, HttpClient httpClient) {
		this.retryPolicy = retryPolicy;
		this.httpClient = httpClient;
	}

	public static HttpResponder getDefaultResponder() {
		RetryPolicy retryPolicy = new RetryPolicy(3, Duration.ofSeconds(1), Duration.ofSeconds(10));
		return new HttpResponder(retryPolicy, HttpClient.newHttpClient());
	}

	public String getResponse(String url) {
		HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).GET().build();

		for (int attempt = 1; attempt <= retryPolicy.getMaxAttempts(); attempt++) {
			log.info("HTTP request attempt {}/{} to: {}", attempt, retryPolicy.getMaxAttempts(), url);
			try {
				HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

				int statusCode = response.statusCode();

				if (statusCode >= 400) {
					throw new HttpException("HTTP " + statusCode + " from " + url + ": " + response.body());
				}

				log.info("Got response from URL: {}", url);

				return response.body();

			} catch (IOException e) {
				log.warn("Attempt {} failed. URL: {}. Error: {}", attempt, url, e.getMessage());

				if (attempt == retryPolicy.getMaxAttempts()) {
					throw new HttpException("Failed to get HTTP response from: " + url, e);
				}

				waitBeforeRetry(retryPolicy.getDelay(attempt));

			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();

				throw new HttpException("HTTP request was interrupted: " + url, e);
			}
		}

		throw new HttpException("Unexpected retry state");
	}

	private void waitBeforeRetry(Duration delay) {
		log.info("Retrying in {} ms", delay.toMillis());
		try {
			Thread.sleep(delay.toMillis());
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new HttpException("Retry was interrupted", e);
		}
	}
}