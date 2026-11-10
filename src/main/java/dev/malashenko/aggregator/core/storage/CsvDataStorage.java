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
	private final ObjectMapper objectMapper;
	private static final String[] HEADER = {"source", "entity_type", "entity_id", "observed_at", "metrics"};

	public CsvDataStorage(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}


	@Override
	public void saveData(List<DataRecord> data, WriteMode mode, Path path) {

		boolean writeHeader = (mode == WriteMode.NEW) || !Files.exists(path);

		StandardOpenOption openOption = (mode ==  WriteMode.NEW) ? StandardOpenOption.TRUNCATE_EXISTING : StandardOpenOption.APPEND;
		StandardOpenOption[] options = new StandardOpenOption[]{StandardOpenOption.CREATE, openOption, StandardOpenOption.WRITE};

		try (Writer writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8, options);
		     CSVPrinter printer = new CSVPrinter(writer, CSVFormat.DEFAULT.builder()
			                                                 .setHeader(writeHeader ? HEADER : null)
			                                                 .build())) {

			for (DataRecord record : data) {
				printer.printRecord(
					record.source(),
					record.entityType().name(),
					record.entityId(),
					record.observedAt().toString(),
					objectMapper.writeValueAsString(record.metrics()));
			}
		} catch (IOException e) {
			throw new StorageException("Failed to write CSV to " + path + ": " + e.getMessage(), e);
		}
	}

	@Override
	public List<DataRecord> readData(Path path) {
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
					new TypeReference<Map<String, Object>>() {});

				records.add(new DataRecord(
					row.get("source"),
					EntityType.valueOf(row.get("entity_type")),
					row.get("entity_id"),
					Instant.parse(row.get("observed_at")),
					metrics));
			}
			return records;
		} catch (IOException e) {
			throw new StorageException("Failed to read CSV from " + path + ": " + e.getMessage(), e);
		}

	}

	@Override
	public List<DataRecord> readByApi(EntityType apiType, Path path) {
		return readData(path).stream().filter(dataRecord -> dataRecord.entityType().equals(apiType)).toList();
	}

	@Override
	public StorageFormat getStorageFormat() {
		return StorageFormat.CSV;
	}
}
