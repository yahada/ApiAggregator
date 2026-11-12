package dev.malashenko.aggregator.core.source.hackernews;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.malashenko.aggregator.core.http.HttpResponder;
import dev.malashenko.aggregator.core.model.EntityType;
import dev.malashenko.aggregator.core.source.ApiSource;
import dev.malashenko.aggregator.core.source.ApiSourceFactory;

public class HackerNewsSourceFactory implements ApiSourceFactory {

	@Override
	public EntityType getEntityType() {
		return EntityType.NEWS;
	}

	@Override
	public ApiSource create(HttpResponder httpResponder, ObjectMapper objectMapper) {
		return new HackerNewsHttpApiSource(httpResponder, new HackerNewsMapper(objectMapper));
	}
}