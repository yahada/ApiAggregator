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

public class KitsuHttpApiSource extends AbstractHttpApiSource {

	private static final String ORIGIN_URL = "https://kitsu.io/api/edge/anime";
	private static final String SOURCE_ID = "kitsu";
	private final ObjectMapper objectMapper;

	public KitsuHttpApiSource(HttpResponder responder, ObjectMapper objectMapper) {
		super(responder);
		this.objectMapper = objectMapper;

	}

	@Override
	protected String originUrl() {
		return ORIGIN_URL;
	}

	@Override
	public RequestParams defaultParams() {
		return new RequestParams(Map.of("page[limit]", "5"));
	}

	@Override
	public List<DataRecord> map(String json) {
		JsonNode root;
		try {
			root = objectMapper.readTree(json);
		} catch (JsonProcessingException e) {
			throw new ApiException("Kitsu parse error: " + e.getMessage(), e);
		}

		List<DataRecord> records = new ArrayList<>();
		for (JsonNode item : root.path("data")) {
			Map<String, Object> metrics = objectMapper.convertValue(item.path("attributes"), new TypeReference<>() {});

			records.add(new DataRecord(
				SOURCE_ID,
				item.path("id").asText(),
				Instant.now(),
				metrics));
		}
		return records;
	}
}