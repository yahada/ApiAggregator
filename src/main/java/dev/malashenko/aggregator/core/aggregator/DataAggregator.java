package dev.malashenko.aggregator.core.aggregator;

import dev.malashenko.aggregator.core.exception.AppException;
import dev.malashenko.aggregator.core.model.DataRecord;
import dev.malashenko.aggregator.core.model.RequestParams;
import dev.malashenko.aggregator.core.source.ApiSource;
import dev.malashenko.aggregator.core.storage.DataStorage;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
public class DataAggregator {

	private final DataStorage dataStorage;
	private final Map<String, ApiSource> apiSourceMap;
	private final Path outputPath;

	public DataAggregator(DataStorage dataStorage, Map<String, ApiSource> apiSourceMap, Path outputPath) {
		this.dataStorage = Objects.requireNonNull(dataStorage, "dataStorage must not be null");
		this.apiSourceMap = Map.copyOf(apiSourceMap);
		this.outputPath = Objects.requireNonNull(outputPath, "outputPath must not be null");
	}

	public void makeRequest(String sourceName, RequestParams params) {
		ApiSource apiSource = apiSourceMap.get(sourceName);

		if (apiSource == null) {
			throw new AppException("API source is not configured: " + sourceName);
		}

		log.info("Making request to {} with params: {}", sourceName, params);
		List<DataRecord> records = apiSource.getApiRequest(params);
		log.info("Received {} records from {}", records.size(), sourceName);

		dataStorage.saveData(records, outputPath);
		log.info("Saved {} records to {}", records.size(), outputPath);
	}
}
