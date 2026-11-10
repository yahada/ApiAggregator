package dev.malashenko.aggregator.core.storage;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.malashenko.aggregator.core.exception.StorageException;
import dev.malashenko.aggregator.core.model.DataRecord;
import dev.malashenko.aggregator.core.model.EntityType;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class JsonDataStorage implements DataStorage {
	private final ObjectMapper objectMapper;


	public JsonDataStorage(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}


	@Override
	public void saveData(List<DataRecord> data, WriteMode mode, Path path) {

		try {
			List<DataRecord> toWrite = switch (mode) {
				case NEW -> new ArrayList<>(data);
				case APPEND -> {
					List<DataRecord> existing = Files.exists(path) ? readData(path) : new ArrayList<>();
					existing.addAll(data);
					yield existing;
				}
			};

			objectMapper.writerWithDefaultPrettyPrinter().writeValue(path.toFile(), toWrite);
 		} catch (IOException e) {
			throw new StorageException("Failed to write JSON to " + path + ": " + e.getMessage(), e);
		}
	}

	@Override
	public StorageFormat getStorageFormat() {
		return StorageFormat.JSON;
	}

	@Override
	public List<DataRecord> readData(Path path) {
		if (!Files.exists(path)) {
			return new ArrayList<>();
		}

		try {
			return objectMapper.readValue(path.toFile(), new TypeReference<List<DataRecord>>() {});
		} catch (IOException e) {
			throw new StorageException("Failed to read JSON from " + path + ": " + e.getMessage(), e);
		}
	}

	@Override
	public List<DataRecord> readByApi(EntityType apiType, Path path) {
		return readData(path).stream().filter(dataRecord -> dataRecord.entityType().equals(apiType)).toList();
	}
}
