package dev.malashenko.aggregator.core.http;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

@Slf4j
public class HttpResponder {
	private static final int MAXRETRIES = 3;

	public HttpResponse<String> getHttpResponse(String url) {
		HttpClient client = HttpClient.newBuilder()
			                    .connectTimeout(Duration.ofSeconds(5))
			                    .build();

		HttpRequest request = HttpRequest.newBuilder()
			                      .uri(URI.create(url))
			                      .GET()
			                      .build();

		int attempt = 0;
		HttpResponse<String> response = null;
		while (attempt < MAXRETRIES) {
			try {
				response = client.send(request, HttpResponse.BodyHandlers.ofString());
				log.info("Got response from url: " + url);
				break;
			} catch (IOException | InterruptedException e) {
				attempt++;

				log.error("Attempt " + attempt + " failed: " + e.getMessage() + "; URL: " + url);

				if (attempt >= MAXRETRIES) {
					log.error("Max retries reached. Giving up.");

					break;
				}

				long delay = (long) Math.pow(2, attempt) * 1000; // Экспоненциальная задержка
				try {
					Thread.sleep(delay);
				} catch (InterruptedException ex) {
					throw new RuntimeException(ex);
				}
			}

		}
		return response;
	}
}
