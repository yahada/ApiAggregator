package dev.malashenko.aggregator.core.source.kitsu;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.malashenko.aggregator.core.exception.ApiException;
import dev.malashenko.aggregator.core.model.DataRecord;
import dev.malashenko.aggregator.core.model.EntityType;
import dev.malashenko.aggregator.core.source.ApiMapper;

import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class KitsuMapper implements ApiMapper {

	private final ObjectMapper objectMapper;

	public KitsuMapper(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
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
			// ВСЕ поля из attributes, вложенность сохранена
			Map<String, Object> metrics = objectMapper.convertValue(
				item.path("attributes"),
				new TypeReference<Map<String, Object>>() {});

			// entityId = data[].id
			String entityId = item.path("id").asText();

			// observedAt: updatedAt из attributes, если есть; иначе now
			Instant observedAt = parseInstant(metrics.get("updatedAt"));

			records.add(new DataRecord(
				KitsuHttpApiSource.SOURCE_ID,
				EntityType.ANIME,
				entityId,
				observedAt,
				metrics));
		}
		return records;
	}

	private Instant parseInstant(Object value) {
		if (value == null) {
			return Instant.now();
		}
		try {
			return Instant.parse(value.toString());
		} catch (DateTimeParseException e) {
			return Instant.now();
		}
	}
}