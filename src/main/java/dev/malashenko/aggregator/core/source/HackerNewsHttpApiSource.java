package dev.malashenko.aggregator.core.source;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.malashenko.aggregator.core.exception.ApiException;
import dev.malashenko.aggregator.core.http.HttpResponder;
import dev.malashenko.aggregator.core.model.DataRecord;
import dev.malashenko.aggregator.core.model.RequestParams;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HackerNewsHttpApiSource extends AbstractHttpApiSource {
	private final static String ORIGIN_URL = "https://hn.algolia.com/api/v1/search";
	private final static String SOURCE_ID = "hackernews";
	private final ObjectMapper objectMapper;

	public HackerNewsHttpApiSource(HttpResponder responder, ObjectMapper objectMapper) {
		super(responder);
		this.objectMapper = objectMapper;
	}

	@Override
	protected String originUrl() {
		return ORIGIN_URL;
	}

	@Override
	public RequestParams defaultParams() {
		return new RequestParams(Map.of("query", "java", "hitsPerPage", "100"));
	}

	@Override
	public List<DataRecord> map(String json) {
		JsonNode root;
		try {
			root = objectMapper.readTree(json);
		} catch (JsonProcessingException e) {
			throw new ApiException("HN parse error: " + e.getMessage(), e);
		}

		List<DataRecord> records = new ArrayList<>();
		for (JsonNode hit : root.path("hits")) {
			Map<String, Object> metrics = objectMapper.convertValue(hit, new TypeReference<>() {});

			records.add(new DataRecord(
				SOURCE_ID,
				hit.path("objectID").asText(),
				Instant.now(),
				metrics));
		}
		return records;
	}
}
