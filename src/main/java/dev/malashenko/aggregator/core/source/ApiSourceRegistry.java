package dev.malashenko.aggregator.core.source;

import dev.malashenko.aggregator.core.exception.AppException;
import dev.malashenko.aggregator.core.http.HttpResponder;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class ApiSourceRegistry {

	public static final Set<String> SOURCE_NAMES = Set.of("usgs", "hackernews", "kitsu");

	private ApiSourceRegistry() {}


	public static Map<String, ApiSource> select(HttpResponder httpResponder, List<String> names) {
		Map<String, ApiSource> selected = new LinkedHashMap<>();
		for (String name : names) {
			selected.put(name, createSource(name, httpResponder));
		}
		return selected;
	}

	private static ApiSource createSource(String name, HttpResponder httpResponder) {
		return switch (name) {
			case "usgs" -> new EarthquakeUsgsHttpApiSource(httpResponder);
			case "hackernews" -> new HackerNewsHttpApiSource(httpResponder);
			case "kitsu" -> new KitsuHttpApiSource(httpResponder);
			default -> throw new AppException("Unknown source: '" + name + "'");
		};
	}
}
