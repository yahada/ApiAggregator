package dev.malashenko.aggregator.core.source.hackernews;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.malashenko.aggregator.core.exception.ApiException;
import dev.malashenko.aggregator.core.model.DataRecord;
import dev.malashenko.aggregator.core.model.EntityType;
import dev.malashenko.aggregator.core.source.ApiMapper;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class HackerNewsMapper implements ApiMapper {

	private final ObjectMapper objectMapper;

	public HackerNewsMapper(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
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
			Map<String, Object> metrics = objectMapper.convertValue(hit, new TypeReference<Map<String, Object>>() {});

			long createdAt = hit.path("created_at_i").asLong(0);
			Optional<Instant> observedAt = (createdAt == 0)
				? Optional.empty()
				: Optional.of(Instant.ofEpochSecond(createdAt));

			records.add(new DataRecord(
				HackerNewsHttpApiSource.SOURCE_ID,
				EntityType.NEWS,
				hit.path("objectID").asText(),
				observedAt,
				metrics));
		}
		return records;
	}
}