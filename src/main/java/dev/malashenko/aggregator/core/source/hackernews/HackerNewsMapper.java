package dev.malashenko.aggregator.core.source.hackernews;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.malashenko.aggregator.core.exception.ApiException;
import dev.malashenko.aggregator.core.model.DataRecord;
import dev.malashenko.aggregator.core.model.EntityType;
import dev.malashenko.aggregator.core.source.ApiMapper;

import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class HackerNewsMapper implements ApiMapper {


	private final ObjectMapper objectMapper;

	public HackerNewsMapper(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}

	@Override
	public List<DataRecord> map(String json) {
		JsonNode root = null;
		try {
			root = objectMapper.readTree(json);
		} catch (JsonProcessingException e) {
			throw new ApiException("HN parse error: " + e.getMessage(), e);
		}

		List<DataRecord> records = new ArrayList<>();
		for (JsonNode hit : root.path("hits")) {
			String objectId = hit.path("objectID").asText();

			Map<String, Object> metrics = new LinkedHashMap<>();
			metrics.put("title", hit.path("title").asText());
			metrics.put("url", hit.path("url").asText());
			metrics.put("author", hit.path("author").asText());
			metrics.put("points", hit.path("points").asInt());
			metrics.put("num_comments", hit.path("num_comments").asInt());

			long createdAtEpoch = hit.path("created_at_i").asLong(0);
			Instant observedAt = createdAtEpoch > 0
				                     ? Instant.ofEpochSecond(createdAtEpoch)
				                     : Instant.EPOCH;

			JsonNode highlight = hit.path("_highlightResult");
			if (!highlight.isMissingNode() && !highlight.isNull()) {
				metrics.put("highlight_title", highlight.path("title").path("value").asText());
				metrics.put("highlight_url", highlight.path("url").path("value").asText());
			}

			records.add(new DataRecord(
				HackerNewsHttpApiSource.SOURCE_ID,
				EntityType.NEWS,
				objectId,
				observedAt,
				metrics));
		}
		return records;
	}
}