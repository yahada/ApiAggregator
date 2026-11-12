package dev.malashenko.aggregator.core.storage.csv;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.malashenko.aggregator.core.storage.DataStorage;
import dev.malashenko.aggregator.core.storage.DataStorageProvider;
import dev.malashenko.aggregator.core.storage.StorageFormat;

public class CsvDataStorageProvider implements DataStorageProvider {

	@Override
	public StorageFormat getFormat() {
		return StorageFormat.CSV;
	}

	@Override
	public DataStorage create(ObjectMapper objectMapper) {
		return new CsvDataStorage(objectMapper);
	}
}
