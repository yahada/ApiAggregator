package dev.malashenko.aggregator.core.aggregator;

import dev.malashenko.aggregator.core.model.DataRecord;
import dev.malashenko.aggregator.core.storage.DataStorage;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.Path;
import java.util.List;
import java.util.Objects;

@Slf4j
public class ShowAggregator {

	private final DataStorage dataStorage;
	private final Path outputPath;

	public ShowAggregator(DataStorage dataStorage, Path outputPath) {
		this.dataStorage = Objects.requireNonNull(dataStorage, "dataStorage must not be null");
		this.outputPath = Objects.requireNonNull(outputPath, "outputPath must not be null");
	}

	public List<DataRecord> readData(String sourceName) {
		log.info("Reading data from {} for source: {}", outputPath, sourceName);
		List<DataRecord> records = dataStorage.readData(outputPath);

		if (sourceName == null) {
			log.info("Found {} total records", records.size());
			return records;
		}

		List<DataRecord> filtered = records.stream()
			   .filter(record -> record.source().equals(sourceName))
			   .toList();
		log.info("Found {} records from source {}", filtered.size(), sourceName);
		return filtered;
	}
}
