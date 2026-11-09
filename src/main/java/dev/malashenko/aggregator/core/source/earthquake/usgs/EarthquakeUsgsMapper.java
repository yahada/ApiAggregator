package dev.malashenko.aggregator.core.source.earthquake.usgs;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.malashenko.aggregator.core.exception.ApiException;
import dev.malashenko.aggregator.core.model.DataRecord;
import dev.malashenko.aggregator.core.model.EntityType;
import dev.malashenko.aggregator.core.source.ApiMapper;

import java.time.DateTimeException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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

		int httpStatus = root.path("metadata").path("status").asInt(200);
		if (httpStatus != 200) {
			throw new ApiException("USGS API error " + httpStatus);
		}

		List<DataRecord> records = new ArrayList<>();
		for (JsonNode feature : root.path("features")) {
			JsonNode props = feature.path("properties");
			JsonNode coords = feature.path("geometry").path("coordinates");

			Map<String, Object> metrics = new LinkedHashMap<>();
			metrics.put("magnitude", props.path("mag").asDouble());
			metrics.put("place", props.path("place").asText());
			metrics.put("tsunami", props.path("tsunami").asInt());
			metrics.put("significance", props.path("sig").asInt());
			metrics.put("longitude", coords.path(0).asDouble());
			metrics.put("latitude", coords.path(1).asDouble());
			metrics.put("depth_km", coords.path(2).asDouble());

			putIfPresent(metrics, "felt", props, "felt");
			putIfPresent(metrics, "mag_type", props, "magType");
			putIfPresent(metrics, "status", props, "status");

			if (metrics.isEmpty()) continue;

			Instant observedAt;
			try {
				observedAt = Instant.ofEpochMilli(props.path("time").asLong());
			} catch (DateTimeException e) {
				throw new ApiException("CMC bad timestamp: " + e.getMessage(), e);
			}
			records.add(new DataRecord(
				EarthquakeUsgsHttpApiSource.SOURCE_ID,
				EntityType.EARTHQUAKE,
				feature.path("id").asText(),
				observedAt,
				metrics));
		}
		return records;
	}

	private static void putIfPresent(Map<String, Object> metrics, String key, JsonNode node, String field) {
		JsonNode value = node.path(field);
		if (value.isMissingNode() || value.isNull()) {
			return;
		}
		if (value.isNumber()) {
			metrics.put(key, value.asInt());
		} else {
			metrics.put(key, value.asText());
		}
	}
}