package dev.malashenko.aggregator.factory;

import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.extern.slf4j.Slf4j;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import dev.malashenko.aggregator.aggregator.Aggregator;
import dev.malashenko.aggregator.http.HttpResponder;
import dev.malashenko.aggregator.source.ApiSourceRegistry;
import dev.malashenko.aggregator.storage.CsvStorage;
import dev.malashenko.aggregator.storage.DataStorage;
import dev.malashenko.aggregator.storage.JsonStorage;

import java.net.http.HttpClient;
import java.nio.file.Path;
import java.util.Locale;

@Slf4j
public class AppFactory {
	private final HttpResponder httpResponder;
	private final ObjectMapper objectMapper = new ObjectMapper()
		                                          .registerModule(new JavaTimeModule())
		                                          .registerModule(new Jdk8Module())
		                                          .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

	public AppFactory(HttpClient client) {
		this.httpResponder = new HttpResponder(client);
	}

	public Aggregator createAggregator() {
		ApiSourceRegistry registry = new ApiSourceRegistry(httpResponder, objectMapper);
		return new Aggregator(registry);
	}

	public DataStorage createStorage(String format, Path path) {
		return switch (format.toLowerCase(Locale.ROOT)) {
			case "json" -> new JsonStorage(path, objectMapper);
			case "csv" -> new CsvStorage(path, objectMapper);
			default -> throw new IllegalArgumentException("Unsupported format: " + format);
		};
	}
}
