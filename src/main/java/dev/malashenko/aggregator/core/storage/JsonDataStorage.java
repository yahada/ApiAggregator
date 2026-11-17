package dev.malashenko.aggregator.core.storage;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.malashenko.aggregator.core.exception.StorageException;
import dev.malashenko.aggregator.core.model.DataRecord;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class JsonDataStorage implements DataStorage {

	private final Path file;
	private final ObjectMapper objectMapper;

	public JsonDataStorage(Path file, ObjectMapper objectMapper) {
		this.file = file;
		this.objectMapper = objectMapper;
	}

	@Override
	public synchronized void saveData(List<DataRecord> data) {
		if (data == null || data.isEmpty()) {
			return;
		}

		log.info("Saving {} records to JSON: {}", data.size(), file);

		List<DataRecord> toWrite = new ArrayList<>();
		toWrite.addAll(readInternal());
		toWrite.addAll(data);

		try {
			objectMapper.writerWithDefaultPrettyPrinter().writeValue(file.toFile(), toWrite);
		} catch (IOException e) {
			throw new StorageException("Failed to write JSON to " + file + ": " + e.getMessage(), e);
		}
	}

	@Override
	public List<DataRecord> readData() {
		if (!Files.exists(file)) {
			log.info("JSON file does not exist: {}", file);
			return new ArrayList<>();
		}

		log.info("Reading JSON from: {}", file);
		return readInternal();
	}

	@Override
	public List<DataRecord> readData(String source) {
		return readData().stream().filter(dataRecord -> dataRecord.source().equals(source)).toList();
	}

	private List<DataRecord> readInternal() {
		try {
			if (!Files.exists(file) || Files.size(file) == 0) {
				return new ArrayList<>();
			}
			return objectMapper.readValue(file.toFile(), new TypeReference<>() {});
		} catch (IOException e) {
			throw new StorageException("Failed to read JSON from " + file + ": " + e.getMessage(), e);
		}
	}
}
