
package dev.malashenko.aggregator.core.storage;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.malashenko.aggregator.core.exception.StorageException;
import dev.malashenko.aggregator.core.model.DataRecord;
import dev.malashenko.aggregator.core.model.EntityType;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CsvDataStorage implements DataStorage {

	private static final String[] HEADER = {
		"source", "entity_type", "entity_id", "observed_at", "metrics"
	};

	private final ObjectMapper objectMapper;

	public CsvDataStorage(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}

	@Override
	public void saveData(List<DataRecord> data, Path path) {
		try {
			Path parent = path.toAbsolutePath().getParent();
			Files.createDirectories(parent);

			boolean writeHeader = !Files.exists(path) || Files.size(path) == 0;

			CSVFormat format = CSVFormat.DEFAULT.builder()
				                   .setHeader(writeHeader ? HEADER : null)
				                   .build();

			try (Writer writer = Files.newBufferedWriter(
				path,
				StandardCharsets.UTF_8,
				StandardOpenOption.CREATE,
				StandardOpenOption.APPEND
			);
			     CSVPrinter printer = new CSVPrinter(writer, format)) {

				for (DataRecord record : data) {
					printer.printRecord(
						record.source(),
						record.entityType().name(),
						record.entityId(),
						record.observedAt().toString(),
						objectMapper.writeValueAsString(record.metrics())
					);
				}
			}

		} catch (IOException e) {
			throw new StorageException(
				"Failed to write CSV to " + path + ": " + e.getMessage(), e
			);
		}
	}

	@Override
	public List<DataRecord> readData(Path path) {
		if (!Files.exists(path)) {
			return new ArrayList<>();
		}

		List<DataRecord> records = new ArrayList<>();

		try (Reader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8);
		     CSVParser parser = CSVFormat.DEFAULT.builder()
			                        .setHeader()
			                        .setSkipHeaderRecord(true)
			                        .build()
			                        .parse(reader)) {

			for (CSVRecord row : parser) {
				Map<String, Object> metrics = objectMapper.readValue(
					row.get("metrics"),
					new TypeReference<Map<String, Object>>() {}
				);

				records.add(new DataRecord(
					row.get("source"),
					EntityType.valueOf(row.get("entity_type")),
					row.get("entity_id"),
					Instant.parse(row.get("observed_at")),
					metrics
				));
			}

			return records;

		} catch (IOException | IllegalArgumentException e) {
			throw new StorageException(
				"Failed to read CSV from " + path + ": " + e.getMessage(), e
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
		return StorageFormat.CSV;
	}
}
