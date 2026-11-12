package dev.malashenko.aggregator.core.storage.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.malashenko.aggregator.core.storage.DataStorage;
import dev.malashenko.aggregator.core.storage.DataStorageProvider;
import dev.malashenko.aggregator.core.storage.StorageFormat;

public class JsonDataStorageProvider implements DataStorageProvider {

	@Override
	public StorageFormat getFormat() {
		return StorageFormat.JSON;
	}

	@Override
	public DataStorage create(ObjectMapper objectMapper) {
		return new JsonDataStorage(objectMapper);
	}
}
