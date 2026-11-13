package dev.malashenko.aggregator.core.source;

import dev.malashenko.aggregator.core.http.HttpResponder;

import java.util.Map;
import java.util.Set;

public final class ApiSourceRegistry {

	public static final Set<String> SOURCE_NAMES = Set.of("usgs", "hackernews", "kitsu");

	private ApiSourceRegistry() {}


	public static Map<String, ApiSource> createAll(HttpResponder httpResponder) {
		return Map.of(
			"usgs", new EarthquakeUsgsHttpApiSource(httpResponder),
			"hackernews", new HackerNewsHttpApiSource(httpResponder),
			"kitsu", new KitsuHttpApiSource(httpResponder)
		);
	}
}
