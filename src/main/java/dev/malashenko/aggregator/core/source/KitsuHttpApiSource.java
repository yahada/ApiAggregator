package dev.malashenko.aggregator.core.source;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.malashenko.aggregator.core.exception.ApiException;
import dev.malashenko.aggregator.core.http.HttpResponder;
import dev.malashenko.aggregator.core.model.DataRecord;
import dev.malashenko.aggregator.core.model.RequestParams;
import dev.malashenko.aggregator.core.tools.ObjectMapperBuilder;

import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class KitsuHttpApiSource extends AbstractHttpApiSource {

	public static final String SOURCE_ID = "kitsu";
	private static final String ORIGIN_URL = "https://kitsu.io/api/edge/anime";

	private final ObjectMapper objectMapper;

	public KitsuHttpApiSource(HttpResponder responder) {
		super(responder);
		this.objectMapper = ObjectMapperBuilder.buildDefault();
	}

	@Override
	protected String originUrl() {
		return ORIGIN_URL;
	}

	@Override
	public String id() {
		return SOURCE_ID;
	}

	@Override
	public RequestParams defaultParams() {
		return new RequestParams(Map.of("page[limit]", "5"));
	}

	@Override
	protected RequestParams enrich(RequestParams params) {
		Map<String, String> m = new LinkedHashMap<>(params.queryParams());
		m.putIfAbsent("page[limit]", "10");
		return new RequestParams(m);
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

			String entityId = item.path("id").asText();

			Optional<Instant> observedAt = parseInstant(metrics.get("updatedAt"));

			records.add(new DataRecord(SOURCE_ID, entityId, observedAt, metrics));
		}
		return records;
	}

	private Optional<Instant> parseInstant(Object value) {
		if (value == null) {
			return Optional.empty();
		}
		try {
			return Optional.of(Instant.parse(value.toString()));
		} catch (DateTimeParseException e) {
			return Optional.empty();
		}
	}
}