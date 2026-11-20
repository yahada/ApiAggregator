package dev.malashenko.aggregator.source;

import lombok.extern.slf4j.Slf4j;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.malashenko.aggregator.http.HttpResponder;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class ApiSourceRegistry {
	private final Map<String, ApiSource> sources = new ConcurrentHashMap<>();

	public ApiSourceRegistry(HttpResponder httpResponder, ObjectMapper objectMapper) {
		sources.put("usgs", new EarthquakeUsgsHttpApiSource(httpResponder, objectMapper));
		sources.put("hackernews", new HackerNewsHttpApiSource(httpResponder, objectMapper));
		sources.put("kitsu", new KitsuHttpApiSource(httpResponder, objectMapper));
	}

	public ApiSource get(String name) {
		ApiSource source = sources.get(name);
		if (source == null) {
			throw new IllegalArgumentException("Unknown source: " + name);
		}
		return source;
	}
}
