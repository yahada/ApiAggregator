package dev.malashenko.aggregator.core.http;

import java.net.http.HttpClient;
import java.time.Duration;

public class HttpClientProvider {

	public HttpClient getHttpClientProvider() {
		return HttpClient.newBuilder()
			                  .connectTimeout(Duration.ofSeconds(5))
			                  .build();
	}

}

