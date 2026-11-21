package dev.malashenko.aggregator.storage;

import dev.malashenko.aggregator.model.DataRecord;
import dev.malashenko.aggregator.model.WriteMode;

import java.io.IOException;
import java.util.List;

public interface DataStorage {
	void save(List<DataRecord> records, WriteMode mode) throws IOException;

	List<DataRecord> readAll() throws IOException;

	default List<DataRecord> readBySource(String source) throws IOException {
		if (source == null) {
			return readAll();
		}

		return readAll().stream()
			       .filter(record -> source.equals(record.source()))
			       .toList();
	}
}
