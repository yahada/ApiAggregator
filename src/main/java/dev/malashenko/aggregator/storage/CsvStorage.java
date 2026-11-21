package dev.malashenko.aggregator.storage;

import lombok.extern.slf4j.Slf4j;

import dev.malashenko.aggregator.model.WriteMode;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.malashenko.aggregator.model.DataRecord;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
public class CsvStorage implements DataStorage {
	private static final List<String> HEADERS = List.of(
		"source", "entityId", "timestamp", "metrics"
	);

	private final Path path;
	private final ObjectMapper objectMapper;

	public CsvStorage(Path path, ObjectMapper objectMapper) {
		this.path = path;
		this.objectMapper = objectMapper;
	}

	@Override
	public void save(List<DataRecord> records, WriteMode mode) throws IOException {
		List<DataRecord> data = new ArrayList<>();

		if (mode == WriteMode.APPEND) {
			data.addAll(readAll());
		}

		data.addAll(records);

		Path parent = path.toAbsolutePath().getParent();

		if (parent != null) {
			Files.createDirectories(parent);
		}

		CSVFormat format = CSVFormat.DEFAULT.builder()
			                   .setHeader(HEADERS.toArray(new String[0]))
			                   .build();

		try (Writer writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8);
		     CSVPrinter printer = new CSVPrinter(writer, format)) {

			for (DataRecord record : data) {
				String metricsJson = objectMapper.writeValueAsString(record.metrics());
				printer.printRecord(
					record.source(),
					record.entityId(),
					record.timestamp().toString(),
					metricsJson
				);
			}
		}
		log.info("Saved {} records to CSV: {}", data.size(), path);
	}

	@Override
	public List<DataRecord> readAll() throws IOException {
		if (Files.notExists(path) || Files.size(path) == 0) {
			log.debug("CSV file not found or empty: {}", path);
			return List.of();
		}

		CSVFormat format = CSVFormat.DEFAULT.builder()
			                   .setHeader()
			                   .setSkipHeaderRecord(true)
			                   .build();

		List<DataRecord> records = new ArrayList<>();

		try (Reader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8);
		     CSVParser parser = new CSVParser(reader, format)) {
			if (!parser.getHeaderNames().equals(HEADERS)) {
				throw new IOException("Invalid CSV headers");
			}

			for (CSVRecord row : parser) {
				if (row.size() != HEADERS.size()) {
					throw new IOException("Invalid CSV record");
				}

				String metricsJson = row.get("metrics");
				Map<String, Object> metrics = objectMapper.readValue(metricsJson, new TypeReference<>() {});

				records.add(new DataRecord(
					row.get("source"),
					row.get("entityId"),
					Instant.parse(row.get("timestamp")),
					metrics
				));
			}
		}

		log.info("Read {} records from CSV: {}", records.size(), path);
		return records;
	}
}
