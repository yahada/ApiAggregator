package dev.malashenko.aggregator.core.source;

import dev.malashenko.aggregator.core.http.HttpResponder;
import dev.malashenko.aggregator.core.model.DataRecord;
import dev.malashenko.aggregator.core.model.EntityType;
import dev.malashenko.aggregator.core.model.RequestParams;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AbstractHttpApiSourceTest {

	@Mock
	HttpResponder responder;
	@Mock
	ApiMapper mapper;
	@Mock
	HttpResponse<String> httpResponse;

	private TestApiSource source;

	@BeforeEach
	void setUp() {
		source = new TestApiSource(responder, mapper);
	}

	@Test
	void getApiRequest_callsResponderAndMapper() {
		String jsonBody = "{\"data\": []}";
		when(httpResponse.body()).thenReturn(jsonBody);
		when(responder.getHttpResponse("https://test.com?format=json"))
			.thenReturn(httpResponse);
		when(mapper.map(jsonBody)).thenReturn(List.of());

		List<DataRecord> result = source.getApiRequest(new RequestParams(Map.of()));

		assertTrue(result.isEmpty());
		verify(responder).getHttpResponse("https://test.com?format=json");
		verify(mapper).map(jsonBody);
	}

	@Test
	void getApiRequest_returnsMappedRecords() {
		String jsonBody = "{\"data\": []}";
		DataRecord record = new DataRecord("test", EntityType.NEWS, "1", java.util.Optional.empty(), Map.of());

		when(httpResponse.body()).thenReturn(jsonBody);
		when(responder.getHttpResponse(any())).thenReturn(httpResponse);
		when(mapper.map(jsonBody)).thenReturn(List.of(record));

		List<DataRecord> result = source.getApiRequest(new RequestParams(Map.of()));

		assertEquals(1, result.size());
		assertEquals("test", result.get(0).source());
	}

	@Test
	void id_returnsSourceId() {
		assertEquals("test", source.id());
	}

	@Test
	void knownParams_returnsExpectedSet() {
		Set<String> params = source.knownParams();
		assertTrue(params.contains("param1"));
	}

	// Concrete implementation for testing the abstract class
	static class TestApiSource extends AbstractHttpApiSource {
		protected TestApiSource(HttpResponder responder, ApiMapper mapper) {
			super(responder, mapper);
		}

		@Override
		protected String originUrl() {
			return "https://test.com";
		}

		@Override
		protected RequestParams enrich(RequestParams params) {
			Map<String, String> enriched = new java.util.LinkedHashMap<>(params.queryParams());
			enriched.put("format", "json");
			return new RequestParams(enriched);
		}

		@Override
		public String id() {
			return "test";
		}

		@Override
		public Set<String> knownParams() {
			return Set.of("param1");
		}
	}
}
