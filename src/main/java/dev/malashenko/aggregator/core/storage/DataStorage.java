package dev.malashenko.aggregator.core.storage;

import dev.malashenko.aggregator.core.model.DataRecord;

import java.util.List;

public interface DataStorage {
	public void saveData(List<DataRecord> data, WriteMode mode, String filename);
	public void saveData(List<DataRecord> data, WriteMode mode);
}
