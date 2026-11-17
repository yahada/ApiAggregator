package dev.malashenko.aggregator.core.storage;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.malashenko.aggregator.core.exception.StorageException;
import dev.malashenko.aggregator.core.model.DataRecord;
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
		"source", "entity_id", "observed_at", "metrics"
	};

	private final Path file;
	private final ObjectMapper objectMapper;

	public CsvDataStorage(Path file, ObjectMapper objectMapper) {
		this.file = file;
		this.objectMapper = objectMapper;
	}

	@Override
	public synchronized void saveData(List<DataRecord> data) {
		if (data == null || data.isEmpty()) {
			return;
		}

		log.info("Saving {} records to CSV: {}", data.size(), file);

		try {
			List<DataRecord> toWrite = new ArrayList<>();
			toWrite.addAll(readInternal());
			toWrite.addAll(data);

			CSVFormat format = CSVFormat.DEFAULT
				.builder()
				.setHeader(HEADER)
				.build();

			try (Writer writer = Files.newBufferedWriter(file, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
			     CSVPrinter printer = new CSVPrinter(writer, format)) {

				for (DataRecord record : toWrite) {
					printer.printRecord(
						record.source(),
						record.entityId(),
						record.observedAt().map(Instant::toString).orElse(""),
						objectMapper.writeValueAsString(record.metrics())
					);
				}
			}

		} catch (IOException e) {
			throw new StorageException("Failed to write CSV to " + file + ": " + e.getMessage(), e);
		}
	}

	@Override
	public List<DataRecord> readData() {
		if (!Files.exists(file)) {
			log.info("CSV file does not exist: {}", file);
			return new ArrayList<>();
		}

		log.info("Reading CSV from: {}", file);
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
		} catch (IOException e) {
			throw new StorageException("Failed to check file " + file + ": " + e.getMessage(), e);
		}
		List<DataRecord> records = new ArrayList<>();

		try (Reader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8);
	         CSVParser parser = CSVFormat.DEFAULT.builder()
		                            .setHeader()
		                            .setSkipHeaderRecord(true)
		                            .build()
		                            .parse(reader)) {

			for (CSVRecord row : parser) {
				Map<String, Object> metrics = objectMapper.readValue(row.get("metrics"), new TypeReference<>() {});

				String observedAtStr = row.get("observed_at");
				Optional<Instant> observedAt = observedAtStr.isEmpty()
					? Optional.empty()
					: Optional.of(Instant.parse(observedAtStr));

				records.add(new DataRecord(
					row.get("source"),
					row.get("entity_id"),
					observedAt,
					metrics
				));
			}

			return records;

		} catch (IOException | IllegalArgumentException e) {
			throw new StorageException("Failed to read CSV from " + file + ": " + e.getMessage(), e);
		}
	}
}
