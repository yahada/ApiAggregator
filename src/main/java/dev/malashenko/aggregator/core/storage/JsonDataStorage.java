
package dev.malashenko.aggregator.core.storage;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.malashenko.aggregator.core.exception.StorageException;
import dev.malashenko.aggregator.core.model.DataRecord;
import dev.malashenko.aggregator.core.model.EntityType;

import java.io.IOException;
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
			Path parent = path.toAbsolutePath().getParent();
			Files.createDirectories(parent);

			List<DataRecord> toWrite;

			switch (mode) {
				case NEW -> toWrite = new ArrayList<>(data);

				case APPEND -> {
					toWrite = Files.exists(path)
						          ? new ArrayList<>(readData(path))
						          : new ArrayList<>();

					toWrite.addAll(data);
				}

				default -> throw new IllegalStateException(
					"Unsupported write mode: " + mode
				);
			}

			objectMapper.writerWithDefaultPrettyPrinter()
				.writeValue(path.toFile(), toWrite);

		} catch (IOException e) {
			throw new StorageException(
				"Failed to write JSON to " + path + ": " + e.getMessage(), e
			);
		}
	}

	@Override
	public List<DataRecord> readData(Path path) {
		if (!Files.exists(path)) {
			return new ArrayList<>();
		}

		try {
			return objectMapper.readValue(
				path.toFile(),
				new TypeReference<List<DataRecord>>() {}
			);

		} catch (IOException e) {
			throw new StorageException(
				"Failed to read JSON from " + path + ": " + e.getMessage(), e
			);
		}
	}

	@Override
	public List<DataRecord> readByApi(EntityType apiType, Path path) {
		return readData(path).stream()
			       .filter(record -> record.entityType() == apiType)
			       .toList();
	}

	@Override
	public StorageFormat getStorageFormat() {
		return StorageFormat.JSON;
	}
}
