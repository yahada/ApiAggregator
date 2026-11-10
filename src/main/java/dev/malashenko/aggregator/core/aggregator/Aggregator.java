package dev.malashenko.aggregator.core.aggregator;


import dev.malashenko.aggregator.core.model.DataRecord;
import dev.malashenko.aggregator.core.model.EntityType;
import dev.malashenko.aggregator.core.model.RequestParams;
import dev.malashenko.aggregator.core.source.ApiSource;

import dev.malashenko.aggregator.core.storage.DataStorage;
import dev.malashenko.aggregator.core.storage.WriteMode;
import dev.malashenko.aggregator.core.tools.FileExtension;

import java.nio.file.Path;
import java.util.*;

public class Aggregator {
	private final DataStorage dataStorage;
	private final WriteMode writeMode;
	private final Map<EntityType, ApiSource> apiSourceMap;
	private final String filename;

	Aggregator(DataStorage dataStorage, WriteMode writeMode, Map<EntityType, ApiSource> apiSourceMap, String filename) {
		this.dataStorage = dataStorage;
		this.writeMode = writeMode;
		//TODO: CHECK FILE EXTENSION
		if (filename == null) {
			this.filename = "data." + FileExtension.formatOf(dataStorage.getStorageFormat());
		} else if (FileExtension.extension(filename).isEmpty()) {
			this.filename = filename + "." + FileExtension.formatOf(dataStorage.getStorageFormat());
		} else {
			this.filename = filename;
		}
		this.apiSourceMap = apiSourceMap;
	}

	public List<ApiSource> getApiSources() {
		return (List<ApiSource>) apiSourceMap.values();
	}

	public void makeRequest(EntityType apiType, RequestParams params) {
		List<DataRecord> records = apiSourceMap.get(apiType).getApiRequest(params);
		dataStorage.saveData(records, writeMode, Path.of(filename));
	}

	public void readData() {
		List<DataRecord> records = dataStorage.readData(Path.of(filename));
		for (DataRecord record: records) {
			System.out.println(record);
		}
	}

	public void readByApi(EntityType apiType) {
		List<DataRecord> records = dataStorage.readData(Path.of(filename));
		List<DataRecord> apiRecords = records.stream().filter(record -> record.entityType().equals(apiType)).toList();
		for (DataRecord record: apiRecords) {
			System.out.println(record);
		}
	}
}
