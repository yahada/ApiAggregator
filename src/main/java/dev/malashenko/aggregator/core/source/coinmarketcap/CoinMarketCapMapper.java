package dev.malashenko.aggregator.core.source.coinmarketcap;

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

public class CoinMarketCapMapper implements ApiMapper {
	private final ObjectMapper objectMapper;

	public CoinMarketCapMapper(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}

	@Override
	public List<DataRecord> map(String json) {
		JsonNode root = null;
		try {
			root = objectMapper.readTree(json);
		} catch (JsonProcessingException e) {
			throw new ApiException("CMC Problem with json-data parsing: " + e.getMessage(), e);
		}

		JsonNode status = root.path("status");
		String errorCode = status.path("error_code").asText("0");
		if (!"0".equals(errorCode)) {
			throw new ApiException("CMC API error " + errorCode + ": " + status.path("error_message").asText());
		}

		List<DataRecord> records = new ArrayList<>();
		for (JsonNode coin : root.path("data")) {
			Map<String, Object> metrics = new LinkedHashMap<>();

			for (JsonNode quote : coin.path("quotes")) {
				String currency = quote.path("symbol").asText().toLowerCase();
				metrics.put("price_" + currency, quote.path("price").asDouble());
			}

			if (metrics.isEmpty()) continue;

			Instant observedAt;
			try {
				observedAt = Instant.parse(status.path("timestamp").asText());
			} catch (DateTimeException e) {
				throw new ApiException("CMC bad timestamp: " + e.getMessage(), e);
			}

			records.add(new DataRecord(
				CoinMarketCapHttpApiSource.SOURCE_ID,
				EntityType.CRYPTOCURRENCY,
				coin.path("symbol").asText(),
				observedAt,
				metrics));
		}
		return records;
	}
}
