package dev.malashenko.aggregator.core.http;

import dev.malashenko.aggregator.core.exception.ApiException;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

@Slf4j
public class HttpResponder {

	private final HttpClient httpClient;
	private final HttpRequestFactory requestFactory;
	private final RetryPolicy retryPolicy;

	public HttpResponder(HttpClientProvider clientProvider, HttpRequestFactory requestFactory, RetryPolicy retryPolicy) {
		this.httpClient = clientProvider.getHttpClientProvider();
		this.requestFactory = requestFactory;
		this.retryPolicy = retryPolicy;
	}

	public HttpResponse<String> getHttpResponse(String url) {
		HttpRequest request = requestFactory.createGetRequest(url);

		for (int attempt = 1; attempt <= retryPolicy.getMaxAttempts(); attempt++) {
			log.info("HTTP request attempt {}/{} to: {}", attempt, retryPolicy.getMaxAttempts(), url);
			try {
				HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

				int statusCode = response.statusCode();

				if (statusCode >= 400) {
					log.error("HTTP error {} from URL: {}. Body: {}", statusCode, url, response.body());
					throw new ApiException("HTTP " + statusCode + " from " + url + ": " + response.body());
				}

				log.info("Got response from URL: {}", url);

				return response;

			} catch (IOException e) {
				log.error("Attempt {} failed. URL: {}. Error: {}", attempt, url, e.getMessage());

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