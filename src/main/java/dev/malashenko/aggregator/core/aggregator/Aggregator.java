package dev.malashenko.aggregator.core.aggregator;

import dev.malashenko.aggregator.core.exception.AppException;
import dev.malashenko.aggregator.core.model.DataRecord;
import dev.malashenko.aggregator.core.model.RequestParams;
import dev.malashenko.aggregator.core.source.ApiSource;
import dev.malashenko.aggregator.core.storage.DataStorage;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
public class Aggregator {

	private final DataStorage dataStorage;
	private final Map<String, ApiSource> apiSourceMap;

	public Aggregator(DataStorage dataStorage, Map<String, ApiSource> apiSourceMap) {
		this.dataStorage = Objects.requireNonNull(dataStorage, "dataStorage must not be null");
		this.apiSourceMap = apiSourceMap != null ? Map.copyOf(apiSourceMap) : null;
	}

	public void makeRequest(String sourceName, RequestParams params) {
		if (apiSourceMap == null) {
			throw new AppException("Aggregator is not configured for data requests");
		}

		ApiSource apiSource = apiSourceMap.get(sourceName);
		if (apiSource == null) {
			throw new AppException("API source is not configured: " + sourceName);
		}

		log.info("Making request to {} with params: {}", sourceName, params);
		List<DataRecord> records = apiSource.getApiRequest(params);
		log.info("Received {} records from {}", records.size(), sourceName);

 	dataStorage.saveData(records);
		log.info("Saved {} records", records.size());
	}

	public List<DataRecord> readData(String sourceName) {
		if (sourceName == null) {
			List<DataRecord> records = dataStorage.readData();
			log.info("Found {} total records", records.size());
			return records;
		}

		List<DataRecord> filtered = dataStorage.readData(sourceName);
		log.info("Found {} records from source {}", filtered.size(), sourceName);
		return filtered;
	}
}
