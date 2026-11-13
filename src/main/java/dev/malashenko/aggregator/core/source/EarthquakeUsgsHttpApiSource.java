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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class EarthquakeUsgsHttpApiSource extends AbstractHttpApiSource {
	private final static String ORIGIN_URL = "https://earthquake.usgs.gov/fdsnws/event/1/query";
	public final static String SOURCE_ID = "usgs";

	private final ObjectMapper objectMapper;

	public EarthquakeUsgsHttpApiSource(HttpResponder responder) {
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
		return new RequestParams(Map.of("limit", "5", "orderby", "time"));
	}

	@Override
	protected RequestParams enrich(RequestParams params) {
		Map<String, String> enriched = new LinkedHashMap<>(params.queryParams());
		enriched.put("format", "geojson");
		return new RequestParams(enriched);
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
				SOURCE_ID,
				feature.path("id").asText(),
				observedAt,
				metrics));
		}
		return records;
	}
}
