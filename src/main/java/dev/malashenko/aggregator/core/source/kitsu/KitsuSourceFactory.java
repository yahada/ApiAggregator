package dev.malashenko.aggregator.core.source.kitsu;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.malashenko.aggregator.core.http.HttpResponder;
import dev.malashenko.aggregator.core.model.EntityType;
import dev.malashenko.aggregator.core.source.ApiSource;
import dev.malashenko.aggregator.core.source.ApiSourceFactory;

public class KitsuSourceFactory implements ApiSourceFactory {

	@Override
	public EntityType getEntityType() {
		return EntityType.ANIME;
	}

	@Override
	public ApiSource create(HttpResponder httpResponder, ObjectMapper objectMapper) {
		return new KitsuHttpApiSource(httpResponder, new KitsuMapper(objectMapper));
	}
}