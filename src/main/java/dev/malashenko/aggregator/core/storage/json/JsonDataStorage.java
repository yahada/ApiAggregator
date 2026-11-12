
package dev.malashenko.aggregator.core.storage.json;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.malashenko.aggregator.core.exception.StorageException;
import dev.malashenko.aggregator.core.model.DataRecord;
import dev.malashenko.aggregator.core.storage.DataStorage;
import dev.malashenko.aggregator.core.storage.StorageFormat;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class JsonDataStorage implements DataStorage {

	private final ObjectMapper objectMapper;

	public JsonDataStorage(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}

	@Override
	public void saveData(List<DataRecord> data, Path path) {
		log.info("Saving {} records to JSON: {}", data.size(), path);
		try {
			Path parent = path.toAbsolutePath().getParent();
			Files.createDirectories(parent);

			List<DataRecord> toWrite = new ArrayList<>();

			if (Files.exists(path) && Files.size(path) > 0) {
				toWrite.addAll(readData(path));
			}

			toWrite.addAll(data);

			objectMapper.writerWithDefaultPrettyPrinter()
				.writeValue(path.toFile(), toWrite);

		} catch (IOException e) {
			log.error("Failed to write JSON to {}", path, e);
			throw new StorageException(
				"Failed to write JSON to " + path + ": " + e.getMessage(), e
			);
		}
	}


	@Override
	public List<DataRecord> readData(Path path) {
		if (!Files.exists(path)) {
			log.info("JSON file does not exist: {}", path);
			return new ArrayList<>();
		}

		log.info("Reading JSON from: {}", path);
		try {
			return objectMapper.readValue(
				path.toFile(),
				new TypeReference<List<DataRecord>>() {}
			);

		} catch (IOException e) {
			log.error("Failed to read JSON from {}", path, e);
			throw new StorageException(
				"Failed to read JSON from " + path + ": " + e.getMessage(), e
			);
		}
	}

}
