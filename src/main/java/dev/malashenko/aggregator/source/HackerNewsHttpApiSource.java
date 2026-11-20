package dev.malashenko.aggregator.source;

import lombok.extern.slf4j.Slf4j;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.malashenko.aggregator.http.HttpResponder;
import dev.malashenko.aggregator.model.DataRecord;


import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class HackerNewsHttpApiSource implements ApiSource {

	private static final String BASE_URL = "https://hn.algolia.com/api/v1/search";
	private final ObjectMapper objectMapper;
	private final HttpResponder httpResponder;

	public HackerNewsHttpApiSource(HttpResponder httpResponder, ObjectMapper objectMapper) {
		this.httpResponder = httpResponder;
		this.objectMapper = objectMapper;
	}

	@Override
	public Map<String, String> defaultParams() {
		return Map.of("query", "java", "hitsPerPage", "100");
	}

	@Override
	public List<DataRecord> fetch(Map<String, String> parameters) throws IOException, InterruptedException {
		String url = UrlBuilder.build(BASE_URL, parameters);
		log.debug("Fetching HackerNews data from: {}", url);
		String json = httpResponder.get(url);
		return map(json);
	}

	@Override
	public List<DataRecord> map(String json) throws IOException {
		JsonNode root = objectMapper.readTree(json);

		List<DataRecord> records = new ArrayList<>();
		for (JsonNode hit : root.path("hits")) {
			String entityId = hit.path("objectID").asText("unknown");
			Instant timestamp = Instant.ofEpochSecond(hit.path("created_at_i").asLong(0));

			Map<String, Object> metrics = new HashMap<>();
			metrics.put("title", hit.path("title").asText("unknown"));
			if (hit.path("points").isNumber()) {
				metrics.put("points", hit.path("points").asInt());
			}
			metrics.put("url", hit.path("url").asText(""));
			metrics.put("num_comments", hit.path("num_comments").asInt(0));

			records.add(new DataRecord("hackernews", entityId, timestamp, metrics));
		}
		log.info("Mapped {} HackerNews records", records.size());
		return records;
	}
}
