package dev.malashenko.aggregator.core.source.earthquake.usgs;

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

public class EarthquakeUsgsMapper implements ApiMapper {

	private final ObjectMapper objectMapper;

	public EarthquakeUsgsMapper(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}

	@Override
	public List<DataRecord> map(String json) {
		JsonNode root;
		try {
			root = objectMapper.readTree(json);
		} catch (JsonProcessingException e) {
			throw new ApiException("USGS parse error: " + e.getMessage(), e);
		}

		List<DataRecord> records = new ArrayList<>();
		for (JsonNode feature : root.path("features")) {
			Map<String, Object> metrics = objectMapper.convertValue(feature, new TypeReference<>() {});

			long timeMillis = feature.path("properties").path("time").asLong(0);
			Optional<Instant> observedAt = (timeMillis == 0)
				? Optional.empty()
				: Optional.of(Instant.ofEpochMilli(timeMillis));

			records.add(new DataRecord(
				EarthquakeUsgsHttpApiSource.SOURCE_ID,
				EntityType.EARTHQUAKE,
				feature.path("id").asText(),
				observedAt,
				metrics));
		}
		return records;
	}
}