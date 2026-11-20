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
public class KitsuHttpApiSource implements ApiSource {

	private static final String BASE_URL = "https://kitsu.io/api/edge/anime";
	private final HttpResponder httpResponder;
	private final ObjectMapper objectMapper;

	public KitsuHttpApiSource(HttpResponder httpResponder, ObjectMapper objectMapper) {
		this.httpResponder = httpResponder;
		this.objectMapper = objectMapper;
	}

	@Override
	public Map<String, String> defaultParams() {
		return Map.of("page[limit]", "5");
	}

	@Override
	public List<DataRecord> fetch(Map<String, String> parameters) throws IOException, InterruptedException {
		StringBuilder url = new StringBuilder(BASE_URL);
		String separator = "?";
		for (Map.Entry<String, String> entry : parameters.entrySet()) {
			url.append(separator).append(encode(entry.getKey())).append("=").append(encode(entry.getValue()));
			separator = "&";
		}

		log.debug("Fetching Kitsu data from: {}", url);
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
		for (JsonNode item : root.path("data")) {
			String entityId = item.path("id").asText("unknown");

			JsonNode attrs = item.path("attributes");
			Instant timestamp = Instant.parse(attrs.path("createdAt").asText(Instant.now().toString()));

			Map<String, Object> metrics = new HashMap<>();

			metrics.put("title", attrs.path("titles").path("en").asText("unknown"));

			if (attrs.path("averageRating").isNumber()) {
				metrics.put("averageRating", attrs.path("averageRating").asDouble());
			}

			metrics.put("status", attrs.path("status").asText(""));

			metrics.put("episodeCount", attrs.path("episodeCount").asInt(0));

			records.add(new DataRecord("kitsu", entityId, timestamp, metrics));
		}
		log.info("Mapped {} Kitsu anime records", records.size());
		return records;
	}

}