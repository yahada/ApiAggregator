package dev.malashenko.aggregator.core.aggregator;

import dev.malashenko.aggregator.core.exception.ApiException;
import dev.malashenko.aggregator.core.exception.AppException;
import dev.malashenko.aggregator.core.exception.HttpException;
import dev.malashenko.aggregator.core.exception.StorageException;
import dev.malashenko.aggregator.core.model.DataRecord;
import dev.malashenko.aggregator.core.model.RequestParams;
import dev.malashenko.aggregator.core.source.ApiSource;
import dev.malashenko.aggregator.core.storage.DataStorage;
import lombok.extern.slf4j.Slf4j;

import java.time.format.DateTimeParseException;
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

	public void makeRequest() {
		if (apiSourceMap == null) {
			throw new AppException("Aggregator is not configured for data requests");
		}

		for (Map.Entry<String, ApiSource> entry : apiSourceMap.entrySet()) {
			String name = entry.getKey();
			ApiSource source = entry.getValue();
			RequestParams params = source.defaultParams();

			try {
				log.info("Making request to {} with params: {}", name, params);
				List<DataRecord> records = source.getApiRequest(params);
				log.info("Received {} records from {}", records.size(), name);

				dataStorage.saveData(records);
				log.info("Saved {} records from {}", records.size(), name);
			} catch (HttpException e) {
				log.error("Network error while polling {}: {}", name, e.getMessage());
			} catch (ApiException e) {
				log.error("Source error while polling {}: {}", name, e.getMessage());
			} catch (StorageException e) {
				log.error("Storage error while polling {}: {}", name, e.getMessage());
			} catch (AppException e) {
				log.error("Application error while polling {}: {}", name, e.getMessage());
			} catch (DateTimeParseException e) {
				log.error("Date parse error while polling {}: {}", name, e.getMessage());
			} catch (RuntimeException e) {
				log.error("Unexpected error while polling {}: [{}] {}", name, e.getClass().getSimpleName(), e.getMessage());
			}
		}
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
