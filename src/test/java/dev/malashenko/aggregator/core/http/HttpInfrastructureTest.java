package dev.malashenko.aggregator.core.http;

import org.junit.jupiter.api.Test;

import java.net.http.HttpRequest;

import static org.junit.jupiter.api.Assertions.*;

class HttpRequestFactoryTest {

	@Test
	void createGetRequest_returnsValidRequest() {
		HttpRequestFactory factory = new HttpRequestFactory();
		HttpRequest request = factory.createGetRequest("https://example.com");

		assertEquals("GET", request.method());
		assertEquals("https://example.com", request.uri().toString());
	}

	@Test
	void createGetRequest_withQueryParams() {
		HttpRequestFactory factory = new HttpRequestFactory();
		HttpRequest request = factory.createGetRequest("https://example.com?key=value");

		assertEquals("GET", request.method());
	}
}

class HttpClientProviderTest {

	@Test
	void getHttpClientProvider_returnsClient() {
		HttpClientProvider provider = new HttpClientProvider();
		assertNotNull(provider.getHttpClientProvider());
	}
}

class HttpInfrastructureTest {

	@Test
	void createDefault_createsInfrastructure() {
		HttpInfrastructure infra = HttpInfrastructure.createDefault();
		assertNotNull(infra.httpResponder());
	}
}
