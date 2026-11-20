package dev.malashenko.aggregator.source;

import lombok.extern.slf4j.Slf4j;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.malashenko.aggregator.http.HttpResponder;
import dev.malashenko.aggregator.model.DataRecord;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class EarthquakeUsgsHttpApiSource implements ApiSource {

	private static final String BASE_URL = "https://earthquake.usgs.gov/fdsnws/event/1/query?format=geojson";
	private final ObjectMapper objectMapper;
	private final HttpResponder httpResponder;

	public EarthquakeUsgsHttpApiSource(HttpResponder httpResponder, ObjectMapper objectMapper) {
		this.httpResponder = httpResponder;
		this.objectMapper = objectMapper;
	}

	@Override
	public Map<String, String> defaultParams() {
		return Map.of("limit", "5", "orderby", "time");
	}

	@Override
	public List<DataRecord> fetch(Map<String, String> parameters) throws IOException, InterruptedException {
		StringBuilder url = new StringBuilder(BASE_URL);
		String separator = "&";
		for (Map.Entry<String, String> entry : parameters.entrySet()) {
			url.append(separator).append(encode(entry.getKey())).append("=").append(encode(entry.getValue()));
			separator = "&";
		}

		log.debug("Fetching USGS data from: {}", url);
		String json = httpResponder.get(url.toString());
		return map(json);
	}

	private String encode(String value) {
		return URLEncoder.encode(value, StandardCharsets.UTF_8);
	}


	@Override
	public List<DataRecord> map(String json) throws IOException {
		JsonNode root = objectMapper.readTree(json);

		List<DataRecord> records = new ArrayList<>();
		for (JsonNode feature : root.path("features")) {
			JsonNode props = feature.path("properties");
			String entityId = feature.path("id").asText("unknown");
			Instant timestamp = Instant.ofEpochMilli(props.path("time").asLong(0));

			Map<String, Object> metrics = new HashMap<>();
			metrics.put("place", props.path("place").asText("unknown"));
			if (props.path("mag").isNumber()) {
				metrics.put("magnitude", props.path("mag").asDouble());
			}
			metrics.put("type", props.path("type").asText("earthquake"));

			records.add(new DataRecord("usgs", entityId, timestamp, metrics));
		}
		log.info("Mapped {} USGS earthquake records", records.size());
		return records;
	}


}
