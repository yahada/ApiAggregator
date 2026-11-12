package dev.malashenko.aggregator.core.storage;

import com.fasterxml.jackson.databind.ObjectMapper;

public interface DataStorageProvider {

	StorageFormat getFormat();

	DataStorage create(ObjectMapper objectMapper);
}
