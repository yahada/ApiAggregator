package dev.malashenko.aggregator.core.storage;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.malashenko.aggregator.core.model.DataRecord;
import dev.malashenko.aggregator.core.model.EntityType;

import java.lang.runtime.ObjectMethods;
import java.nio.file.Path;
import java.util.List;

public interface DataStorage {
	public void saveData(List<DataRecord> data, Path file);
	public StorageFormat getStorageFormat();
	public List<DataRecord> readData(Path file);
	public List<DataRecord> readByApi(EntityType apiType, Path file);

}
