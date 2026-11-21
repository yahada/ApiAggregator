package dev.malashenko.aggregator.source;

import lombok.extern.slf4j.Slf4j;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.malashenko.aggregator.http.HttpResponder;

import java.util.Map;

@Slf4j
public class ApiSourceRegistry {
	private final Map<String, HttpApiSource> sources;

	public ApiSourceRegistry(HttpResponder httpResponder, ObjectMapper objectMapper) {
		this.sources = Map.of(
			"usgs", new EarthquakeUsgsHttpApiSource(httpResponder, objectMapper),
			"hackernews", new HackerNewsHttpApiSource(httpResponder, objectMapper),
			"kitsu", new KitsuHttpApiSource(httpResponder, objectMapper)
		);
	}

	public HttpApiSource get(String name) {
		HttpApiSource source = sources.get(name);
		if (source == null) {
			throw new IllegalArgumentException("Unknown source: " + name);
		}
		return source;
	}
}
