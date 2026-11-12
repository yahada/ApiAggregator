package dev.malashenko.aggregator.core.source;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.malashenko.aggregator.core.http.HttpResponder;
import dev.malashenko.aggregator.core.model.EntityType;

public interface ApiSourceFactory {

	EntityType getEntityType();

	ApiSource create(HttpResponder httpResponder, ObjectMapper objectMapper);
}