package dev.malashenko.aggregator.core.aggregator;

import dev.malashenko.aggregator.core.model.DataRecord;
import dev.malashenko.aggregator.core.model.EntityType;
import dev.malashenko.aggregator.core.model.RequestParams;
import dev.malashenko.aggregator.core.source.ApiSource;
import dev.malashenko.aggregator.core.storage.DataStorage;
import dev.malashenko.aggregator.core.storage.WriteMode;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class Aggregator {

	private final DataStorage dataStorage;
	private final WriteMode writeMode;
	private final Map<EntityType, ApiSource> apiSourceMap;
	private final Path outputPath;

	Aggregator(
		DataStorage dataStorage,
		WriteMode writeMode,
		Map<EntityType, ApiSource> apiSourceMap,
		Path outputPath
	) {
		this.dataStorage = Objects.requireNonNull(dataStorage);
		this.writeMode = Objects.requireNonNull(writeMode);
		this.apiSourceMap = Map.copyOf(apiSourceMap);
		this.outputPath = Objects.requireNonNull(outputPath);
	}

	public List<ApiSource> getApiSources() {
		return List.copyOf(apiSourceMap.values());
	}

	public void makeRequest(EntityType apiType, RequestParams params) {
		ApiSource apiSource = apiSourceMap.get(apiType);

		if (apiSource == null) {
			throw new IllegalArgumentException(
				"API source is not configured: " + apiType
			);
		}

		List<DataRecord> records = apiSource.getApiRequest(params);

		dataStorage.saveData(records, writeMode, outputPath);
	}

	public void readData(Optional<EntityType> apiType) {
		Objects.requireNonNull(apiType);

		List<DataRecord> records = dataStorage.readData(outputPath);

		if (apiType.isPresent()) {
			EntityType type = apiType.get();

			records = records.stream()
				          .filter(record -> record.entityType() == type)
				          .toList();
		}

		records.forEach(System.out::println);
	}
}