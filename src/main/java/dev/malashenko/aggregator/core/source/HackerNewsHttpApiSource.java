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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class HackerNewsHttpApiSource extends AbstractHttpApiSource {
	private final static String ORIGIN_URL = "https://hn.algolia.com/api/v1/search";
	public final static String SOURCE_ID = "hackernews";

	private final ObjectMapper objectMapper;

	public HackerNewsHttpApiSource(HttpResponder responder) {
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
		return new RequestParams(Map.of("query", "java", "hitsPerPage", "100"));
	}

	@Override
	protected RequestParams enrich(RequestParams params) {
		return params;
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

			long createdAt = hit.path("created_at_i").asLong(0);
			Optional<Instant> observedAt = (createdAt == 0)
				? Optional.empty()
				: Optional.of(Instant.ofEpochSecond(createdAt));

			records.add(new DataRecord(
				SOURCE_ID,
				hit.path("objectID").asText(),
				observedAt,
				metrics));
		}
		return records;
	}
}
