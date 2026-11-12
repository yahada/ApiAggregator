
package dev.malashenko.aggregator.core.storage.csv;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.malashenko.aggregator.core.exception.StorageException;
import dev.malashenko.aggregator.core.model.DataRecord;
import dev.malashenko.aggregator.core.model.EntityType;
import dev.malashenko.aggregator.core.storage.DataStorage;
import lombok.extern.slf4j.Slf4j;
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
import java.util.Optional;

@Slf4j
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
		log.info("Saving {} records to CSV: {}", data.size(), path);
		try {
			Path parent = path.toAbsolutePath().getParent();
			Files.createDirectories(parent);

			boolean writeHeader = !Files.exists(path) || Files.size(path) == 0;

			CSVFormat format = CSVFormat.DEFAULT
				                   .builder()
				                   .setHeader(writeHeader ? HEADER : null)
				                   .build();

			try (Writer writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
			     CSVPrinter printer = new CSVPrinter(writer, format)) {


				for (DataRecord record : data) {
					printer.printRecord(
						record.source(),
						record.entityType().name(),
						record.entityId(),
						record.observedAt().map(Instant::toString).orElse(""),
						objectMapper.writeValueAsString(record.metrics())
					);
				}
			}

		} catch (IOException e) {
			log.error("Failed to write CSV to {}", path, e);
			throw new StorageException("Failed to write CSV to " + path + ": " + e.getMessage(), e);
		}
	}

	@Override
	public List<DataRecord> readData(Path path) {
		if (!Files.exists(path)) {
			log.info("CSV file does not exist: {}", path);
			return new ArrayList<>();
		}

		log.info("Reading CSV from: {}", path);
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
					new TypeReference<>() {
					}
				);

				String observedAtStr = row.get("observed_at");
				Optional<Instant> observedAt = observedAtStr.isEmpty()
					? Optional.empty()
					: Optional.of(Instant.parse(observedAtStr));

				records.add(new DataRecord(
					row.get("source"),
					EntityType.valueOf(row.get("entity_type")),
					row.get("entity_id"),
					observedAt,
					metrics
				));
			}

			return records;

		} catch (IOException | IllegalArgumentException e) {
			log.error("Failed to read CSV from {}", path, e);
			throw new StorageException(
				"Failed to read CSV from " + path + ": " + e.getMessage(), e);
		}
	}
}
