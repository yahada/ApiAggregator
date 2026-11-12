package dev.malashenko.aggregator.core.model;

import java.util.Map;

public final class SourceTypeRegistry {

	private static final Map<String, EntityType> SOURCE_TO_TYPE = Map.of(
		"kitsu", EntityType.ANIME,
		"usgs", EntityType.EARTHQUAKE,
		"hackernews", EntityType.NEWS
	);

	private SourceTypeRegistry() {}


	public static EntityType of(String sourceName) {
		if (sourceName == null) {
			return null;
		}
		return SOURCE_TO_TYPE.get(sourceName.toLowerCase());
	}


	public static java.util.Set<String> sourceNames() {
		return SOURCE_TO_TYPE.keySet();
	}
}
