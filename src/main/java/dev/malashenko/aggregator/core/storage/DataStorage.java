package dev.malashenko.aggregator.core.storage;

import dev.malashenko.aggregator.core.model.DataRecord;

import java.util.List;

public interface DataStorage {

	void saveData(List<DataRecord> data);

	List<DataRecord> readData();

	List<DataRecord> readData(String source);
}
