package dev.malashenko.aggregator.aggregator;

import dev.malashenko.aggregator.source.HttpApiSource;
import lombok.extern.slf4j.Slf4j;

import dev.malashenko.aggregator.model.DataRecord;
import dev.malashenko.aggregator.source.ApiSourceRegistry;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class Aggregator {
	private final ApiSourceRegistry registry;

	public Aggregator(ApiSourceRegistry registry) {
		this.registry = registry;
	}

	public List<DataRecord> aggregate(List<String> sourceNames) throws IOException, InterruptedException {
		List<DataRecord> result = new ArrayList<>();

		for (String name : sourceNames) {
			HttpApiSource source = registry.get(name);
			log.debug("Fetching data from source: {}", name);
			List<DataRecord> records = source.fetch(source.defaultParams());
			log.info("Source {} returned {} records", name, records.size());
			result.addAll(records);
		}

		log.info("Total aggregated records: {}", result.size());
		return result;
	}
}
