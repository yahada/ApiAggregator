package dev.malashenko.aggregator.core.aggregator;

import dev.malashenko.aggregator.core.exception.AppException;
import dev.malashenko.aggregator.core.model.DataRecord;
import dev.malashenko.aggregator.core.model.EntityType;
import dev.malashenko.aggregator.core.model.RequestParams;
import dev.malashenko.aggregator.core.source.ApiSource;
import dev.malashenko.aggregator.core.storage.DataStorage;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
public class Aggregator {

	private final DataStorage dataStorage;
	private final Map<EntityType, ApiSource> apiSourceMap;
	private final Path outputPath;

	public Aggregator(DataStorage dataStorage, Map<EntityType, ApiSource> apiSourceMap, Path outputPath) {
		this.dataStorage = Objects.requireNonNull(dataStorage, "dataStorage must not be null");
		this.apiSourceMap = Map.copyOf(apiSourceMap);
		this.outputPath = Objects.requireNonNull(outputPath, "outputPath must not be null");
	}

	public List<ApiSource> getApiSources() {
		return List.copyOf(apiSourceMap.values());
	}

	public void makeRequest(EntityType apiType, RequestParams params) {
		ApiSource apiSource = apiSourceMap.get(apiType);

		if (apiSource == null) {
			throw new AppException("API source is not configured: " + apiType);
		}

		log.info("Making request to {} with params: {}", apiType, params);
		List<DataRecord> records = apiSource.getApiRequest(params);
		log.info("Received {} records from {}", records.size(), apiType);

		dataStorage.saveData(records, outputPath);
		log.info("Saved {} records to {}", records.size(), outputPath);
	}

	public List<DataRecord> readData(EntityType type) {
		log.info("Reading data from {} for type: {}", outputPath, type);
		List<DataRecord> records = dataStorage.readData(outputPath);

		if (type == null) {
			log.info("Found {} total records", records.size());
			return records;
		}

		List<DataRecord> filtered = records.stream()
			   .filter(record -> record.entityType() == type)
			   .toList();
		log.info("Found {} records of type {}", filtered.size(), type);
		return filtered;
	}
}