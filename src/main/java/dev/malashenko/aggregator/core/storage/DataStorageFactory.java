package dev.malashenko.aggregator.core.storage;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public class DataStorageFactory {

	public static DataStorage getDataStorage(StorageFormat format, ObjectMapper om) {
		return switch (format) {
			case CSV -> new CsvDataStorage(om);
			case JSON -> new JsonDataStorage(om);
		};
	}

	public static ObjectMapper defaultMapper() {
		return new ObjectMapper()
			       .registerModule(new JavaTimeModule())
			       .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
	}

}


