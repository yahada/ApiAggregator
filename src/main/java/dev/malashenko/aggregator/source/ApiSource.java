package dev.malashenko.aggregator.source;

import dev.malashenko.aggregator.model.DataRecord;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface ApiSource {
	Map<String, String> defaultParams();
	List<DataRecord> fetch(Map<String, String> parameters) throws IOException, InterruptedException;
	List<DataRecord> map(String json) throws IOException;

}
