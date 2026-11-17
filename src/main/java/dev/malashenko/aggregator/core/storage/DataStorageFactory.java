package dev.malashenko.aggregator.core.storage;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.malashenko.aggregator.core.model.StorageFormat;
import dev.malashenko.aggregator.core.tools.ObjectMapperBuilder;

import java.nio.file.Path;

public final class DataStorageFactory {
	private static final ObjectMapper objectMapper = ObjectMapperBuilder.buildDefault();
	private DataStorageFactory() {}

	public static DataStorage getDataStorage(StorageFormat format, Path file) {
		return switch (format) {
			case JSON -> new JsonDataStorage(file, objectMapper);
			case CSV -> new CsvDataStorage(file, objectMapper);
		};
	}
}


