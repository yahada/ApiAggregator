package dev.malashenko.aggregator.core.source;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.malashenko.aggregator.core.exception.AppException;
import dev.malashenko.aggregator.core.http.HttpResponder;
import dev.malashenko.aggregator.core.tools.ObjectMapperBuilder;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class ApiSourceRegistry {

	public static final Set<String> SOURCE_NAMES = Set.of("usgs", "hackernews", "kitsu");
	private ApiSourceRegistry() {}


	public static Map<String, ApiSource> select(HttpResponder httpResponder, List<String> names, ObjectMapper objectMapper) {
		Map<String, ApiSource> selected = new LinkedHashMap<>();
		for (String name : names) {
			selected.put(name, createSource(name, httpResponder, objectMapper));
		}
		return selected;
	}

	private static ApiSource createSource(String name, HttpResponder httpResponder, ObjectMapper objectMapper) {
		return switch (name) {
			case "usgs" -> new EarthquakeUsgsHttpApiSource(httpResponder, objectMapper);
			case "hackernews" -> new HackerNewsHttpApiSource(httpResponder, objectMapper);
			case "kitsu" -> new KitsuHttpApiSource(httpResponder, objectMapper);
			default -> throw new AppException("Unknown source: '" + name + "'");
		};
	}
}
