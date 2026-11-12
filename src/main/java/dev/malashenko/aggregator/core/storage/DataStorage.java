package dev.malashenko.aggregator.core.storage;

import dev.malashenko.aggregator.core.model.DataRecord;

import java.nio.file.Path;
import java.util.List;

public interface DataStorage {
	void saveData(List<DataRecord> data, Path file);
	List<DataRecord> readData(Path file);
}
