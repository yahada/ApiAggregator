package dev.malashenko.aggregator.core.storage;

import com.fasterxml.jackson.databind.ObjectMapper;

public class DataStorageFactory {

	public static DataStorage getDataStorage(StorageFormat format, ObjectMapper om) {
		return DataStorageRegistry.findAll().stream()
			.filter(provider -> (provider.getFormat() == format))
			.map(provider -> provider.create(om))
			.findFirst()
			.orElseThrow(() -> new IllegalArgumentException("Unsupported storage format: " + format));
	}

}


